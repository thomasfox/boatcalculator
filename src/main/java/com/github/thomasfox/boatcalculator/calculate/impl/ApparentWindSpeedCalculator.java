package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

public class ApparentWindSpeedCalculator extends Calculator
{
  public ApparentWindSpeedCalculator()
  {
    super(PhysicalQuantity.APPARENT_WIND_SPEED,
        PhysicalQuantity.VELOCITY,
        PhysicalQuantity.WIND_SPEED,
        PhysicalQuantity.SAILING_ANGLE);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double boatSpeed = valueSet.getKnownQuantityValue(PhysicalQuantity.VELOCITY).getValue();
    double windSpeed = valueSet.getKnownQuantityValue(PhysicalQuantity.WIND_SPEED).getValue();
    double pointingAngle = valueSet.getKnownQuantityValue(PhysicalQuantity.SAILING_ANGLE).getValue();

    double apparentWindSpeedAlongBoat = boatSpeed + windSpeed * Math.cos(pointingAngle * Math.PI / 180d);
    double apparentWindSpeedPerpendicularToBoat = windSpeed * Math.sin(pointingAngle * Math.PI / 180d);
    return Math.sqrt(apparentWindSpeedAlongBoat *apparentWindSpeedAlongBoat
        + apparentWindSpeedPerpendicularToBoat * apparentWindSpeedPerpendicularToBoat);
  }
}
