package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantityValues;

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
  protected double calculateWithoutChecks(PhysicalQuantityValues input)
  {
    double weight = input.getValue(PhysicalQuantity.WEIGHT);
    double torque = input.getValue(PhysicalQuantity.TORQUE_BETWEEN_FORCES);
    double gravityAcceleration = input.getValue(PhysicalQuantity.GRAVITY_ACCELERATION);

    double force = weight * gravityAcceleration;
    double lever = torque / force;
    return lever;
  }
}
