package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValues;

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
        PhysicalQuantity.WING_SPAN,
        PhysicalQuantity.BENDING_FORCE,
        PhysicalQuantity.MODULUS_OF_ELASTICITY,
        PhysicalQuantity.SECOND_MOMENT_OF_AREA);
  }

  @Override
  protected double calculateWithoutChecks(PhysicalQuantityValues input)
  {
    double wingWidth = input.getValue(PhysicalQuantity.WING_SPAN);
    double bendingForce = input.getValue(PhysicalQuantity.BENDING_FORCE);
    double modulusOfElasicity = input.getValue(PhysicalQuantity.MODULUS_OF_ELASTICITY);
    double secondMomentOfArea = input.getValue(PhysicalQuantity.SECOND_MOMENT_OF_AREA);
    return bendingForce*wingWidth*wingWidth*wingWidth/(modulusOfElasicity*secondMomentOfArea*3);
  }
}
