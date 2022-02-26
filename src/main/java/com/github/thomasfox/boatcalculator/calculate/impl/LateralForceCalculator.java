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
    double drag = valueSet.getKnownQuantityValue(PhysicalQuantity.TOTAL_DRAG).getValue();
    double lift = valueSet.getKnownQuantityValue(PhysicalQuantity.LIFT).getValue();
    double flowDirection = valueSet.getKnownQuantityValue(PhysicalQuantity.FLOW_DIRECTION).getValue();

    double lateralForce = lift * Math.cos(flowDirection * Math.PI / 180d) + drag * Math.sin(flowDirection * Math.PI / 180d);
    return lateralForce;
  }
}
