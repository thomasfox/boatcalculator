package com.github.thomasfox.boatcalculator.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jfree.data.xy.XYSeries;

import com.github.thomasfox.boatcalculator.boat.Boat;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.gui.model.ScanResult;
import com.github.thomasfox.boatcalculator.gui.model.ScanResultForSingleQuantity;
import com.github.thomasfox.boatcalculator.gui.panel.CalculationStateDisplay;
import com.github.thomasfox.boatcalculator.gui.panel.ChartsPanel;
import com.github.thomasfox.boatcalculator.gui.panel.InputPanel;
import com.github.thomasfox.boatcalculator.gui.panel.TextResultPanel;
import com.github.thomasfox.boatcalculator.gui.panel.part.ProfileInput;
import com.github.thomasfox.boatcalculator.gui.panel.part.QuantityInput;
import com.github.thomasfox.boatcalculator.gui.panel.part.QuantityOutput;
import com.github.thomasfox.boatcalculator.gui.panel.part.ScannedInput;
import com.github.thomasfox.boatcalculator.progress.CalculationState;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

public class SwingGui
{
  public static final File PROFILE_DIRECTORY = new File("profiles");

  public static final File HULL_DIRECTORY = new File("hulls");

  private final Menubar menubar = new Menubar(this::boatTypeSelected);

  private Boat boat = menubar.getSelectedBoat();

  private final JFrame frame = new JFrame("wingCalculator");

  private final InputPanel inputPanel = new InputPanel(boat.getValueSets());

  private final TextResultPanel textResultPanel = new TextResultPanel();

  private final ChartsPanel chartsPanel= new ChartsPanel();

  private CalculationStateDisplay calculationStateDisplay;

  public SwingGui()
  {
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setJMenuBar(menubar);

    frame.getContentPane().setLayout(new GridBagLayout());

    inputPanel.addToFrame(frame);
    textResultPanel.addToFrame(frame);
    chartsPanel.addToFrame(frame);
    addCalculationStateDisplay();

    inputPanel.addCalculateButtonActionListener(this::calculateButtonPressed);
    inputPanel.addScanButtonActionListener(this::scanButtonPressed);
    inputPanel.addSaveResultsButtonActionListener(this::saveResultsButtonPressed);

    frame.pack();
    frame.setVisible(true);
  }

  private void boatTypeSelected(Boat boat)
  {
    this.boat = boat;
    clearResult();
    inputPanel.reset(boat.getValueSets());
  }

  private void addCalculationStateDisplay()
  {
    calculationStateDisplay = new CalculationStateDisplay();
    CalculationState.register(calculationStateDisplay);
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 3;
    frame.add(calculationStateDisplay, gridBagConstraints);
  }


