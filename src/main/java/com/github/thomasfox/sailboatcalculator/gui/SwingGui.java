package com.github.thomasfox.sailboatcalculator.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.github.thomasfox.sailboatcalculator.boat.Boat;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.progress.CalculationState;
import com.github.thomasfox.sailboatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.sailboatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.valueset.ValueSet;
import com.github.thomasfox.sailboatcalculator.valueset.impl.BoatGlobalValues;
import com.github.thomasfox.sailboatcalculator.valueset.impl.Crew;
import com.github.thomasfox.sailboatcalculator.valueset.impl.DaggerboardOrKeel;
import com.github.thomasfox.sailboatcalculator.valueset.impl.Hull;
import com.github.thomasfox.sailboatcalculator.valueset.impl.MainLiftingFoil;
import com.github.thomasfox.sailboatcalculator.valueset.impl.Rigg;
import com.github.thomasfox.sailboatcalculator.valueset.impl.Rudder;

public class SwingGui
{
  public static final File PROFILE_DIRECTORY = new File("profiles");

  public static final File HULL_DIRECTORY = new File("hulls");

  private final Menubar menubar = new Menubar(this::boatTypeSelected);

  private Boat boat = menubar.getSelectedBoat();

  private final JFrame frame = new JFrame("wingCalculator");

  private final InputPanel inputPanel = new InputPanel(boat.getValueSets());

  private final TextResultPanel textResultPanel = new TextResultPanel();

  private JPanel chartsPanel;

  private final List<ChartPanel> chartPanels = new ArrayList<>();

  private CalculationStateDisplay calculationStateDisplay;

  private final Map<String, Set<PhysicalQuantityInSet>> graphSets = new HashMap<>();

  public SwingGui()
  {
    fillGraphSets();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setJMenuBar(menubar);

    frame.getContentPane().setLayout(new GridBagLayout());

    inputPanel.addToFrame(frame);
    textResultPanel.addToFrame(frame);
    addChartsPanel();
    addCalculationStateDisplay();

    inputPanel.addCalculateButtonActionListener(this::calculateButtonPressed);
    inputPanel.addScanButtonActionListener(this::scanButtonPressed);

    frame.pack();
    frame.setVisible(true);
  }

