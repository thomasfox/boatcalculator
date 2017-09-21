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
        PhysicalQuantity.WING_CHORD,
        PhysicalQuantity.LIFT_COEFFICIENT);
  }

  @Override
  protected double calculateWithoutChecks(PhysicalQuantityValues input)
  {
    double wingWidth = input.getValue(PhysicalQuantity.WING_SPAN);
    double wingDepth = input.getValue(PhysicalQuantity.WING_CHORD);
    double liftCoefficient = input.getValue(PhysicalQuantity.LIFT_COEFFICIENT);

    double aspectRatio = wingWidth / wingDepth;
    double result = liftCoefficient * liftCoefficient / Math.PI / aspectRatio;
    return result;
  }
}
