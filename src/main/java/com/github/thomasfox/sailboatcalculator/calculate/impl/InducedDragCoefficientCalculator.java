package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValues;

/**
 * https://de.wikipedia.org/wiki/Induzierter_Luftwiderstand
 * Laut Hoerner Oswaldfaktor in der Größenordnung von 1 % -> wird vernachlaessigt
 */
public class InducedDragCoefficientCalculator extends Calculator
{
  public InducedDragCoefficientCalculator()
  {
    super(PhysicalQuantity.INDUCED_DRAG_COEFFICIENT,
        PhysicalQuantity.WING_SPAN,
        PhysicalQuantity.WING_AREA,
        PhysicalQuantity.LIFT_COEFFICIENT);
  }

  @Override
  protected double calculateWithoutChecks(PhysicalQuantityValues input)
  {
    double wingSpan = input.getValue(PhysicalQuantity.WING_SPAN);
    double wingArea = input.getValue(PhysicalQuantity.WING_AREA);
    double liftCoefficient = input.getValue(PhysicalQuantity.LIFT_COEFFICIENT);

    double aspectRatio = wingSpan *wingSpan / wingArea;
    double result = liftCoefficient * liftCoefficient / Math.PI / aspectRatio;
    return result;
  }
}
