package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

/**
 * Gibt die Durchbiegung eines Flügels, das an einem Ende fest gelagert
 * und am anderen Ende mit einer Kraft Quer zum Flügel belastet wird, zurück.
 * Siehe Joos, Lehrbuch der Theoretischen Physik, 15. Auflage, S. 169
 */
public class PointLoadBendingCalculator extends Calculator
{
  public PointLoadBendingCalculator()
  {
    super(PhysicalQuantity.BENDING,
        PhysicalQuantity.WING_SPAN,
        PhysicalQuantity.BENDING_FORCE,
        PhysicalQuantity.MODULUS_OF_ELASTICITY,
        PhysicalQuantity.SECOND_MOMENT_OF_AREA);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double wingWidth = valueSet.getKnownValue(PhysicalQuantity.WING_SPAN).getValue();
    double bendingForce = valueSet.getKnownValue(PhysicalQuantity.BENDING_FORCE).getValue();
    double modulusOfElasicity = valueSet.getKnownValue(PhysicalQuantity.MODULUS_OF_ELASTICITY).getValue();
    double secondMomentOfArea = valueSet.getKnownValue(PhysicalQuantity.SECOND_MOMENT_OF_AREA).getValue();

    return bendingForce*wingWidth*wingWidth*wingWidth/(modulusOfElasicity*secondMomentOfArea*3);
  }
}
