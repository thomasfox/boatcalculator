package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValues;

/**
 * https://de.wikipedia.org/wiki/Dynamischer_Auftrieb
 */
public class LiftCoefficientCalculator extends Calculator
{
  public LiftCoefficientCalculator()
  {
    super(PhysicalQuantity.LIFT_COEFFICIENT,
        PhysicalQuantity.LIFT,
        PhysicalQuantity.WING_SPAN,
        PhysicalQuantity.WING_CHORD,
        PhysicalQuantity.VELOCITY,
        PhysicalQuantity.DENSITY);
  }

  @Override
  protected double calculateWithoutChecks(PhysicalQuantityValues input)
  {
    double lift = input.getValue(PhysicalQuantity.LIFT);
    double wingWidth = input.getValue(PhysicalQuantity.WING_SPAN);
    double wingDepth = input.getValue(PhysicalQuantity.WING_CHORD);
    double velocity = input.getValue(PhysicalQuantity.VELOCITY);
    double density = input.getValue(PhysicalQuantity.DENSITY);

    double area = wingWidth * wingDepth;
    double result = 2 * lift / velocity / velocity / density / area;
    return result;
  }
}
