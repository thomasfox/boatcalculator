package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValues;

/**
 * Berechnet die Fl�chentiefe aus dem normalisierten
 * Fl�chentr�gheitsmoment f�r eine Fl�che mit Tiefe 1
 * und dem gew�nschten Fl�chentr�gheitsmoment
 */
public class WingDepthFromSecondMomentOfAreaCalculator extends Calculator
{
  public WingDepthFromSecondMomentOfAreaCalculator()
  {
    super(PhysicalQuantity.WING_CHORD,
        PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA,
        PhysicalQuantity.SECOND_MOMENT_OF_AREA);
  }

  @Override
  protected double calculateWithoutChecks(PhysicalQuantityValues input)
  {
    double moment = input.getValue(PhysicalQuantity.SECOND_MOMENT_OF_AREA);
    double mormalizedMoment = input.getValue(PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA);

    return Math.pow(moment/mormalizedMoment, 0.25);
  }
}
