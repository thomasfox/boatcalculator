package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

public class LiftDividedByTotalDragCalculator extends Calculator
{
  public LiftDividedByTotalDragCalculator()
  {
    super(PhysicalQuantity.LIFT_DIVIDED_BY_TOTAL_DRAG,
        PhysicalQuantity.LIFT,
        PhysicalQuantity.TOTAL_DRAG);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double lift = valueSet.getKnownQuantityValue(PhysicalQuantity.LIFT).getValue();
    double drag = valueSet.getKnownQuantityValue(PhysicalQuantity.TOTAL_DRAG).getValue();

    if (lift == 0)
    {
      return 0;
    }
    double result = lift / drag;
    return result;
  }
}
