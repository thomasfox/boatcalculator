package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.valueset.ValueSet;

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
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double wingDepth = valueSet.getKnownValue(PhysicalQuantity.WING_CHORD).getValue();
    double mormalizedMoment = valueSet.getKnownValue(PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA).getValue();

    return mormalizedMoment*wingDepth*wingDepth*wingDepth*wingDepth;
  }
}
