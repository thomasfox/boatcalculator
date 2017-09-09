package com.github.thomasfox.sailboatcalculator.calculate.impl;

import java.util.Map;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;

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
  protected double calculateWithoutChecks(Map<PhysicalQuantity, Double> input)
  {
    double boatSpeed = PhysicalQuantity.VELOCITY.getValueFromAvailableQuantities(input);
    double windSpeed = PhysicalQuantity.WIND_SPEED.getValueFromAvailableQuantities(input);
    double pointingAngle = PhysicalQuantity.POINTING_ANGLE.getValueFromAvailableQuantities(input);
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
