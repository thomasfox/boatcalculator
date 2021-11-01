package com.github.thomasfox.boatcalculator.gui.panel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import org.jfree.data.xy.XYSeriesCollection;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.gui.model.ScanResult;
import com.github.thomasfox.boatcalculator.gui.model.ScanResultForSingleQuantity;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.valueset.impl.BoatGlobalValues;
import com.github.thomasfox.boatcalculator.valueset.impl.Crew;
import com.github.thomasfox.boatcalculator.valueset.impl.DaggerboardOrKeel;
import com.github.thomasfox.boatcalculator.valueset.impl.Hull;
import com.github.thomasfox.boatcalculator.valueset.impl.MainLiftingFoil;
import com.github.thomasfox.boatcalculator.valueset.impl.Rigg;
import com.github.thomasfox.boatcalculator.valueset.impl.Rudder;
import com.github.thomasfox.boatcalculator.valueset.impl.RudderLiftingFoil;

public class ChartsPanel extends JPanel
{
  private final List<ChartPanel> chartPanels = new ArrayList<>();

  private final Map<String, Set<PhysicalQuantityInSet>> graphSets = new HashMap<>();

  private static final long serialVersionUID = 1L;

  private ScanResult scanResult;

  public ChartsPanel()
  {
    setLayout(new GridBagLayout());
    fillGraphSets();
  }

