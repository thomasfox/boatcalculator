package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

/**
 * Berechnet die Fl�chentiefe aus dem normalisierten
 * Fl�chentr�gheitsmoment f�r eine Fl�che mit Tiefe 1
 * und dem gew�nschten Fl�chentr�gheitsmoment
 */
public class WingChordFromSecondMomentOfAreaCalculator extends Calculator
{
  public WingChordFromSecondMomentOfAreaCalculator()
  {
    super(PhysicalQuantity.WING_CHORD,
        PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA,
        PhysicalQuantity.SECOND_MOMENT_OF_AREA);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double moment = valueSet.getKnownValue(PhysicalQuantity.SECOND_MOMENT_OF_AREA).getValue();
    double mormalizedMoment = valueSet.getKnownValue(PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA).getValue();

    return Math.pow(moment/mormalizedMoment, 0.25);
  }
}
