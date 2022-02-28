package com.github.thomasfox.boatcalculator.gui.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The result of a scan where two physical quantities are scanned.
 */
public class ScanResult2D
{
  private final List<ScanResultForSingleQuantity2D> singleQuantityScanResults = new ArrayList<>();

  public List<ScanResultForSingleQuantity2D> getSingleQuantityScanResults()
  {
    return singleQuantityScanResults;
  }
}
