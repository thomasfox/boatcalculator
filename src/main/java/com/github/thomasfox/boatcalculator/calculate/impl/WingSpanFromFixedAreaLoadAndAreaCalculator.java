package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

/**
 * Gibt die Durchbiegung eines Flügels, der in der Mitte fest gelagert ist an den Enden frei ist, zurück.
 * Der Flügel ist über die gesamte Fläche gleichmäßig belastet.
 * TODO passt nicht für WING_SPAN_IN_MEDIUM
 */
public class WingSpanFromFixedAreaLoadAndAreaCalculator extends Calculator
{
  public WingSpanFromFixedAreaLoadAndAreaCalculator()
  {
    super(PhysicalQuantity.WING_SPAN,
        PhysicalQuantity.BENDING,
        PhysicalQuantity.LIFT,
        PhysicalQuantity.AREA_IN_MEDIUM,
        PhysicalQuantity.MODULUS_OF_ELASTICITY,
        PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double bending = valueSet.getKnownQuantityValue(PhysicalQuantity.BENDING).getValue();
    double lift = valueSet.getKnownQuantityValue(PhysicalQuantity.LIFT).getValue();
    double area = valueSet.getKnownQuantityValue(PhysicalQuantity.AREA_IN_MEDIUM).getValue();
    double modulusOfElasicity = valueSet.getKnownQuantityValue(PhysicalQuantity.MODULUS_OF_ELASTICITY).getValue();
    double normalzedSecondMomentOfArea = valueSet.getKnownQuantityValue(PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA).getValue();

    return Math.pow(128*bending*modulusOfElasicity*normalzedSecondMomentOfArea*area*area*area*area/lift, 1d/7d);
  }
}
