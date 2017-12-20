package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.valueset.ValueSet;

/**
 * https://de.wikipedia.org/wiki/Dynamischer_Auftrieb
 */
public class ProfileDragCalculator extends Calculator
{
  public ProfileDragCalculator()
  {
    super(PhysicalQuantity.PROFILE_DRAG,
        PhysicalQuantity.PROFILE_DRAG_COEFFICIENT,
        PhysicalQuantity.AREA,
        PhysicalQuantity.VELOCITY,
        PhysicalQuantity.DENSITY);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double dragCoefficient = valueSet.getKnownValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT).getValue();
    double wingArea = valueSet.getKnownValue(PhysicalQuantity.AREA).getValue();
    double velocity = valueSet.getKnownValue(PhysicalQuantity.VELOCITY).getValue();
    double density = valueSet.getKnownValue(PhysicalQuantity.DENSITY).getValue();

    double result = dragCoefficient * velocity * velocity * density * wingArea / 2;
    return result;
  }
}
