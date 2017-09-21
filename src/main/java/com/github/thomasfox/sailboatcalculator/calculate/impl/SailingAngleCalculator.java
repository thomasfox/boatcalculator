package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValues;

public class SailingAngleCalculator extends Calculator
{
  public SailingAngleCalculator()
  {
    super(PhysicalQuantity.SAILING_ANGLE,
        PhysicalQuantity.POINTING_ANGLE,
        PhysicalQuantity.DRIFT_ANGLE);
  }

  @Override
  protected double calculateWithoutChecks(PhysicalQuantityValues input)
  {
    double pointingAngle = input.getValue(PhysicalQuantity.POINTING_ANGLE);
    double driftAngle = input.getValue(PhysicalQuantity.DRIFT_ANGLE);
    if (pointingAngle < 0 || pointingAngle > 360)
    {
      throw new IllegalArgumentException("invalid pointing angle " + pointingAngle
          + "(must be between 0 and 360 degrees");
    }
    double result = pointingAngle + driftAngle;
    return result;
  }
}
