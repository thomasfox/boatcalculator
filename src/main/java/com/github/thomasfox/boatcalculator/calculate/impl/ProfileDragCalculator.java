package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

/**
 * https://de.wikipedia.org/wiki/Dynamischer_Auftrieb
 */
public class ProfileDragCalculator extends Calculator
{
  public ProfileDragCalculator()
  {
    super(PhysicalQuantity.PROFILE_DRAG,
        PhysicalQuantity.PROFILE_DRAG_COEFFICIENT,
        PhysicalQuantity.AREA_IN_MEDIUM,
        PhysicalQuantity.VELOCITY,
        PhysicalQuantity.DENSITY);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double dragCoefficient = valueSet.getKnownQuantityValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT).getValue();
    double wingArea = valueSet.getKnownQuantityValue(PhysicalQuantity.AREA_IN_MEDIUM).getValue();
    double velocity = valueSet.getKnownQuantityValue(PhysicalQuantity.VELOCITY).getValue();
    double density = valueSet.getKnownQuantityValue(PhysicalQuantity.DENSITY).getValue();

    double result = dragCoefficient * velocity * velocity * density * wingArea / 2;
    return result;
  }
}
