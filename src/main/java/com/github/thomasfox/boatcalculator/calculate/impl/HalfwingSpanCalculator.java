package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

public class HalfwingSpanCalculator extends Calculator
{
  public HalfwingSpanCalculator()
  {
    super(PhysicalQuantity.HALFWING_SPAN,
        PhysicalQuantity.WING_SPAN);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double wingSpan = valueSet.getKnownQuantityValue(PhysicalQuantity.WING_SPAN).getValue();
    return wingSpan / 2;
  }
}
