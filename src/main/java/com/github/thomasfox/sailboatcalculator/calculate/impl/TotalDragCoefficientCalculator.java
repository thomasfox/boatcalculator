package com.github.thomasfox.sailboatcalculator.calculate.impl;

import java.util.Map;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;

/**
 * https://de.wikipedia.org/wiki/Dynamischer_Auftrieb
 */
public class TotalDragCoefficientCalculator extends Calculator
{
  public TotalDragCoefficientCalculator()
  {
    super(PhysicalQuantity.TOTAL_DRAG_COEFFICIENT,
        PhysicalQuantity.PROFILE_DRAG_COEFFICIENT,
        PhysicalQuantity.INDUCED_DRAG_COEFFICIENT);
  }

  @Override
  protected double calculateWithoutChecks(Map<PhysicalQuantity, Double> input)
  {
    double profileDragCoefficient = PhysicalQuantity.PROFILE_DRAG_COEFFICIENT.getValueFromAvailableQuantities(input);
    double inducedDragCoefficient = PhysicalQuantity.INDUCED_DRAG_COEFFICIENT.getValueFromAvailableQuantities(input);

    double totalDragCoefficient = profileDragCoefficient + inducedDragCoefficient;
    return totalDragCoefficient;
  }
}
