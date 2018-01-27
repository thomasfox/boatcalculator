package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.valueset.ValueSet;

/**
 * Berechnet die Querschnittsfl�che aus der normalisierten
 * Querschnittsfl�che f�r eine Fl�che mit Tiefe 1.
 */
public class CrosssectionAreaCalculator extends Calculator
{
  public CrosssectionAreaCalculator()
  {
    super(PhysicalQuantity.AREA_OF_CROSSECTION,
        PhysicalQuantity.WING_CHORD,
        PhysicalQuantity.NORMALIZED_AREA_OF_CROSSECTION);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double wingDepth = valueSet.getKnownValue(PhysicalQuantity.WING_CHORD).getValue();
    double normalizedArea = valueSet.getKnownValue(PhysicalQuantity.NORMALIZED_AREA_OF_CROSSECTION).getValue();

    return normalizedArea*wingDepth*wingDepth;
  }
}
