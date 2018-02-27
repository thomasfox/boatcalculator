package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

/**
 * https://de.wikipedia.org/wiki/Induzierter_Luftwiderstand
 * Laut Hoerner Oswaldfaktor in der Gr��enordnung von 1 % -> wird vernachlaessigt
 */
public class InducedDragCoefficientCalculator extends Calculator
{
  public InducedDragCoefficientCalculator()
  {
    super(PhysicalQuantity.INDUCED_DRAG_COEFFICIENT,
        PhysicalQuantity.WING_SPAN,
        PhysicalQuantity.AREA,
        PhysicalQuantity.LIFT_COEFFICIENT);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double wingSpan = valueSet.getKnownValue(PhysicalQuantity.WING_SPAN).getValue();
    double wingArea = valueSet.getKnownValue(PhysicalQuantity.AREA).getValue();
    double liftCoefficient = valueSet.getKnownValue(PhysicalQuantity.LIFT_COEFFICIENT).getValue();

    double aspectRatio = wingSpan *wingSpan / wingArea;
    double result = liftCoefficient * liftCoefficient / Math.PI / aspectRatio;
    return result;
  }
}
