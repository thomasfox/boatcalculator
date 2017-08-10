package com.github.thomasfox.wingcalculator.calculate.impl;

import java.util.Map;

import com.github.thomasfox.wingcalculator.calculate.Calculator;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;

/**
 * Gibt die Durchbiegung eines Flügels, das an einem Ende fest gelagert
 * und am anderen Ende mit einer Kraft Quer zum Flügel belastet wird, zurück.
 * Siehe Joos, Lehrbuch der Theoretischen Physik, 15. Auflage, S. 169
 */
public class SecondMomentOfAreaCalculator extends Calculator
{
  public SecondMomentOfAreaCalculator()
  {
    super(PhysicalQuantity.SECOND_MOMENT_OF_AREA,
        PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA);
  }

  @Override
  protected double calculateWithoutChecks(Map<PhysicalQuantity, Double> input)
  {
    double wingDepth = PhysicalQuantity.WING_DEPTH.getValueFromAvailableQuantities(input);
    double mormalizedMoment = PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA.getValueFromAvailableQuantities(input);
    return mormalizedMoment*wingDepth*wingDepth*wingDepth*wingDepth;
  }
}
