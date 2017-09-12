package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantityValues;

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
  protected double calculateWithoutChecks(PhysicalQuantityValues input)
  {
    double wingDepth = input.getValue(PhysicalQuantity.WING_CHORD);
    double mormalizedMoment = input.getValue(PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA);

    return mormalizedMoment*wingDepth*wingDepth*wingDepth*wingDepth;
  }
}
