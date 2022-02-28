package com.github.thomasfox.boatcalculator.gui.model;

import org.jfree.data.xy.XYSeries;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;

public class ScanResultForSingleQuantity1D
{
  private final PhysicalQuantity scannedQuantity;

  private final PhysicalQuantityInSet resultQuantity;

  private String displayName;

  private XYSeries series;

  public ScanResultForSingleQuantity1D(PhysicalQuantity scannedQuantity,
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
