package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValues;

/**
 * https://de.wikipedia.org/wiki/Froude-Zahl
 */
public class FroudeNumberCalculator extends Calculator
{
  public FroudeNumberCalculator()
  {
    super(PhysicalQuantity.FROUDE_NUMBER_SUMBERGENCE,
        PhysicalQuantity.SUBMERGENCE_DEPTH,
        PhysicalQuantity.VELOCITY,
        PhysicalQuantity.GRAVITY_ACCELERATION);
  }

  @Override
  protected double calculateWithoutChecks(PhysicalQuantityValues input)
  {
    double submergenceDepth = input.getValue(PhysicalQuantity.SUBMERGENCE_DEPTH);
    double velocity = input.getValue(PhysicalQuantity.VELOCITY);
    double gravityAcceleration = input.getValue(PhysicalQuantity.GRAVITY_ACCELERATION);

    double froudeNumber = velocity / Math.sqrt(submergenceDepth * gravityAcceleration);
    return froudeNumber;
  }
}
