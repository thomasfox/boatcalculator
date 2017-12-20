package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.valueset.ValueSet;

public class LeverFromWeightCalculator extends Calculator
{
  public LeverFromWeightCalculator()
  {
    super(PhysicalQuantity.LEVER_WEIGHT,
        PhysicalQuantity.WEIGHT,
        PhysicalQuantity.TORQUE_BETWEEN_FORCES,
        PhysicalQuantity.GRAVITY_ACCELERATION);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double weight = valueSet.getKnownValue(PhysicalQuantity.WEIGHT).getValue();
    double torque = valueSet.getKnownValue(PhysicalQuantity.TORQUE_BETWEEN_FORCES).getValue();
    double gravityAcceleration = valueSet.getKnownValue(PhysicalQuantity.GRAVITY_ACCELERATION).getValue();

    double force = weight * gravityAcceleration;
    double lever = torque / force;
    return lever;
  }
}
