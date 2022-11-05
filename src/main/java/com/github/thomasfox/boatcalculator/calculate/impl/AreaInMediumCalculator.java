package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

public class AreaInMediumCalculator extends Calculator
{
  public AreaInMediumCalculator()
  {
    super(PhysicalQuantity.AREA_IN_MEDIUM,
        PhysicalQuantity.WING_CHORD,
        PhysicalQuantity.WING_SPAN_IN_MEDIUM);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double wingChord = valueSet.getKnownQuantityValue(PhysicalQuantity.WING_CHORD).getValue();
    double wingSpan = valueSet.getKnownQuantityValue(PhysicalQuantity.WING_SPAN_IN_MEDIUM).getValue();

    return wingChord*wingSpan;
  }
}
