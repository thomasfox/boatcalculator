package com.github.thomasfox.sailboatcalculator.calculate.impl;

import java.util.Map;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;

/**
 * Berechnet das Flächenträgheitsmoment aus dem normalisierten
 * Flächenträgheitsmoment für eine Fläche mit Tiefe 1
 */
public class SecondMomentOfAreaCalculator extends Calculator
{
  public SecondMomentOfAreaCalculator()
  {
    super(PhysicalQuantity.SECOND_MOMENT_OF_AREA,
        PhysicalQuantity.WING_CHORD,
        PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA);
  }

  @Override
  protected double calculateWithoutChecks(Map<PhysicalQuantity, Double> input)
  {
    double wingDepth = PhysicalQuantity.WING_CHORD.getValueFromAvailableQuantities(input);
    double mormalizedMoment = PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA.getValueFromAvailableQuantities(input);
    return mormalizedMoment*wingDepth*wingDepth*wingDepth*wingDepth;
  }
}
