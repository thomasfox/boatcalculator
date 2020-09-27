package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

/**
 * https://de.wikipedia.org/wiki/Dynamischer_Auftrieb
 */
public class LiftCalculator extends Calculator
{
  public LiftCalculator()
  {
    super(PhysicalQuantity.LIFT,
        PhysicalQuantity.LIFT_COEFFICIENT_3D,
        PhysicalQuantity.AREA,
        PhysicalQuantity.VELOCITY,
        PhysicalQuantity.DENSITY);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double liftCoefficient = valueSet.getKnownValue(PhysicalQuantity.LIFT_COEFFICIENT_3D).getValue();
    double wingArea = valueSet.getKnownValue(PhysicalQuantity.AREA).getValue();
    double velocity = valueSet.getKnownValue(PhysicalQuantity.VELOCITY).getValue();
    double density = valueSet.getKnownValue(PhysicalQuantity.DENSITY).getValue();

    double lift = liftCoefficient *  velocity * velocity * density * wingArea / 2;
    return lift;
  }
}