  private void fillGraphSets()
  {
    graphSets.clear();
    Set<PhysicalQuantityInSet> dragSet = new HashSet<>();
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, BoatGlobalValues.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, Rudder.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.INDUCED_DRAG, Rudder.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.PROFILE_DRAG, Rudder.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, DaggerboardOrKeel.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.INDUCED_DRAG, DaggerboardOrKeel.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.PROFILE_DRAG, DaggerboardOrKeel.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, Hull.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, MainLiftingFoil.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.INDUCED_DRAG, MainLiftingFoil.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.PROFILE_DRAG, MainLiftingFoil.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.WAVE_MAKING_DRAG, MainLiftingFoil.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.DRIVING_FORCE, Rigg.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, Rigg.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.INDUCED_DRAG, Rigg.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.PROFILE_DRAG, Rigg.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.BRAKING_FORCE, Crew.ID));
    graphSets.put("Widerstand und Vortrieb", dragSet);
  }

  private void boatTypeSelected(Boat boat)
  {
    this.boat = boat;
    clearResult();
    inputPanel.reset(boat.getValueSets());
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
    gridBagConstraints.weightx = 1;
    gridBagConstraints.weighty = 1;
    frame.add(scrollPane, gridBagConstraints);
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

    QuantityOutput.Mode mode = getOutputMode();
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

  private QuantityOutput.Mode getOutputMode()
  {
    QuantityOutput.Mode mode;
    List<QuantityInput> scannedInputs = inputPanel.getScannedInputs();
    if (scannedInputs.isEmpty())
    {
      mode = QuantityOutput.Mode.NUMERIC_DISPLAY;
    }
    else
    {
      mode = QuantityOutput.Mode.CHECKBOX_DISPLAY;
    }
    return mode;
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
    clearCharts();

    Set<PhysicalQuantityInSet> shownGraphs = textResultPanel.getShownGraphs();
    QuantityInput scannedInput = getScannedInput();

    Map<PhysicalQuantityInSet, XYSeries> quantitySeries
        = calculateQuantitySeriesForSelectedOutputs(shownGraphs, scannedInput);

    calculationStateDisplay.clear();

    int row = 0;
    int column = 0;
    Map<PhysicalQuantityInSet, JFreeChart> charts = new HashMap<>();
    for (Map.Entry<PhysicalQuantityInSet, XYSeries> seriesEntry : quantitySeries.entrySet())
    {
      JFreeChart chart = getExistingChart(seriesEntry.getKey(), charts);
      if (chart == null)
      {
        chart = createChart(scannedInput, seriesEntry);
        charts.put(seriesEntry.getKey(), chart);

        ChartPanel chartPanel = new ChartPanel(chart);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridx = column;
        gridBagConstraints.gridy = row;
        chartsPanel.add(chartPanel, gridBagConstraints);
        chartPanels.add(chartPanel);
      }
      else
      {
        addDataSet(seriesEntry, chart);
      }

      row++;
      if (row > 1)
      {
        row = 0;
        column++;
      }
    }
  }

  private void addDataSet(Map.Entry<PhysicalQuantityInSet, XYSeries> seriesEntry, JFreeChart chart)
  {
    String seriesDisplayName = getSeriesDisplayName(seriesEntry.getKey());
    if (chart.getPlot() instanceof PolarPlot)
    {
      PolarPlot plot = (PolarPlot) chart.getPlot();
      ((XYSeriesCollection)(plot.getDataset())).addSeries(seriesEntry.getValue());
    }
    else if (chart.getPlot() instanceof XYPlot)
    {
      XYPlot plot = (XYPlot) chart.getPlot();
      ((DefaultXYDataset) plot.getDataset()).addSeries(seriesDisplayName, seriesEntry.getValue().toArray());
    }
    chart.setTitle(getGraphSetInWhichQiantityIsMember(seriesEntry.getKey()).getKey());
  }

  private JFreeChart getExistingChart(PhysicalQuantityInSet forQuantity, Map<PhysicalQuantityInSet, JFreeChart> charts)
  {
    Map.Entry<String, Set<PhysicalQuantityInSet>> graphSetInWhichForQuantityIsMember
        = getGraphSetInWhichQiantityIsMember(forQuantity);
    if (graphSetInWhichForQuantityIsMember == null)
    {
      return null;
    }
    for (PhysicalQuantityInSet graphSetQuantity : graphSetInWhichForQuantityIsMember.getValue())
    {
      JFreeChart chart = charts.get(graphSetQuantity);
      if (chart != null)
      {
        return chart;
      }
    }
    return null;
  }

  private Map.Entry<String, Set<PhysicalQuantityInSet>> getGraphSetInWhichQiantityIsMember(
      PhysicalQuantityInSet quantity)
  {
    Map.Entry<String, Set<PhysicalQuantityInSet>> graphSetInWhichQuantityIsMember = null;
    for (Map.Entry<String, Set<PhysicalQuantityInSet>> candidate : graphSets.entrySet())
    {
      if (candidate.getValue().contains(quantity))
      {
        graphSetInWhichQuantityIsMember = candidate;
      }
    }
    return graphSetInWhichQuantityIsMember;
  }

  private QuantityInput getScannedInput()
  {
    List<QuantityInput> scannedInputs = inputPanel.getScannedInputs();
    if (scannedInputs.size() > 1)
    {
      throw new IllegalArgumentException("Can only handle one scanned input");
    }
    QuantityInput scannedInput = scannedInputs.get(0);
    return scannedInput;
  }

  private JFreeChart createChart(
      QuantityInput scannedInput,
      Map.Entry<PhysicalQuantityInSet, XYSeries> seriesEntry)
  {
    String seriesDisplayName = getSeriesDisplayName(seriesEntry.getKey());
    JFreeChart chart;
    if ("°".equals(scannedInput.getQuantity().getUnit()))
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
          seriesEntry.getKey().getPhysicalQuantity().getDisplayNameIncludingUnit(),
          dataset);
    }
    return chart;
  }

  private String getSeriesDisplayName(PhysicalQuantityInSet quantity)
  {
    String seriesDisplayName = boat.getValueSetNonNull(quantity.getValueSetId()).getDisplayName()
        + " " + quantity.getPhysicalQuantity().getDisplayName();
    return seriesDisplayName;
  }

  private Map<PhysicalQuantityInSet, XYSeries> calculateQuantitySeriesForSelectedOutputs(
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
    return quantitySeries;
  }

  private void clearResult()
  {
    clearCharts();
    textResultPanel.clear();
  }

  private void clearCharts()
  {
    for (ChartPanel chartPanel : chartPanels)
    {
      chartsPanel.remove(chartPanel);
    }
    chartPanels.clear();
  }
}
