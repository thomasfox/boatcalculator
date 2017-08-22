package com.github.thomasfox.wingcalculator.calculate.impl;

import java.util.Map;

import com.github.thomasfox.wingcalculator.calculate.Calculator;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;

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
  protected double calculateWithoutChecks(Map<PhysicalQuantity, Double> input)
  {
    double lift = PhysicalQuantity.LIFT.getValueFromAvailableQuantities(input);
    double wingWidth = PhysicalQuantity.WING_SPAN.getValueFromAvailableQuantities(input);
    double wingDepth = PhysicalQuantity.WING_CHORD.getValueFromAvailableQuantities(input);
    double velocity = PhysicalQuantity.VELOCITY.getValueFromAvailableQuantities(input);
    double density = PhysicalQuantity.DENSITY.getValueFromAvailableQuantities(input);

    double area = wingWidth * wingDepth;
    double result = 2 * lift / velocity / velocity / density / area;
    return result;
  }
}
