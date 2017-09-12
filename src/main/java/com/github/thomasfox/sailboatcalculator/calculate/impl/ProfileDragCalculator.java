package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantityValues;

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
  protected double calculateWithoutChecks(PhysicalQuantityValues input)
  {
    double dragCoefficient = input.getValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    double wingWidth = input.getValue(PhysicalQuantity.WING_SPAN);
    double wingDepth = input.getValue(PhysicalQuantity.WING_CHORD);
    double velocity = input.getValue(PhysicalQuantity.VELOCITY);
    double density = input.getValue(PhysicalQuantity.DENSITY);

    double area = wingWidth * wingDepth;
    double result = dragCoefficient * velocity * velocity * density * area / 2;
    return result;
  }
}
