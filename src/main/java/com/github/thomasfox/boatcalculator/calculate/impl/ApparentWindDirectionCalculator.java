package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

public class ApparentWindDirectionCalculator extends Calculator
{
  public ApparentWindDirectionCalculator()
  {
    super(PhysicalQuantity.APPARENT_WIND_ANGLE,
        PhysicalQuantity.VELOCITY,
        PhysicalQuantity.WIND_SPEED,
        PhysicalQuantity.SAILING_ANGLE);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double boatSpeed = valueSet.getKnownValue(PhysicalQuantity.VELOCITY).getValue();
    double windSpeed = valueSet.getKnownValue(PhysicalQuantity.WIND_SPEED).getValue();
    double pointingAngle = valueSet.getKnownValue(PhysicalQuantity.SAILING_ANGLE).getValue();

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
