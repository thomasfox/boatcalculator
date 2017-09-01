package com.github.thomasfox.wingcalculator.calculate.impl;

import java.util.Map;

import com.github.thomasfox.wingcalculator.calculate.Calculator;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;

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
  protected double calculateWithoutChecks(Map<PhysicalQuantity, Double> input)
  {
    double weight = PhysicalQuantity.WEIGHT.getValueFromAvailableQuantities(input);
    double torque = PhysicalQuantity.TORQUE_BETWEEN_FORCES.getValueFromAvailableQuantities(input);
    double gravityAcceleration = PhysicalQuantity.GRAVITY_ACCELERATION.getValueFromAvailableQuantities(input);

    double force = weight * gravityAcceleration;
    double lever = torque / force;
    return lever;
  }
}
