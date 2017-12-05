package com.github.thomasfox.sailboatcalculator.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.github.thomasfox.sailboatcalculator.boat.Boat;
import com.github.thomasfox.sailboatcalculator.boat.impl.Flying29er;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.CalculatedPhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.calculate.value.ValueSet;
import com.github.thomasfox.sailboatcalculator.progress.CalculationState;

public class SwingGui
{
  public static final File PROFILE_DIRECTORY = new File("profiles");

  public static final File HULL_DIRECTORY = new File("hulls");

  private final JFrame frame = new JFrame("wingCalculator");

  private JPanel inputPanel;

  private JPanel singleResultPanel;

  private final List<PartInput> valueSetInputs = new ArrayList<>();

  private final List<PartOutput> valueSetOutputs = new ArrayList<>();

  private JPanel chartsPanel;

  private final List<ChartPanel> chartPanels = new ArrayList<>();

  private final JButton calculateButton;

  private final JButton scanButton;

  private final Boat boat = new Flying29er();

  public SwingGui()
  {
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new GridBagLayout());

    addInputPanel();
    addSingleResultsPanel();
    addChartsPanel();
    addCalculationStateDisplay();

    for (ValueSet valueSet : boat.getValueSets())
    {
      createPartInput(valueSet);
    }

    int row = 0;
    for (PartInput partInput : valueSetInputs)
    {
      row += partInput.addToContainerInRow(inputPanel, row);
    }

    SwingHelper.addSeparatorToContainer(inputPanel, row++, 5);

    calculateButton = new JButton("Berechnen");
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = row;
    calculateButton.addActionListener(this::calculateButtonPressed);
    inputPanel.add(calculateButton, gridBagConstraints);

    scanButton = new JButton("Diagramme anzeigen");
    scanButton.addActionListener(this::scanButtonPressed);
    scanButton.setVisible(false);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = row;
    inputPanel.add(scanButton, gridBagConstraints);

