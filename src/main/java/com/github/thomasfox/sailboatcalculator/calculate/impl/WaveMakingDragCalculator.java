package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.ValueSet;

/**
 * https://de.wikipedia.org/wiki/Dynamischer_Auftrieb
 */
public class WaveMakingDragCalculator extends Calculator
{
  public WaveMakingDragCalculator()
  {
    super(PhysicalQuantity.WAVE_MAKING_DRAG,
        PhysicalQuantity.WAVE_MAKING_DRAG_COEFFICIENT,
        PhysicalQuantity.WING_AREA,
        PhysicalQuantity.VELOCITY,
        PhysicalQuantity.DENSITY);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double dragCoefficient = valueSet.getKnownValue(PhysicalQuantity.WAVE_MAKING_DRAG_COEFFICIENT).getValue();
    double wingArea = valueSet.getKnownValue(PhysicalQuantity.WING_AREA).getValue();
    double velocity = valueSet.getKnownValue(PhysicalQuantity.VELOCITY).getValue();
    double density = valueSet.getKnownValue(PhysicalQuantity.DENSITY).getValue();

    double result = dragCoefficient * velocity * velocity * density * wingArea / 2;
    return result;
  }
}