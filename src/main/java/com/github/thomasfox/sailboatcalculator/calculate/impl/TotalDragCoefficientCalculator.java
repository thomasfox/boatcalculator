package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.ValueSet;

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
        PhysicalQuantity.WAVE_MAKING_DRAG_COEFFICIENT);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double profileDragCoefficient = valueSet.getKnownValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT).getValue();
    double inducedDragCoefficient = valueSet.getKnownValue(PhysicalQuantity.INDUCED_DRAG_COEFFICIENT).getValue();
    double waveMakingDragCoefficient = valueSet.getKnownValue(PhysicalQuantity.WAVE_MAKING_DRAG_COEFFICIENT).getValue();

    double totalDragCoefficient = profileDragCoefficient + inducedDragCoefficient + waveMakingDragCoefficient;
    return totalDragCoefficient;
  }
}