    frame.pack();
    frame.setVisible(true);
  }

  private void addInputPanel()
  {
    inputPanel = new JPanel();
    inputPanel.setLayout(new GridBagLayout());
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    frame.add(inputPanel, gridBagConstraints);
  }

  private void addSingleResultsPanel()
  {
    singleResultPanel = new JPanel();
    singleResultPanel.setLayout(new GridBagLayout());
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    JScrollPane scrollPane = new JScrollPane(singleResultPanel);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setPreferredSize(new Dimension(600, 400));
    frame.add(scrollPane, gridBagConstraints);
    singleResultPanel.setBorder(new EmptyBorder(0,10,0,10));
  }

  private void addChartsPanel()
  {
    chartsPanel = new JPanel();
    chartsPanel.setLayout(new GridBagLayout());
    JScrollPane scrollPane = new JScrollPane(chartsPanel);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setPreferredSize(new Dimension(600, 400));
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    frame.add(scrollPane, gridBagConstraints);
  }

  private void addCalculationStateDisplay()
  {
    CalculationStateDisplay calculationStateDisplay = new CalculationStateDisplay();
    CalculationState.register(calculationStateDisplay);
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 3;
    frame.add(calculationStateDisplay, gridBagConstraints);
  }

  private void createPartInput(ValueSet valueSet)
  {
    PartInput partInput = new PartInput(valueSet);
    valueSetInputs.add(partInput);
    for (PhysicalQuantity physicalQuantity : valueSet.getToInput())
    {
      if (valueSet.getFixedValue(physicalQuantity) == null)
      {
        PhysicalQuantityValue startValue = valueSet.getStartValue(physicalQuantity);
        if (startValue == null)
        {
          partInput.add(new QuantityInput(physicalQuantity));
        }
        else
        {
          partInput.add(new QuantityInput(startValue));
        }
      }
    }
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

  private List<QuantityInput> getScannedInputs()
  {
    List<QuantityInput> scannedInputs = new ArrayList<>();
    for (PartInput valueSetInput : valueSetInputs)
    {
      scannedInputs.addAll(valueSetInput.getScannedQuantityInputs());
    }
    return scannedInputs;
  }

  private Map<PartOutput, List<QuantityOutput>> getShownGraphs()
  {
    Map<PartOutput, List<QuantityOutput>> result = new HashMap<>();
    for (PartOutput partOutput : valueSetOutputs)
    {
      List<QuantityOutput> shownGraphsInValueSet = partOutput.getShownGraphs();
      if (!shownGraphsInValueSet.isEmpty())
      {
        result.put(partOutput, shownGraphsInValueSet);
      }
    }
    return result;
  }

  private void calculateAndRefreshDisplayedResults()
  {
    clearResult();
    reinitalizeValueSetInputs();
    boat.calculate();
    CalculationState.clear();

    QuantityOutput.Mode mode;
    List<QuantityInput> scannedInputs = getScannedInputs();
    if (scannedInputs.isEmpty())
    {
      mode = QuantityOutput.Mode.NUMERIC_DISPLAY;
    }
    else
    {
      mode = QuantityOutput.Mode.CHECKBOX_DISPLAY;
    }

    int row = displayCalculateResultInValueSetOutputs(mode);

    if (!scannedInputs.isEmpty())
    {
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = row + 1;
      scanButton.setVisible(true);
    }
    else
    {
      scanButton.setVisible(false);
    }
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
    Map<PartOutput, List<QuantityOutput>> shownGraphs = getShownGraphs();

    clearCharts();

    List<QuantityInput> scannedInputs = getScannedInputs();
    if (scannedInputs.size() > 1)
    {
      throw new IllegalArgumentException("Can only handle one scanned input");
    }
    QuantityInput scannedInput = scannedInputs.get(0);

    Map<QuantityOutput, XYSeries> quantitySeries = new HashMap<>();
    for (Map.Entry<PartOutput, List<QuantityOutput>> shownGraphsPart: shownGraphs.entrySet())
    {
      for (QuantityOutput shownGraph : shownGraphsPart.getValue())
      {
        XYSeries series = new XYSeries(shownGraph.getQuantity().getDisplayName(), false, true);
        quantitySeries.put(shownGraph, series);
      }
    }

    for (int i = 0; i < scannedInput.getNumberOfScanSteps(); ++i)
    {
      double xValue = scannedInput.getScanStepValue(i);
      CalculationState.set("scan:" + scannedInput.getQuantity().toString(), xValue);
      scannedInput.setValue(xValue);
      reinitalizeValueSetInputs();
      boat.calculate();
      for (Map.Entry<PartOutput, List<QuantityOutput>> shownGraphsPart: shownGraphs.entrySet())
      {
        for (QuantityOutput shownGraph : shownGraphsPart.getValue())
        {
          ValueSet valueSet = boat.getValueSetNonNull(shownGraphsPart.getKey().getId());
          PhysicalQuantityValue knownValue = valueSet.getKnownValue(shownGraph.getQuantity());
          if (knownValue != null)
          {
            double yValue = knownValue.getValue();
            quantitySeries.get(shownGraph).add(xValue, yValue);
          }
        }
      }
    }
    CalculationState.clear();
    int row = 0;
    for (Map.Entry<QuantityOutput, XYSeries> seriesEntry : quantitySeries.entrySet())
    {
      String seriesDisplayName = seriesEntry.getKey().getQuantity().getDisplayName();
      JFreeChart chart;
      if ("�".equals(scannedInput.getQuantity().getUnit()))
      {
        XYSeriesCollection seriesCollection = new XYSeriesCollection();
        seriesCollection.addSeries(seriesEntry.getValue());
        chart = ChartFactory.createPolarChart(seriesDisplayName, seriesCollection, false, true, false);
      }
      else
      {
        DefaultXYDataset dataset = new DefaultXYDataset();
        dataset.addSeries(seriesDisplayName, seriesEntry.getValue().toArray());
        chart = ChartFactory.createXYLineChart(
            seriesDisplayName,
            scannedInput.getQuantity().getDisplayNameIncludingUnit(),
            PhysicalQuantity.VELOCITY.getDisplayNameIncludingUnit(),
            dataset);
      }
      ChartPanel chartPanel = new ChartPanel(chart);
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = GridBagConstraints.BOTH;
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = row++;
      chartsPanel.add(chartPanel);
      chartPanels.add(chartPanel);
    }
  }

  private int displayCalculateResultInValueSetOutputs(QuantityOutput.Mode mode)
  {
    int outputRow = 0;
    for (PartInput valueSetInput : valueSetInputs)
    {
      PartOutput partOutput = new PartOutput(valueSetInput.getValueSet().getId(), valueSetInput.getValueSet().getName());
      valueSetOutputs.add(partOutput);
      for (CalculatedPhysicalQuantityValue calculatedValue : valueSetInput.getValueSet().getCalculatedValues().getAsList())
      {
        if (valueSetInput.getValueSet().getHiddenOutputs().contains(calculatedValue.getPhysicalQuantity()))
        {
          continue;
        }
        QuantityOutput output = new QuantityOutput(calculatedValue);
        partOutput.add(output);
      }
      outputRow += partOutput.addToContainerInRow(singleResultPanel, outputRow, mode);
    }
    return outputRow;
  }

  private void reinitalizeValueSetInputs()
  {
    for (PartInput valueSetInput : valueSetInputs)
    {
      valueSetInput.getValueSet().clearCalculatedValues();
      valueSetInput.applyStartValues();
      valueSetInput.applyProfile();
    }
  }

  private void clearResult()
  {
    clearCharts();
    clearSingleResult();
  }

  private void clearCharts()
  {
    for (ChartPanel chartPanel : chartPanels)
    {
      chartsPanel.remove(chartPanel);
    }
    chartPanels.clear();
  }

  private void clearSingleResult()
  {
    for (PartOutput partOutput : valueSetOutputs)
    {
      partOutput.removeFromContainerAndReset(singleResultPanel);
    }
    valueSetOutputs.clear();
    singleResultPanel.remove(scanButton);
  }
}
