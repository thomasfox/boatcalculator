package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.ValueSet;

/**
 * Berechnet die Flächentiefe aus dem normalisierten
 * Flächenträgheitsmoment für eine Fläche mit Tiefe 1
 * und dem gewünschten Flächenträgheitsmoment
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
