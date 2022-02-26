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
    double lift = valueSet.getKnownQuantityValue(PhysicalQuantity.LIFT).getValue();
    double wingArea = valueSet.getKnownQuantityValue(PhysicalQuantity.AREA).getValue();
    double velocity = valueSet.getKnownQuantityValue(PhysicalQuantity.VELOCITY).getValue();
    double density = valueSet.getKnownQuantityValue(PhysicalQuantity.DENSITY).getValue();

    if (velocity == 0d)
    {
      return 0d;
    }
    double result = 2 * lift / velocity / velocity / density / wingArea;
    return result;
  }
}
