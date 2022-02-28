package com.github.thomasfox.boatcalculator.gui.model;

import java.util.ArrayList;
import java.util.List;

import org.jfree.data.xy.DefaultXYZDataset;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;

public class ScanResultForSingleQuantity2D
{
  private final PhysicalQuantity scannedQuantityX;

  private final PhysicalQuantity scannedQuantityY;

  private final PhysicalQuantityInSet resultQuantity;

  private String displayName;

  private final List<Double> xValues = new ArrayList<>();

  private final List<Double> yValues = new ArrayList<>();

  private final double stepWidthX;

  private final double stepWidthY;

  private final List<Double> resultValues = new ArrayList<>();

  public ScanResultForSingleQuantity2D(
      PhysicalQuantity scannedQuantityX,
      PhysicalQuantity scannedQuantityY,
      PhysicalQuantityInSet resultQuantity,
      double stepWidthX,
      double stepWidthY)
  {
    this.scannedQuantityX = scannedQuantityX;
    this.scannedQuantityY = scannedQuantityY;
    this.resultQuantity = resultQuantity;
    this.stepWidthX = stepWidthX;
    this.stepWidthY = stepWidthY;
  }

  public String getDisplayName()
  {
    return displayName;
  }

  public void setDisplayName(String resultQuantitySetDisplayName)
  {
    this.displayName = resultQuantitySetDisplayName;
  }

  public double getStepWidthX()
  {
    return stepWidthX;
  }

  public double getStepWidthY()
  {
    return stepWidthY;
  }

  public void add(double scannedQuantity1Value, double scannedQuantity2Value, double resultQuantityValue)
  {
    xValues.add(scannedQuantity1Value);
    yValues.add(scannedQuantity2Value);
    resultValues.add(resultQuantityValue);
  }

  private double[][] getXYZDatasetValue()
  {
    double[][] datasetValues = new double[3][resultValues.size()];
    for (int i = 0; i < resultValues.size(); i++)
    {
      datasetValues[0][i] = xValues.get(i);
      datasetValues[1][i] = yValues.get(i);
      datasetValues[2][i] = resultValues.get(i);
    }
    return datasetValues;
  }

  public void addSeriesTo(DefaultXYZDataset dataset)
  {
    dataset.addSeries(
        resultQuantity.getSetId() + ":" + resultQuantity.getPhysicalQuantity().getDisplayName(),
        getXYZDatasetValue());
  }

  public PhysicalQuantity getScannedQuantityX()
  {
    return scannedQuantityX;
  }

  public PhysicalQuantity getScannedQuantityY()
  {
    return scannedQuantityY;
  }

  public PhysicalQuantityInSet getResultQuantity()
  {
    return resultQuantity;
  }

  public double getMaximumAbsoluteValue()
  {
    double maxAbsValue = 0;
    for (double resultValue : resultValues)
    {
      double absValue = Math.abs(resultValue);
      if (maxAbsValue < absValue)
      {
        maxAbsValue = absValue;
      }
    }
    return maxAbsValue;
  }

  @Override
  public String toString()
  {
    return displayName;
  }
}
