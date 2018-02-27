package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

/**
 * https://de.wikipedia.org/wiki/Dynamischer_Auftrieb
 */
public class TotalDragCalculator extends Calculator
{
  public TotalDragCalculator()
  {
    super(PhysicalQuantity.TOTAL_DRAG,
        PhysicalQuantity.TOTAL_DRAG_COEFFICIENT,
        PhysicalQuantity.AREA,
        PhysicalQuantity.VELOCITY,
        PhysicalQuantity.DENSITY);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double dragCoefficient = valueSet.getKnownValue(PhysicalQuantity.TOTAL_DRAG_COEFFICIENT).getValue();
    double area = valueSet.getKnownValue(PhysicalQuantity.AREA).getValue();
    double velocity = valueSet.getKnownValue(PhysicalQuantity.VELOCITY).getValue();
    double density = valueSet.getKnownValue(PhysicalQuantity.DENSITY).getValue();

    double drag = dragCoefficient *  velocity * velocity * density * area / 2;
    return drag;
  }
}