  public static void main(String[] args)
  {
    javax.swing.SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run() {
        new SwingGui();
      }
    });
  }

  public void calculateButtonPressed(ActionEvent e)
  {
    Runnable calculateRunnable = new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          calculateAndRefreshDisplayedResults();
        }
        catch (Exception e)
        {
          e.printStackTrace();
          JOptionPane.showMessageDialog(frame, "Exception occured:\n" + e.getMessage());
        }
        frame.revalidate();
      }
    };
    new Thread(calculateRunnable).start();
  }

  public void saveResultsButtonPressed(ActionEvent e)
  {
    ScanResult scanResult = chartsPanel.getScanResult();
    if (scanResult == null)
    {
      throw new IllegalStateException("no scan Result available");
    }
    File file = new File("results.csv");
    scanResult.exportToCsv(file);
  }


  private void calculateAndRefreshDisplayedResults()
  {
    clearResult();
    inputPanel.reinitalizeValueSetInputs();
    boat.calculate();

    calculationStateDisplay.clear();

    QuantityOutput.Mode mode = inputPanel.getOutputMode();
    textResultPanel.displayCalculateResultInValueSetOutputs(mode, boat.getValueSets());

    if (mode == QuantityOutput.Mode.CHECKBOX_DISPLAY)
    {
      inputPanel.setScanButtonVisible(true);
      inputPanel.setSaveResultsButtonVisible(true);
    }
    else
    {
      inputPanel.setScanButtonVisible(false);
      inputPanel.setSaveResultsButtonVisible(false);
    }
  }

  private void clearResult()
  {
    chartsPanel.clear();
    textResultPanel.clear();
  }

  public void scanButtonPressed(ActionEvent e)
  {
    Runnable calculateRunnable = new Runnable()
    {
      @Override
      public void run()
      {
        calculateAndRefreshDisplayedResultsScan();
        frame.revalidate();
      }
    };
    new Thread(calculateRunnable).start();
  }

  private void calculateAndRefreshDisplayedResultsScan()
  {
    chartsPanel.clear();

    Set<PhysicalQuantityInSet> shownGraphs = textResultPanel.getShownGraphs();
    Map<String, ProfileInput> profileInput = inputPanel.getScannedProfileInputs();
    if (profileInput.size() > 0)
    {
      shownGraphs.add(new PhysicalQuantityInSet(
          PhysicalQuantity.PROFILE,
          profileInput.keySet().iterator().next()));
    }
    QuantityInput scannedQuantityInput = inputPanel.getScannedQuantityInput();
    ProfileInput scannedProfileInput = inputPanel.getScannedProfileInput();

    ScanResult scanResult;
    if (scannedQuantityInput != null)
    {
      scanResult = calculateQuantitySeriesForSelectedOutputs(shownGraphs, scannedQuantityInput);
    }
    else
    {
      scanResult = calculateQuantitySeriesForSelectedOutputs(shownGraphs, scannedProfileInput);
    }

    calculationStateDisplay.clear();
    chartsPanel.display(scanResult);
  }

  private ScanResult calculateQuantitySeriesForSelectedOutputs(
      Set<PhysicalQuantityInSet> shownGraphs,
      ScannedInput scannedInput)
  {
    Map<PhysicalQuantityInSet, XYSeries> quantitySeries = new HashMap<>();
    PhysicalQuantityInSet profileQuantity = null;
    for (PhysicalQuantityInSet shownGraph : shownGraphs)
    {
      if (shownGraph.getPhysicalQuantity() == PhysicalQuantity.PROFILE)
      {
        profileQuantity = shownGraph;
      }
      else
      {
        XYSeries series = new XYSeries(shownGraph.getPhysicalQuantity().getDisplayName(), false, true);
        quantitySeries.put(shownGraph, series);
      }
    }

    Map<Integer, String> profileNames = new HashMap<>();
    for (int i = 0; i < scannedInput.getNumberOfScanSteps(); ++i)
    {
      CalculationState.set(
          scannedInput.getScanDescription(),
          scannedInput.getScanDescription() + ":" + scannedInput.getScanStepDescription(i));
      scannedInput.setValueForScanStep(i);
      inputPanel.reinitalizeValueSetInputs();
      boat.calculate();
      for (PhysicalQuantityInSet shownGraph : shownGraphs)
      {
        ValueSet valueSet = boat.getValueSetNonNull(shownGraph.getSetId());
        PhysicalQuantityValue knownValue = valueSet.getKnownQuantityValue(shownGraph.getPhysicalQuantity());
        if (knownValue != null)
        {
          double yValue = knownValue.getValue();
          quantitySeries.get(shownGraph).add(scannedInput.getScanXValue(i), yValue);
        }
      }
      if (profileQuantity != null)
      {
        ValueSet valueSet = boat.getValueSetNonNull(profileQuantity.getSetId());
        profileNames.put(i, valueSet.getProfileName());
      }
    }
    ScanResult result = new ScanResult();
    for (Map.Entry<PhysicalQuantityInSet, XYSeries> singleSeries : quantitySeries.entrySet())
    {
      ScanResultForSingleQuantity resultEntry = new ScanResultForSingleQuantity(scannedInput.getQuantity(), singleSeries.getKey());
      resultEntry.setSeries(singleSeries.getValue());
      resultEntry.setDisplayName(getSeriesDisplayName(singleSeries.getKey()));
      result.getSingleQuantityScanResults().add(resultEntry);
    }

    result.setProfileNames(profileNames);
    return result;
  }


  private String getSeriesDisplayName(PhysicalQuantityInSet quantity)
  {
    String seriesDisplayName = boat.getValueSetNonNull(quantity.getSetId()).getDisplayName()
        + " " + quantity.getPhysicalQuantity().getDisplayName();
    return seriesDisplayName;
  }
}
