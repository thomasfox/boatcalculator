package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValues;

public class ApparentWindDirectionCalculator extends Calculator
{
  public ApparentWindDirectionCalculator()
  {
    super(PhysicalQuantity.APPARENT_WIND_ANGLE,
        PhysicalQuantity.VELOCITY,
        PhysicalQuantity.WIND_SPEED,
        PhysicalQuantity.POINTING_ANGLE);
  }

  @Override
  protected double calculateWithoutChecks(PhysicalQuantityValues input)
  {
    double boatSpeed = input.getValue(PhysicalQuantity.VELOCITY);
    double windSpeed = input.getValue(PhysicalQuantity.WIND_SPEED);
    double pointingAngle = input.getValue(PhysicalQuantity.POINTING_ANGLE);
    double apparentWindSpeedAlongBoat = boatSpeed + windSpeed * Math.cos(pointingAngle * Math.PI / 180d);
    double apparentWindSpeedPerpendicularToBoat = windSpeed * Math.sin(pointingAngle * Math.PI / 180d);
    if (Math.abs(apparentWindSpeedAlongBoat) < 1E-20d)
    {
      if (Math.abs(apparentWindSpeedPerpendicularToBoat) < 1E-20d)
      {
        return 0d;
      }
      return 90d;
    }
    double result = Math.atan(apparentWindSpeedPerpendicularToBoat / apparentWindSpeedAlongBoat) * 180d / Math.PI;
    if (result < 0)
    {
      result += 180;
    }
    return result;
  }
}
