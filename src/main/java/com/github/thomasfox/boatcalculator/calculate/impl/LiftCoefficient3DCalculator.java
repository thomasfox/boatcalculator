package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

/**
 * https://de.wikipedia.org/wiki/Dynamischer_Auftrieb
 */
public class LiftCoefficient3DCalculator extends Calculator
{
  public LiftCoefficient3DCalculator()
  {
    super(PhysicalQuantity.LIFT_COEFFICIENT_3D,
        PhysicalQuantity.LIFT,
        PhysicalQuantity.AREA,
        PhysicalQuantity.VELOCITY,
        PhysicalQuantity.DENSITY);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double lift = valueSet.getKnownValue(PhysicalQuantity.LIFT).getValue();
    double wingArea = valueSet.getKnownValue(PhysicalQuantity.AREA).getValue();
    double velocity = valueSet.getKnownValue(PhysicalQuantity.VELOCITY).getValue();
    double density = valueSet.getKnownValue(PhysicalQuantity.DENSITY).getValue();

    double result = 2 * lift / velocity / velocity / density / wingArea;
    return result;
  }
}
