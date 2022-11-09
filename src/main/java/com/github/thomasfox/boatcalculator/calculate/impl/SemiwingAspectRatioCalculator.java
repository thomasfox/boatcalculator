package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

public class SemiwingAspectRatioCalculator extends Calculator
{
  public SemiwingAspectRatioCalculator()
  {
    super(PhysicalQuantity.SEMIWING_ASPECT_RATIO,
        PhysicalQuantity.HALFWING_SPAN,
        PhysicalQuantity.AREA_IN_MEDIUM);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double halfwingSpan = valueSet.getKnownQuantityValue(PhysicalQuantity.HALFWING_SPAN).getValue();
    double wingArea = valueSet.getKnownQuantityValue(PhysicalQuantity.AREA_IN_MEDIUM).getValue();

    double aspectRatio = halfwingSpan * halfwingSpan / wingArea;
    return aspectRatio;
  }
}
