package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

public class AreaInMediumTrapezoidalWingCalculator extends Calculator
{
  public AreaInMediumTrapezoidalWingCalculator()
  {
    super(PhysicalQuantity.AREA_IN_MEDIUM,
        PhysicalQuantity.WING_INNER_CHORD,
        PhysicalQuantity.WING_OUTER_CHORD,
        PhysicalQuantity.WING_SPAN_IN_MEDIUM);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double wingInnerChord = valueSet.getKnownQuantityValue(PhysicalQuantity.WING_INNER_CHORD).getValue();
    double wingOuterChord = valueSet.getKnownQuantityValue(PhysicalQuantity.WING_OUTER_CHORD).getValue();
    double wingSpan = valueSet.getKnownQuantityValue(PhysicalQuantity.WING_SPAN_IN_MEDIUM).getValue();

    return (wingInnerChord + wingOuterChord)*wingSpan/2;
  }
}
