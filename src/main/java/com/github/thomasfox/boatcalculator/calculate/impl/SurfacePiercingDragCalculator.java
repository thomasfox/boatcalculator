package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

/**
 * https://de.wikipedia.org/wiki/Dynamischer_Auftrieb
 */
public class SurfacePiercingDragCalculator extends Calculator
{
  public SurfacePiercingDragCalculator()
  {
    super(PhysicalQuantity.WAVE_MAKING_DRAG,
        PhysicalQuantity.WAVE_MAKING_DRAG_COEFFICIENT,
        PhysicalQuantity.AREA,
        PhysicalQuantity.VELOCITY,
        PhysicalQuantity.DENSITY);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double dragCoefficient = valueSet.getKnownQuantityValue(PhysicalQuantity.WAVE_MAKING_DRAG_COEFFICIENT).getValue();
    double wingArea = valueSet.getKnownQuantityValue(PhysicalQuantity.AREA).getValue();
    double velocity = valueSet.getKnownQuantityValue(PhysicalQuantity.VELOCITY).getValue();
    double density = valueSet.getKnownQuantityValue(PhysicalQuantity.DENSITY).getValue();

    double result = dragCoefficient * velocity * velocity * density * wingArea / 2;
    return result;
  }
}
