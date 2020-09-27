package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

/**
 * Berechnet die Flächentiefe aus der Fläche und der Spannweite des Flügels
 */
public class WingChordFromAreaAndSpanCalculator extends Calculator
{
  public WingChordFromAreaAndSpanCalculator()
  {
    super(PhysicalQuantity.WING_CHORD,
        PhysicalQuantity.AREA,
        PhysicalQuantity.WING_SPAN);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double wingArea = valueSet.getKnownValue(PhysicalQuantity.AREA).getValue();
    double wingSpan = valueSet.getKnownValue(PhysicalQuantity.WING_SPAN).getValue();

    return wingArea/wingSpan;
  }
}
