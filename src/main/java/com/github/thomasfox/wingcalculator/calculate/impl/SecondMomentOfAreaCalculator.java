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
    super(PhysicalQuantity.WING_DEPTH,
        PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA);
  }

  @Override
  protected double calculateWithoutChecks(Map<PhysicalQuantity, Double> input)
  {
    double wingDepth = getValueOf(PhysicalQuantity.WING_DEPTH, input);
    double mormalizedMoment = getValueOf(PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA, input);
    return mormalizedMoment*wingDepth*wingDepth*wingDepth*wingDepth;
  }
}
