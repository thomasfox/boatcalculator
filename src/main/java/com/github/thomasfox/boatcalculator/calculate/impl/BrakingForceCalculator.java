package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

public class BrakingForceCalculator extends Calculator
{
  public BrakingForceCalculator()
  {
    super(PhysicalQuantity.BRAKING_FORCE,
        PhysicalQuantity.FLOW_DIRECTION,
        PhysicalQuantity.PARASITIC_DRAG);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double drag = valueSet.getKnownValue(PhysicalQuantity.PARASITIC_DRAG).getValue();
    double flowDirection = valueSet.getKnownValue(PhysicalQuantity.FLOW_DIRECTION).getValue();

    double brakingForce = drag * Math.cos(flowDirection * Math.PI / 180d);
    return brakingForce;
  }
}
