package com.github.thomasfox.boatcalculator.gui.panel.part;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;

public interface ScannedInput
{
  /** Returns the number of scan steps, or null if the input is not scanned. */
  Integer getNumberOfScanSteps();

  /** Sets the value of the input to the value of the given scan step. */
  void setValueForScanStep(int step);

  Number getScanXValue(int step);

  /** Returns a textual description of the given scan step. */
  String getScanStepDescription(int step);

  String getScanDescription();

  boolean isScan();

  PhysicalQuantity getQuantity();

  double getStepWidth();
}
