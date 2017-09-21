package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValues;

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
  protected double calculateWithoutChecks(PhysicalQuantityValues input)
  {
    double drag = input.getValue(PhysicalQuantity.TOTAL_DRAG);
    double lift = input.getValue(PhysicalQuantity.LIFT);
    double flowDirection = input.getValue(PhysicalQuantity.FLOW_DIRECTION);

    double lateralForce = lift * Math.cos(flowDirection * Math.PI / 180d) + drag * Math.sin(flowDirection * Math.PI / 180d);
    return lateralForce;
  }
}
