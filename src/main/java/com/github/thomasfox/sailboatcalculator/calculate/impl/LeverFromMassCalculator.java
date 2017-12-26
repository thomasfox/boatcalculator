package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.valueset.ValueSet;

public class LeverFromMassCalculator extends Calculator
{
  public LeverFromMassCalculator()
  {
    super(PhysicalQuantity.LEVER_WEIGHT,
        PhysicalQuantity.MASS,
        PhysicalQuantity.TORQUE_BETWEEN_FORCES,
        PhysicalQuantity.GRAVITY_ACCELERATION);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double mass = valueSet.getKnownValue(PhysicalQuantity.MASS).getValue();
    double torque = valueSet.getKnownValue(PhysicalQuantity.TORQUE_BETWEEN_FORCES).getValue();
    double gravityAcceleration = valueSet.getKnownValue(PhysicalQuantity.GRAVITY_ACCELERATION).getValue();

    double weight = mass * gravityAcceleration;
    double lever = torque / weight;
    return lever;
  }
}
