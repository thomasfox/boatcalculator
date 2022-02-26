package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

public class AreaCalculator extends Calculator
{
  public AreaCalculator()
  {
    super(PhysicalQuantity.AREA,
        PhysicalQuantity.WING_CHORD,
        PhysicalQuantity.WING_SPAN);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double wingChord = valueSet.getKnownQuantityValue(PhysicalQuantity.WING_CHORD).getValue();
    double wingSpan = valueSet.getKnownQuantityValue(PhysicalQuantity.WING_SPAN).getValue();

    return wingChord*wingSpan;
  }
}
