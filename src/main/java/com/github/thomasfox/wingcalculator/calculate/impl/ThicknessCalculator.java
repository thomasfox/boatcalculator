package com.github.thomasfox.wingcalculator.calculate.impl;

import java.util.Map;

import com.github.thomasfox.wingcalculator.calculate.Calculator;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;

/**
 * Gibt die Durchbiegung eines Flügels, das an einem Ende fest gelagert
 * und am anderen Ende mit einer Kraft Quer zum Flügel belastet wird, zurück.
 * Siehe Joos, Lehrbuch der Theoretischen Physik, 15. Auflage, S. 169
 */
public class ThicknessCalculator extends Calculator
{
  public ThicknessCalculator()
  {
    super(PhysicalQuantity.WING_THICKNESS,
        PhysicalQuantity.WING_RELATIVE_THICKNESS,
        PhysicalQuantity.WING_DEPTH);
  }

  @Override
  protected double calculateWithoutChecks(Map<PhysicalQuantity, Double> input)
  {
    double wingDepth = getValueOf(PhysicalQuantity.WING_DEPTH, input);
    double relativeThickness = getValueOf(PhysicalQuantity.WING_RELATIVE_THICKNESS, input);
    return wingDepth*relativeThickness;
  }
}
