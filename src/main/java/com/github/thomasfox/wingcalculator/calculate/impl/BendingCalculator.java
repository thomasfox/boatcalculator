package com.github.thomasfox.wingcalculator.calculate.impl;

import java.util.Map;

import com.github.thomasfox.wingcalculator.calculate.Calculator;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;

/**
 * Gibt die Durchbiegung eines Flügels, das an einem Ende fest gelagert
 * und am anderen Ende mit einer Kraft Quer zum Flügel belastet wird, zurück.
 * Siehe Joos, Lehrbuch der Theoretischen Physik, 15. Auflage, S. 169
 */
public class BendingCalculator extends Calculator
{
  public BendingCalculator()
  {
    super(PhysicalQuantity.WING_WIDTH,
        PhysicalQuantity.BENDING_FORCE,
        PhysicalQuantity.MODULUS_OF_ELASTICITY,
        PhysicalQuantity.SECOND_MOMENT_OF_AREA);
  }

  @Override
  protected double calculateWithoutChecks(Map<PhysicalQuantity, Double> input)
  {
    double wingWidth = getValueOf(PhysicalQuantity.WING_WIDTH, input);
    double bendingForce = getValueOf(PhysicalQuantity.BENDING_FORCE, input);
    double modulusOfElasicity = getValueOf(PhysicalQuantity.MODULUS_OF_ELASTICITY, input);
    double secondMomentOfArea = getValueOf(PhysicalQuantity.SECOND_MOMENT_OF_AREA, input);
    return bendingForce*wingWidth*wingWidth*wingWidth/(modulusOfElasicity*secondMomentOfArea*3);
  }
}
