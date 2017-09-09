package com.github.thomasfox.sailboatcalculator.calculate.impl;

import java.util.Map;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;

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
  protected double calculateWithoutChecks(Map<PhysicalQuantity, Double> input)
  {
    double boatSpeed = PhysicalQuantity.VELOCITY.getValueFromAvailableQuantities(input);
    double windSpeed = PhysicalQuantity.WIND_SPEED.getValueFromAvailableQuantities(input);
    double pointingAngle = PhysicalQuantity.POINTING_ANGLE.getValueFromAvailableQuantities(input);
    double apparentWindSpeedAlongBoat = boatSpeed + windSpeed * Math.cos(pointingAngle * Math.PI / 180d);
    double apparentWindSpeedPerpendicularToBoat = windSpeed * Math.sin(pointingAngle * Math.PI / 180d);
    return Math.sqrt(apparentWindSpeedAlongBoat *apparentWindSpeedAlongBoat
        + apparentWindSpeedPerpendicularToBoat * apparentWindSpeedPerpendicularToBoat);
  }
}
