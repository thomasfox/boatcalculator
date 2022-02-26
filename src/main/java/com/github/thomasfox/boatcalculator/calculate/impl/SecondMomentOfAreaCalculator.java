package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

/**
 * Berechnet das Fl�chentr�gheitsmoment aus dem normalisierten
 * Fl�chentr�gheitsmoment f�r eine Fl�che mit Tiefe 1
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
    double wingDepth = valueSet.getKnownQuantityValue(PhysicalQuantity.WING_CHORD).getValue();
    double mormalizedMoment = valueSet.getKnownQuantityValue(PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA).getValue();

    return mormalizedMoment*wingDepth*wingDepth*wingDepth*wingDepth;
  }
}
