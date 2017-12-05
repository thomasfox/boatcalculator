package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.ValueSet;

public class SailingAngleCalculator extends Calculator
{
  public SailingAngleCalculator()
  {
    super(PhysicalQuantity.SAILING_ANGLE,
        PhysicalQuantity.POINTING_ANGLE,
        PhysicalQuantity.DRIFT_ANGLE);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double pointingAngle = valueSet.getKnownValue(PhysicalQuantity.POINTING_ANGLE).getValue();
    double driftAngle = valueSet.getKnownValue(PhysicalQuantity.DRIFT_ANGLE).getValue();

    if (pointingAngle < 0 || pointingAngle > 360)
    {
      throw new IllegalArgumentException("invalid pointing angle " + pointingAngle
          + "(must be between 0 and 360 degrees");
    }
    double result = pointingAngle + driftAngle;
    return result;
  }
}
