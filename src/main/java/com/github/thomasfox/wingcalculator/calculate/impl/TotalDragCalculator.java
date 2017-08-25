package com.github.thomasfox.wingcalculator.calculate.impl;

import java.util.Map;

import com.github.thomasfox.wingcalculator.calculate.Calculator;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;

/**
 * https://de.wikipedia.org/wiki/Dynamischer_Auftrieb
 */
public class TotalDragCalculator extends Calculator
{
  public TotalDragCalculator()
  {
    super(PhysicalQuantity.TOTAL_DRAG,
        PhysicalQuantity.TOTAL_DRAG_COEFFICIENT,
        PhysicalQuantity.WING_SPAN,
        PhysicalQuantity.WING_CHORD,
        PhysicalQuantity.VELOCITY,
        PhysicalQuantity.DENSITY);
  }

  @Override
  protected double calculateWithoutChecks(Map<PhysicalQuantity, Double> input)
  {
    double dragCoefficient = PhysicalQuantity.TOTAL_DRAG_COEFFICIENT.getValueFromAvailableQuantities(input);
    double wingWidth = PhysicalQuantity.WING_SPAN.getValueFromAvailableQuantities(input);
    double wingDepth = PhysicalQuantity.WING_CHORD.getValueFromAvailableQuantities(input);
    double velocity = PhysicalQuantity.VELOCITY.getValueFromAvailableQuantities(input);
    double density = PhysicalQuantity.DENSITY.getValueFromAvailableQuantities(input);

    double area = wingWidth * wingDepth;
    double drag = dragCoefficient *  velocity * velocity * density * area / 2;
    return drag;
  }
}