  private void fillGraphSets()
  {
    Set<PhysicalQuantityInSet> dragSet = new HashSet<>();
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, BoatGlobalValues.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, Rudder.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.INDUCED_DRAG, Rudder.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.PROFILE_DRAG, Rudder.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.SURFACE_PIERCING_DRAG, Rudder.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, DaggerboardOrKeel.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.INDUCED_DRAG, DaggerboardOrKeel.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.PROFILE_DRAG, DaggerboardOrKeel.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.SURFACE_PIERCING_DRAG, DaggerboardOrKeel.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, Hull.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, MainLiftingFoil.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.INDUCED_DRAG, MainLiftingFoil.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.PROFILE_DRAG, MainLiftingFoil.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.WAVE_MAKING_DRAG, MainLiftingFoil.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, RudderLiftingFoil.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.INDUCED_DRAG, RudderLiftingFoil.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.PROFILE_DRAG, RudderLiftingFoil.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.WAVE_MAKING_DRAG, RudderLiftingFoil.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.DRIVING_FORCE, Rigg.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, Rigg.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.INDUCED_DRAG, Rigg.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.PROFILE_DRAG, Rigg.ID));
    dragSet.add(new PhysicalQuantityInSet(PhysicalQuantity.BRAKING_FORCE, Crew.ID));
    graphSets.put("Widerstand und Vortrieb", dragSet);
    Set<PhysicalQuantityInSet> dragCoefficiontSet = new HashSet<>();
    dragCoefficiontSet.add(new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG_COEFFICIENT, Rudder.ID));
    dragCoefficiontSet.add(new PhysicalQuantityInSet(PhysicalQuantity.INDUCED_DRAG_COEFFICIENT, Rudder.ID));
    dragCoefficiontSet.add(new PhysicalQuantityInSet(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT, Rudder.ID));
    dragCoefficiontSet.add(new PhysicalQuantityInSet(PhysicalQuantity.SURFACE_PIERCING_DRAG_COEFFICIENT, Rudder.ID));
    dragCoefficiontSet.add(new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG_COEFFICIENT, DaggerboardOrKeel.ID));
    dragCoefficiontSet.add(new PhysicalQuantityInSet(PhysicalQuantity.INDUCED_DRAG_COEFFICIENT, DaggerboardOrKeel.ID));
    dragCoefficiontSet.add(new PhysicalQuantityInSet(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT, DaggerboardOrKeel.ID));
    dragCoefficiontSet.add(new PhysicalQuantityInSet(PhysicalQuantity.SURFACE_PIERCING_DRAG_COEFFICIENT, DaggerboardOrKeel.ID));
    dragCoefficiontSet.add(new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG_COEFFICIENT, MainLiftingFoil.ID));
    dragCoefficiontSet.add(new PhysicalQuantityInSet(PhysicalQuantity.INDUCED_DRAG_COEFFICIENT, MainLiftingFoil.ID));
    dragCoefficiontSet.add(new PhysicalQuantityInSet(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT, MainLiftingFoil.ID));
    dragCoefficiontSet.add(new PhysicalQuantityInSet(PhysicalQuantity.WAVE_MAKING_DRAG_COEFFICIENT, MainLiftingFoil.ID));
    dragCoefficiontSet.add(new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG_COEFFICIENT, RudderLiftingFoil.ID));
    dragCoefficiontSet.add(new PhysicalQuantityInSet(PhysicalQuantity.INDUCED_DRAG_COEFFICIENT, RudderLiftingFoil.ID));
    dragCoefficiontSet.add(new PhysicalQuantityInSet(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT, RudderLiftingFoil.ID));
    dragCoefficiontSet.add(new PhysicalQuantityInSet(PhysicalQuantity.WAVE_MAKING_DRAG_COEFFICIENT, RudderLiftingFoil.ID));
    graphSets.put("Widerstandsbeiwerte", dragCoefficiontSet);
  }

  public void addToFrame(JFrame frame)
  {
    JScrollPane scrollPane = new JScrollPane(this);
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

  public void clear()
  {
    for (ChartPanel chartPanel : chartPanels)
    {
      remove(chartPanel);
    }
    chartPanels.clear();
  }

  public void display(ScanResult scanResult)
  {
    this.scanResult = scanResult;
    int row = 0;
    int column = 0;
    Map<PhysicalQuantityInSet, JFreeChart> charts = new HashMap<>();
    for (ScanResultForSingleQuantity singleScanResult : scanResult.getSingleQuantityScanResults())
    {
      JFreeChart chart = getExistingChart(singleScanResult.getResultQuantity(), charts);
      if (chart == null)
      {
        chart = createChart(singleScanResult);
        charts.put(singleScanResult.getResultQuantity(), chart);

        ChartPanel chartPanel = new ChartPanel(chart);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridx = column;
        gridBagConstraints.gridy = row;
        add(chartPanel, gridBagConstraints);
        chartPanels.add(chartPanel);
        row++;
        if (row > 1)
        {
          row = 0;
          column++;
        }
      }
      else
      {
        addDataSet(singleScanResult, chart);
      }
    }
  }

  public ScanResult getScanResult()
  {
    return scanResult;
  }

  private Map.Entry<String, Set<PhysicalQuantityInSet>> getGraphSetInWhichQuantityIsMember(
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

  private JFreeChart createChart(ScanResultForSingleQuantity singleScanResult)
  {
    JFreeChart chart;
    if ("°".equals(singleScanResult.getScannedQuantity().getUnit())
        && PhysicalQuantity.MAX_ANGLE_OF_ATTACK != singleScanResult.getScannedQuantity()
        && PhysicalQuantity.ANGLE_OF_ATTACK != singleScanResult.getScannedQuantity())
    {
      XYSeriesCollection seriesCollection = new XYSeriesCollection();
      seriesCollection.addSeries(singleScanResult.getSeries());
      chart = ChartFactory.createPolarChart(singleScanResult.getDisplayName(), seriesCollection, false, true, false);
    }
    else
    {
      DefaultXYDataset dataset = new DefaultXYDataset();
      dataset.addSeries(singleScanResult.getDisplayName(), singleScanResult.getSeries().toArray());
      chart = ChartFactory.createXYLineChart(
          singleScanResult.getDisplayName(),
          singleScanResult.getScannedQuantity().getDisplayNameIncludingUnit(),
          singleScanResult.getResultQuantity().getPhysicalQuantity().getDisplayNameIncludingUnit(),
          dataset);
    }
    return chart;
  }

  private JFreeChart getExistingChart(PhysicalQuantityInSet forQuantity, Map<PhysicalQuantityInSet, JFreeChart> charts)
  {
    Map.Entry<String, Set<PhysicalQuantityInSet>> graphSetInWhichForQuantityIsMember
        = getGraphSetInWhichQuantityIsMember(forQuantity);
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

  private void addDataSet(ScanResultForSingleQuantity singleScanResult, JFreeChart chart)
  {
    if (chart.getPlot() instanceof PolarPlot)
    {
      PolarPlot plot = (PolarPlot) chart.getPlot();
      ((XYSeriesCollection)(plot.getDataset())).addSeries(singleScanResult.getSeries());
    }
    else if (chart.getPlot() instanceof XYPlot)
    {
      XYPlot plot = (XYPlot) chart.getPlot();
      ((DefaultXYDataset) plot.getDataset()).addSeries(singleScanResult.getDisplayName(), singleScanResult.getSeries().toArray());
    }
    chart.setTitle(getGraphSetInWhichQuantityIsMember(singleScanResult.getResultQuantity()).getKey());
  }
}
