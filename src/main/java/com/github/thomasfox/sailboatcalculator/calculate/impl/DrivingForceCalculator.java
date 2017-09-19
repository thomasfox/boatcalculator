package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValues;

public class DrivingForceCalculator extends Calculator
{
  public DrivingForceCalculator()
  {
    super(PhysicalQuantity.DRIVING_FORCE,
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

    double drivingForce = lift * Math.sin(flowDirection * Math.PI / 180d) - drag * Math.cos(flowDirection * Math.PI / 180d);
    return drivingForce;
  }
}
