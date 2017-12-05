package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.ValueSet;

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
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double submergenceDepth = valueSet.getKnownValue(PhysicalQuantity.SUBMERGENCE_DEPTH).getValue();
    double velocity = valueSet.getKnownValue(PhysicalQuantity.VELOCITY).getValue();
    double gravityAcceleration = valueSet.getKnownValue(PhysicalQuantity.GRAVITY_ACCELERATION).getValue();

    double froudeNumber = velocity / Math.sqrt(submergenceDepth * gravityAcceleration);
    return froudeNumber;
  }
}
