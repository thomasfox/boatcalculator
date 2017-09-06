package com.github.thomasfox.wingcalculator.gui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.github.thomasfox.wingcalculator.boat.Boat;
import com.github.thomasfox.wingcalculator.boat.impl.Skiff29er;
import com.github.thomasfox.wingcalculator.calculate.NamedValueSet;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantityValue;

public class SwingGui
{
  public static final File PROFILE_DIRECTORY = new File("profiles");

  public static final File HULL_DIRECTORY = new File("hulls");

  private final JFrame frame = new JFrame("wingCalculator");

  private final JPanel inputPanel = new JPanel();

  private final JPanel resultPanel = new JPanel();

  private final List<PartInput> valueSetInputs = new ArrayList<>();

  private final List<PartOutput> valueSetOutputs = new ArrayList<>();

  private final List<ChartPanel> chartPanels = new ArrayList<>();

  private final JButton calculateButton;

  private final int rowAfterButton;

  private final Boat boat = new Skiff29er();

  public SwingGui()
  {
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new FlowLayout());

    inputPanel.setLayout(new GridBagLayout());
    frame.add(inputPanel);

    resultPanel.setLayout(new GridBagLayout());
    frame.add(resultPanel);

    for (NamedValueSet namedValueSet : boat.getNamedValueSets())
    {
      createPartInput(namedValueSet);
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

    row++;

    rowAfterButton = row;
    frame.pack();
    frame.setVisible(true);
  }

  private void createPartInput(NamedValueSet valueSet)
  {
    PartInput partInput = new PartInput(valueSet);
    valueSetInputs.add(partInput);
    for (PhysicalQuantity physicalQuantity : valueSet.getToInput())
    {
      if (valueSet.getFixedValue(physicalQuantity) == null)
      {
        partInput.add(new QuantityInput(physicalQuantity, valueSet.getStartValue(physicalQuantity)));
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
    List<QuantityInput> scannedInputs = new ArrayList<>();
    for (PartInput valueSetInput : valueSetInputs)
    {
      scannedInputs.addAll(valueSetInput.getScannedQuantityInputs());
    }

    if (!scannedInputs.isEmpty())
    {
      calculateAndRefreshDisplayedResultsScan(scannedInputs);
    }
    else
    {
      calculateAndRefreshDisplayedResultsNoScan();
    }
    frame.pack();
  }

  private void calculateAndRefreshDisplayedResultsNoScan()
  {
    clearResult();
    reinitalizeValueSetInputs();
    boat.calculate();
    displayCalculateResultInValueSetOutputs();
  }

  private void calculateAndRefreshDisplayedResultsScan(List<QuantityInput> scannedInputs)
  {
    clearResult();

    if (scannedInputs.size() > 1)
    {
      throw new IllegalArgumentException("Can only handle one scanned input");
    }
    QuantityInput scannedInput = scannedInputs.get(0);

    XYSeries velocitySeries = new XYSeries("velocity", false, true);

    for (int i = 0; i < scannedInput.getNumberOfScanSteps(); ++i)
    {
      double xValue = scannedInput.getScanStepValue(i);
      scannedInput.setValue(xValue);
      reinitalizeValueSetInputs();
      boat.calculate();
      double yValue = boat.getNamedValueSetNonNull(Boat.EXTERNAL_SETTINGS_ID).getKnownValue(PhysicalQuantity.VELOCITY).getValue();
      velocitySeries.add(xValue, yValue);
    }
    JFreeChart velocityChart;
    if ("°".equals(scannedInput.getQuantity().getUnit()))
    {
      XYSeriesCollection velocitySeriesCollection = new XYSeriesCollection();
      velocitySeriesCollection.addSeries(velocitySeries);
      velocityChart = ChartFactory.createPolarChart("Velocity", velocitySeriesCollection, false, true, false);
    }
    else
    {
      DefaultXYDataset dataset = new DefaultXYDataset();
      dataset.addSeries("velocity", velocitySeries.toArray());
      velocityChart = ChartFactory.createXYLineChart(
          "Velocity",
          scannedInput.getQuantity().getDisplayNameIncludingUnit(),
          PhysicalQuantity.VELOCITY.getDisplayNameIncludingUnit(),
          dataset);
    }
    ChartPanel velocityChartPanel = new ChartPanel(velocityChart);
    resultPanel.add(velocityChartPanel);
    chartPanels.add(velocityChartPanel);
  }


  private void displayCalculateResultInValueSetOutputs()
  {
    int outputRow = 0;
    for (PartInput valueSetInput : valueSetInputs)
    {
      PartOutput partOutput = new PartOutput(valueSetInput.getValueSet().getName());
      valueSetOutputs.add(partOutput);
      for (PhysicalQuantityValue calculatedValue : valueSetInput.getValueSet().getCalculatedValues().getAsList())
      {
        QuantityOutput output = new QuantityOutput(calculatedValue.getPhysicalQuantity(), calculatedValue.getValue());
        partOutput.getQuantityOutputs().add(output);
      }
      outputRow += partOutput.addToContainerInRow(resultPanel, rowAfterButton + outputRow);
    }
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
    clearValueSetOutputs();
  }

  private void clearCharts()
  {
    for (ChartPanel chartPanel : chartPanels)
    {
      resultPanel.remove(chartPanel);
    }
    chartPanels.clear();
  }

  private void clearValueSetOutputs()
  {
    for (PartOutput partOutput : valueSetOutputs)
    {
      partOutput.removeFromContainerAndReset(resultPanel);
    }
    valueSetOutputs.clear();
  }
}
