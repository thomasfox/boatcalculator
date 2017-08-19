package com.github.thomasfox.wingcalculator.calculate.impl;

import java.util.Map;

import com.github.thomasfox.wingcalculator.calculate.Calculator;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;

/**
 * https://de.wikipedia.org/wiki/Dynamischer_Auftrieb
 */
public class ProfileDragCalculator extends Calculator
{
  public ProfileDragCalculator()
  {
    super(PhysicalQuantity.PROFILE_DRAG,
        PhysicalQuantity.PROFILE_DRAG_COEFFICIENT,
        PhysicalQuantity.WING_WIDTH,
        PhysicalQuantity.WING_DEPTH,
        PhysicalQuantity.VELOCITY,
        PhysicalQuantity.DENSITY);
  }

  @Override
  protected double calculateWithoutChecks(Map<PhysicalQuantity, Double> input)
  {
    double dragCoefficient = PhysicalQuantity.PROFILE_DRAG_COEFFICIENT.getValueFromAvailableQuantities(input);
    double wingWidth = PhysicalQuantity.WING_WIDTH.getValueFromAvailableQuantities(input);
    double wingDepth = PhysicalQuantity.WING_DEPTH.getValueFromAvailableQuantities(input);
    double velocity = PhysicalQuantity.VELOCITY.getValueFromAvailableQuantities(input);
    double density = PhysicalQuantity.DENSITY.getValueFromAvailableQuantities(input);

    double area = wingWidth * wingDepth;
    double result = dragCoefficient * velocity * velocity * density * area / 2;
    return result;
  }
}
