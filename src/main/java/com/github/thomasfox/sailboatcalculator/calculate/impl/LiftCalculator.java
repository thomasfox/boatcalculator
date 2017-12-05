package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.ValueSet;

/**
 * https://de.wikipedia.org/wiki/Dynamischer_Auftrieb
 */
public class LiftCalculator extends Calculator
{
  public LiftCalculator()
  {
    super(PhysicalQuantity.LIFT,
        PhysicalQuantity.LIFT_COEFFICIENT,
        PhysicalQuantity.WING_AREA,
        PhysicalQuantity.VELOCITY,
        PhysicalQuantity.DENSITY);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double liftCoefficient = valueSet.getKnownValue(PhysicalQuantity.LIFT_COEFFICIENT).getValue();
    double wingArea = valueSet.getKnownValue(PhysicalQuantity.WING_AREA).getValue();
    double velocity = valueSet.getKnownValue(PhysicalQuantity.VELOCITY).getValue();
    double density = valueSet.getKnownValue(PhysicalQuantity.DENSITY).getValue();

    double lift = liftCoefficient *  velocity * velocity * density * wingArea / 2;
    return lift;
  }
}
