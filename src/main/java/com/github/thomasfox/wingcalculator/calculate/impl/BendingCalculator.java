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
    super(PhysicalQuantity.BENDING,
        PhysicalQuantity.WING_WIDTH,
        PhysicalQuantity.BENDING_FORCE,
        PhysicalQuantity.MODULUS_OF_ELASTICITY,
        PhysicalQuantity.SECOND_MOMENT_OF_AREA);
  }

  @Override
  protected double calculateWithoutChecks(Map<PhysicalQuantity, Double> input)
  {
    double wingWidth = PhysicalQuantity.WING_WIDTH.getValueFromAvailableQuantities(input);
    double bendingForce = PhysicalQuantity.BENDING_FORCE.getValueFromAvailableQuantities(input);
    double modulusOfElasicity = PhysicalQuantity.MODULUS_OF_ELASTICITY.getValueFromAvailableQuantities(input);
    double secondMomentOfArea = PhysicalQuantity.SECOND_MOMENT_OF_AREA.getValueFromAvailableQuantities(input);
    return bendingForce*wingWidth*wingWidth*wingWidth/(modulusOfElasicity*secondMomentOfArea*3);
  }
}
