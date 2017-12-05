package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.ValueSet;

/**
 * https://de.wikipedia.org/wiki/Dynamischer_Auftrieb
 */
public class TotalDragCalculator extends Calculator
{
  public TotalDragCalculator()
  {
    super(PhysicalQuantity.TOTAL_DRAG,
        PhysicalQuantity.TOTAL_DRAG_COEFFICIENT,
        PhysicalQuantity.WING_AREA,
        PhysicalQuantity.VELOCITY,
        PhysicalQuantity.DENSITY);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double dragCoefficient = valueSet.getKnownValue(PhysicalQuantity.TOTAL_DRAG_COEFFICIENT).getValue();
    double area = valueSet.getKnownValue(PhysicalQuantity.WING_AREA).getValue();
    double velocity = valueSet.getKnownValue(PhysicalQuantity.VELOCITY).getValue();
    double density = valueSet.getKnownValue(PhysicalQuantity.DENSITY).getValue();

    double drag = dragCoefficient *  velocity * velocity * density * area / 2;
    return drag;
  }
}
