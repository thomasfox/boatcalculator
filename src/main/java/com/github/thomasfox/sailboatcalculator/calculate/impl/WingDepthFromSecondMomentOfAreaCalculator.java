package com.github.thomasfox.sailboatcalculator.calculate.impl;

import java.util.Map;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;

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
  protected double calculateWithoutChecks(Map<PhysicalQuantity, Double> input)
  {
    double moment = PhysicalQuantity.SECOND_MOMENT_OF_AREA.getValueFromAvailableQuantities(input);
    double mormalizedMoment = PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA.getValueFromAvailableQuantities(input);
    return Math.pow(moment/mormalizedMoment, 0.25);
  }
}
