package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

/**
 * Gibt die Durchbiegung eines Fl�gels, der in der Mitte fest gelagert ist an den Enden frei ist, zur�ck.
 * Der Fl�gel ist �ber die gesamte Fl�che gleichm��ig belastet.
 * TODO passt nicht f�r WING_SPAN_IN_MEDIUM
 */
public class AreaLoadFixedMiddleBendingCalculator extends Calculator
{
  public AreaLoadFixedMiddleBendingCalculator()
  {
    super(PhysicalQuantity.BENDING,
        PhysicalQuantity.WING_SPAN,
        PhysicalQuantity.LIFT,
        PhysicalQuantity.MODULUS_OF_ELASTICITY,
        PhysicalQuantity.SECOND_MOMENT_OF_AREA);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double wingWidth = valueSet.getKnownQuantityValue(PhysicalQuantity.WING_SPAN).getValue();
    double beamLength = wingWidth / 2;
    double force = valueSet.getKnownQuantityValue(PhysicalQuantity.LIFT).getValue();
    double bendingForce = force / 2;
    double modulusOfElasicity = valueSet.getKnownQuantityValue(PhysicalQuantity.MODULUS_OF_ELASTICITY).getValue();
    double secondMomentOfArea = valueSet.getKnownQuantityValue(PhysicalQuantity.SECOND_MOMENT_OF_AREA).getValue();

    return bendingForce*beamLength*beamLength*beamLength/(modulusOfElasicity*secondMomentOfArea*8);
  }
}
