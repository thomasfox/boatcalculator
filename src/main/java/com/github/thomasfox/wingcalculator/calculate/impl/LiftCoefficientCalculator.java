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
        PhysicalQuantity.WING_WIDTH,
        PhysicalQuantity.WING_DEPTH,
        PhysicalQuantity.WING_VELOCITY,
        PhysicalQuantity.DENSITY);
  }

  @Override
  protected double calculateWithoutChecks(Map<PhysicalQuantity, Double> input)
  {
    double lift = getValueOf(PhysicalQuantity.LIFT, input);
    double wingWidth = getValueOf(PhysicalQuantity.WING_WIDTH, input);
    double wingDepth = getValueOf(PhysicalQuantity.WING_DEPTH, input);
    double velocity = getValueOf(PhysicalQuantity.WING_VELOCITY, input);
    double density = getValueOf(PhysicalQuantity.DENSITY, input);

    double area = wingWidth * wingDepth;
    double result = 2 * lift / velocity / velocity / density / area;
    return result;
  }
}
