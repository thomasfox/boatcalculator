package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.ValueSet;

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
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double drag = valueSet.getKnownValue(PhysicalQuantity.TOTAL_DRAG).getValue();
    double lift = valueSet.getKnownValue(PhysicalQuantity.LIFT).getValue();
    double flowDirection = valueSet.getKnownValue(PhysicalQuantity.FLOW_DIRECTION).getValue();

    double drivingForce = lift * Math.sin(flowDirection * Math.PI / 180d) - drag * Math.cos(flowDirection * Math.PI / 180d);
    return drivingForce;
  }
}
