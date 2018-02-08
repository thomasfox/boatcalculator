package com.github.thomasfox.sailboatcalculator.gui.model;

import org.jfree.data.xy.XYSeries;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.value.PhysicalQuantityInSet;

public class SingleScanResult
{
  private final PhysicalQuantity scannedQuantity;

  private final PhysicalQuantityInSet resultQuantity;

  private String displayName;

  private XYSeries series;

  public SingleScanResult(PhysicalQuantity scannedQuantity,
      PhysicalQuantityInSet resultQuantity)
  {
    this.scannedQuantity = scannedQuantity;
    this.resultQuantity = resultQuantity;
  }

  public String getDisplayName()
  {
    return displayName;
  }

  public void setDisplayName(String resultQuantitySetDisplayName)
  {
    this.displayName = resultQuantitySetDisplayName;
  }

  public XYSeries getSeries()
  {
    return series;
  }

  public void setSeries(XYSeries series)
  {
    this.series = series;
  }

  public PhysicalQuantity getScannedQuantity()
  {
    return scannedQuantity;
  }

  public PhysicalQuantityInSet getResultQuantity()
  {
    return resultQuantity;
  }

  @Override
  public String toString()
  {
    return displayName;
  }
}
