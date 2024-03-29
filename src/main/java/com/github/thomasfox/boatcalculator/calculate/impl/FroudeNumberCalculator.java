package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

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
    double submergenceDepth = valueSet.getKnownQuantityValue(PhysicalQuantity.SUBMERGENCE_DEPTH).getValue();
    double velocity = valueSet.getKnownQuantityValue(PhysicalQuantity.VELOCITY).getValue();
    double gravityAcceleration = valueSet.getKnownQuantityValue(PhysicalQuantity.GRAVITY_ACCELERATION).getValue();

    double froudeNumber = velocity / Math.sqrt(submergenceDepth * gravityAcceleration);
    return froudeNumber;
  }
}
