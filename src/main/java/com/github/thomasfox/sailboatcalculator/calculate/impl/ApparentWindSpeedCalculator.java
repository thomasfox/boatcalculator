package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValues;

public class ApparentWindSpeedCalculator extends Calculator
{
  public ApparentWindSpeedCalculator()
  {
    super(PhysicalQuantity.APPARENT_WIND_SPEED,
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
    return Math.sqrt(apparentWindSpeedAlongBoat *apparentWindSpeedAlongBoat
        + apparentWindSpeedPerpendicularToBoat * apparentWindSpeedPerpendicularToBoat);
  }
}
