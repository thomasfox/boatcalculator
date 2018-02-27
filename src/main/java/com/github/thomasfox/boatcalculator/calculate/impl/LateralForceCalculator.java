package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

public class LateralForceCalculator extends Calculator
{
  public LateralForceCalculator()
  {
    super(PhysicalQuantity.LATERAL_FORCE,
        PhysicalQuantity.FLOW_DIRECTION,
        PhysicalQuantity.TOTAL_DRAG,
        PhysicalQuantity.LIFT);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double drag = valueSet.getKnownValue(PhysicalQuantity.TOTAL_DRAG).getValue();
    double lift = valueSet.getKnownValue(PhysicalQuantity.LIFT).getValue();
    double flowDirection = valueSet.getKnownValue(PhysicalQuantity.FLOW_DIRECTION).getValue();

    double lateralForce = lift * Math.cos(flowDirection * Math.PI / 180d) + drag * Math.sin(flowDirection * Math.PI / 180d);
    return lateralForce;
  }
}
