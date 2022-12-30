package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

public class AngleOfAttackFromAngleToHorizontalCalculator extends Calculator
{
  public AngleOfAttackFromAngleToHorizontalCalculator()
  {
    super(PhysicalQuantity.ANGLE_OF_ATTACK,
        PhysicalQuantity.WINDWARD_HEEL_ANGLE,
        PhysicalQuantity.SIDEWAY_ANGLE,
        PhysicalQuantity.BACKWAY_ANGLE,
        PhysicalQuantity.APPARENT_WIND_ANGLE);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    // gamma=SIDEWAY_ANGLE + windwardHeelAngle, delta=BACKWAY_ANGLE,alpha=APPARENT_WIND_ANGLE
    // Anströmwinkel ANGLE_OF_ATTACK beta ist gegeben durch
    // sin(beta) = Skalarprodukt von Flächennormalen und scheinbarem Windrichtungsvektor
    // Flächennormale ist: x=sin(delta), y=sin(gamma) cos(delta), z= cos(gamma)cos(delta)
    // scheinbarer Windrichtungsvektor ist: x=cos(alpha),y=-sin(alpha), z=0
    double sidewayAngleInRadians =
        -(valueSet.getKnownQuantityValue(PhysicalQuantity.SIDEWAY_ANGLE).getValue()
          + valueSet.getKnownQuantityValue(PhysicalQuantity.WINDWARD_HEEL_ANGLE).getValue())*Math.PI/180;
    double backwayAngleInRadians = valueSet.getKnownQuantityValue(PhysicalQuantity.BACKWAY_ANGLE).getValue()*Math.PI/180;
    double apparentWindAngleInRadians = valueSet.getKnownQuantityValue(PhysicalQuantity.APPARENT_WIND_ANGLE).getValue()*Math.PI/180;

    double scalarProduct = Math.sin(backwayAngleInRadians)*Math.cos(apparentWindAngleInRadians)
        + Math.sin(sidewayAngleInRadians)*Math.cos(backwayAngleInRadians)*Math.sin(apparentWindAngleInRadians);
    return Math.asin(scalarProduct)*180/Math.PI;
  }
}
