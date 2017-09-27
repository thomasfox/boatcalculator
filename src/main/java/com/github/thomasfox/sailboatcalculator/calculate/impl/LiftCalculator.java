package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValues;

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
  protected double calculateWithoutChecks(PhysicalQuantityValues input)
  {
    double liftCoefficient = input.getValue(PhysicalQuantity.LIFT_COEFFICIENT);
    double wingArea = input.getValue(PhysicalQuantity.WING_AREA);
    double velocity = input.getValue(PhysicalQuantity.VELOCITY);
    double density = input.getValue(PhysicalQuantity.DENSITY);

    double lift = liftCoefficient *  velocity * velocity * density * wingArea / 2;
    return lift;
  }
}
