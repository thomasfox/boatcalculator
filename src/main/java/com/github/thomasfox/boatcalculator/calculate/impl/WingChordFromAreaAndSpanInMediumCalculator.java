package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

/**
 * Berechnet die Flächentiefe aus der Fläche und der Spannweite des Flügels
 */
public class WingChordFromAreaAndSpanInMediumCalculator extends Calculator
{
  public WingChordFromAreaAndSpanInMediumCalculator()
  {
    super(PhysicalQuantity.WING_CHORD,
        PhysicalQuantity.AREA_IN_MEDIUM,
        PhysicalQuantity.WING_SPAN_IN_MEDIUM);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double wingArea = valueSet.getKnownQuantityValue(PhysicalQuantity.AREA_IN_MEDIUM).getValue();
    double wingSpan = valueSet.getKnownQuantityValue(PhysicalQuantity.WING_SPAN_IN_MEDIUM).getValue();

    return wingArea/wingSpan;
  }
}
