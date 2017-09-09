package com.github.thomasfox.sailboatcalculator.calculate.impl;

import java.util.Map;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;

/**
 * https://de.wikipedia.org/wiki/Dynamischer_Auftrieb
 */
public class ProfileDragCalculator extends Calculator
{
  public ProfileDragCalculator()
  {
    super(PhysicalQuantity.PROFILE_DRAG,
        PhysicalQuantity.PROFILE_DRAG_COEFFICIENT,
        PhysicalQuantity.WING_SPAN,
        PhysicalQuantity.WING_CHORD,
        PhysicalQuantity.VELOCITY,
        PhysicalQuantity.DENSITY);
  }

  @Override
  protected double calculateWithoutChecks(Map<PhysicalQuantity, Double> input)
  {
    double dragCoefficient = PhysicalQuantity.PROFILE_DRAG_COEFFICIENT.getValueFromAvailableQuantities(input);
    double wingWidth = PhysicalQuantity.WING_SPAN.getValueFromAvailableQuantities(input);
    double wingDepth = PhysicalQuantity.WING_CHORD.getValueFromAvailableQuantities(input);
    double velocity = PhysicalQuantity.VELOCITY.getValueFromAvailableQuantities(input);
    double density = PhysicalQuantity.DENSITY.getValueFromAvailableQuantities(input);

    double area = wingWidth * wingDepth;
    double result = dragCoefficient * velocity * velocity * density * area / 2;
    return result;
  }
}
