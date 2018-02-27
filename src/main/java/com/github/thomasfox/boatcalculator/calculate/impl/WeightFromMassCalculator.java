package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

public class WeightFromMassCalculator extends Calculator
{
  public WeightFromMassCalculator()
  {
    super(PhysicalQuantity.WEIGHT,
        PhysicalQuantity.MASS,
        PhysicalQuantity.GRAVITY_ACCELERATION);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double mass = valueSet.getKnownValue(PhysicalQuantity.MASS).getValue();
    double gravityAcceleration = valueSet.getKnownValue(PhysicalQuantity.GRAVITY_ACCELERATION).getValue();

    double weight = mass * gravityAcceleration;
    return weight;
  }
}
