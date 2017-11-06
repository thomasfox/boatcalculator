package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValues;

public class BrakingForceCalculator extends Calculator
{
  public BrakingForceCalculator()
  {
    super(PhysicalQuantity.BRAKING_FORCE,
        PhysicalQuantity.FLOW_DIRECTION,
        PhysicalQuantity.PARASITIC_DRAG);
  }

  @Override
  protected double calculateWithoutChecks(PhysicalQuantityValues input)
  {
    double drag = input.getValue(PhysicalQuantity.PARASITIC_DRAG);
    double flowDirection = input.getValue(PhysicalQuantity.FLOW_DIRECTION);

    double brakingForce = drag * Math.cos(flowDirection * Math.PI / 180d);
    return brakingForce;
  }
}
