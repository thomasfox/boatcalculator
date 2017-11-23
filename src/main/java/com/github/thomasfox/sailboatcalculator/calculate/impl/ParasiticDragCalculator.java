package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValues;

/**
 * https://de.wikipedia.org/wiki/Dynamischer_Auftrieb
 */
public class ParasiticDragCalculator extends Calculator
{
  public ParasiticDragCalculator()
  {
    super(PhysicalQuantity.PARASITIC_DRAG,
        PhysicalQuantity.PARASITIC_DRAG_COEFFICIENT,
        PhysicalQuantity.WING_AREA,
        PhysicalQuantity.VELOCITY,
        PhysicalQuantity.DENSITY);
  }

  @Override
  protected double calculateWithoutChecks(PhysicalQuantityValues input)
  {
    double dragCoefficient = input.getValue(PhysicalQuantity.PARASITIC_DRAG_COEFFICIENT);
    double wingArea = input.getValue(PhysicalQuantity.WING_AREA);
    double velocity = input.getValue(PhysicalQuantity.VELOCITY);
    double density = input.getValue(PhysicalQuantity.DENSITY);

    double result = dragCoefficient * velocity * velocity * density * wingArea / 2;
    return result;
  }
}