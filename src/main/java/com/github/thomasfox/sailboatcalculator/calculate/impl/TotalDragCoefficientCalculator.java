package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValues;

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
  protected double calculateWithoutChecks(PhysicalQuantityValues input)
  {
    double profileDragCoefficient = input.getValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    double inducedDragCoefficient = input.getValue(PhysicalQuantity.INDUCED_DRAG_COEFFICIENT);
    double waveMakingDragCoefficient = input.getValue(PhysicalQuantity.WAVE_MAKING_DRAG_COEFFICIENT);

    double totalDragCoefficient = profileDragCoefficient + inducedDragCoefficient + waveMakingDragCoefficient;
    return totalDragCoefficient;
  }
}
