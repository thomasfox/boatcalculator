package com.github.thomasfox.wingcalculator.calculate.impl;

import java.util.Map;

import com.github.thomasfox.wingcalculator.calculate.Calculator;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;

/**
 * https://de.wikipedia.org/wiki/Induzierter_Luftwiderstand
 * Laut Hoerner Oswaldfaktor in der Größenordnung von 1 % -> wird vernachlaessigt
 */
public class InducedResistanceCoefficientCalculator extends Calculator
{
  public InducedResistanceCoefficientCalculator()
  {
    super(PhysicalQuantity.INDUCED_DRAG_COEFFICIENT,
        PhysicalQuantity.WING_WIDTH,
        PhysicalQuantity.WING_DEPTH,
        PhysicalQuantity.LIFT_COEFFICIENT);
  }

  @Override
  protected double calculateWithoutChecks(Map<PhysicalQuantity, Double> input)
  {
    double wingWidth = PhysicalQuantity.WING_WIDTH.getValueFromAvailableQuantities(input);
    double wingDepth = PhysicalQuantity.WING_DEPTH.getValueFromAvailableQuantities(input);
    double liftCoefficient = PhysicalQuantity.LIFT_COEFFICIENT.getValueFromAvailableQuantities(input);

    double aspectRatio = wingWidth / wingDepth;
    double result = liftCoefficient * liftCoefficient / Math.PI / aspectRatio;
    return result;
  }
}
