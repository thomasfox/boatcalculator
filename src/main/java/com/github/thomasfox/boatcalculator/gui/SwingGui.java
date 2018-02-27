package com.github.thomasfox.boatcalculator.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import org.jfree.data.xy.XYSeries;

import com.github.thomasfox.boatcalculator.boat.Boat;
import com.github.thomasfox.boatcalculator.gui.model.SingleScanResult;
import com.github.thomasfox.boatcalculator.gui.panel.CalculationStateDisplay;
import com.github.thomasfox.boatcalculator.gui.panel.ChartsPanel;
import com.github.thomasfox.boatcalculator.gui.panel.InputPanel;
import com.github.thomasfox.boatcalculator.gui.panel.TextResultPanel;
import com.github.thomasfox.boatcalculator.gui.panel.part.QuantityInput;
import com.github.thomasfox.boatcalculator.gui.panel.part.QuantityOutput;
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
        calculateAndRefreshDisplayedResults();
        frame.pack();
      }
    };
    new Thread(calculateRunnable).start();
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
    }
    else
    {
      inputPanel.setScanButtonVisible(false);
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
        frame.pack();
      }
    };
    new Thread(calculateRunnable).start();
  }

  private void calculateAndRefreshDisplayedResultsScan()
  {
    chartsPanel.clear();

    Set<PhysicalQuantityInSet> shownGraphs = textResultPanel.getShownGraphs();
    QuantityInput scannedInput = inputPanel.getScannedInput();

    List<SingleScanResult> scanResults
        = calculateQuantitySeriesForSelectedOutputs(shownGraphs, scannedInput);

    calculationStateDisplay.clear();
    chartsPanel.display(scanResults);
  }

  private List<SingleScanResult> calculateQuantitySeriesForSelectedOutputs(
      Set<PhysicalQuantityInSet> shownGraphs,
      QuantityInput scannedInput)
  {
    Map<PhysicalQuantityInSet, XYSeries> quantitySeries = new HashMap<>();
    for (PhysicalQuantityInSet shownGraph : shownGraphs)
    {
      XYSeries series = new XYSeries(shownGraph.getPhysicalQuantity().getDisplayName(), false, true);
      quantitySeries.put(shownGraph, series);
    }

    for (int i = 0; i < scannedInput.getNumberOfScanSteps(); ++i)
    {
      double xValue = scannedInput.getScanStepValue(i);
      CalculationState.set("scan:" + scannedInput.getQuantity().toString(), xValue);
      scannedInput.setValue(xValue);
      inputPanel.reinitalizeValueSetInputs();
      boat.calculate();
      for (PhysicalQuantityInSet shownGraph : shownGraphs)
      {
        ValueSet valueSet = boat.getValueSetNonNull(shownGraph.getValueSetId());
        PhysicalQuantityValue knownValue = valueSet.getKnownValue(shownGraph.getPhysicalQuantity());
        if (knownValue != null)
        {
          double yValue = knownValue.getValue();
          quantitySeries.get(shownGraph).add(xValue, yValue);
        }
      }
    }
    List<SingleScanResult> result = new ArrayList<>();
    for (Map.Entry<PhysicalQuantityInSet, XYSeries> singleSeries : quantitySeries.entrySet())
    {
      SingleScanResult resultEntry = new SingleScanResult(scannedInput.getQuantity(), singleSeries.getKey());
      resultEntry.setSeries(singleSeries.getValue());
      resultEntry.setDisplayName(getSeriesDisplayName(singleSeries.getKey()));
      result.add(resultEntry);
    }
    return result;
  }


  private String getSeriesDisplayName(PhysicalQuantityInSet quantity)
  {
    String seriesDisplayName = boat.getValueSetNonNull(quantity.getValueSetId()).getDisplayName()
        + " " + quantity.getPhysicalQuantity().getDisplayName();
    return seriesDisplayName;
  }
}
