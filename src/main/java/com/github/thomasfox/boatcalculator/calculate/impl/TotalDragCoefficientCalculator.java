package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

/**
 * https://de.wikipedia.org/wiki/Dynamischer_Auftrieb
 */
public class TotalDragCoefficientCalculator extends Calculator
{
  public TotalDragCoefficientCalculator()
  {
    super(PhysicalQuantity.TOTAL_DRAG_COEFFICIENT,
        PhysicalQuantity.PROFILE_DRAG_COEFFICIENT,
        PhysicalQuantity.INDUCED_DRAG_COEFFICIENT,
        PhysicalQuantity.WAVE_MAKING_DRAG_COEFFICIENT,
        PhysicalQuantity.SURFACE_PIERCING_DRAG_COEFFICIENT);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double profileDragCoefficient = valueSet.getKnownQuantityValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT).getValue();
    double inducedDragCoefficient = valueSet.getKnownQuantityValue(PhysicalQuantity.INDUCED_DRAG_COEFFICIENT).getValue();
    double waveMakingDragCoefficient = valueSet.getKnownQuantityValue(PhysicalQuantity.WAVE_MAKING_DRAG_COEFFICIENT).getValue();
    double surfacePiercingDragCoefficient = valueSet.getKnownQuantityValue(PhysicalQuantity.SURFACE_PIERCING_DRAG_COEFFICIENT).getValue();

    double totalDragCoefficient = profileDragCoefficient + inducedDragCoefficient + waveMakingDragCoefficient + surfacePiercingDragCoefficient;
    return totalDragCoefficient;
  }
}
