package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

public class DrivingForceForBentWingCalculator extends Calculator
{
  public DrivingForceForBentWingCalculator()
  {
    super(PhysicalQuantity.DRIVING_FORCE,
        PhysicalQuantity.SIDEWAY_ANGLE,
        PhysicalQuantity.BACKWAY_ANGLE,
        PhysicalQuantity.APPARENT_WIND_ANGLE,
        PhysicalQuantity.TOTAL_DRAG,
        PhysicalQuantity.LIFT);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double drag = valueSet.getKnownQuantityValue(PhysicalQuantity.TOTAL_DRAG).getValue();
    double lift = valueSet.getKnownQuantityValue(PhysicalQuantity.LIFT).getValue();
    double sidewayAngleInRad = valueSet.getKnownQuantityValue(PhysicalQuantity.SIDEWAY_ANGLE).getValue()*Math.PI/180;
    double backwayAngleInRad = valueSet.getKnownQuantityValue(PhysicalQuantity.BACKWAY_ANGLE).getValue()*Math.PI/180;
    double apparentWindAngleInRad = valueSet.getKnownQuantityValue(PhysicalQuantity.APPARENT_WIND_ANGLE).getValue()*Math.PI/180;

    double areaNormalVectorX = Math.sin(backwayAngleInRad);
    double areaNormalVectorY = Math.sin(sidewayAngleInRad)*Math.cos(backwayAngleInRad);
    double areaNormalVectorZ = Math.cos(sidewayAngleInRad)*Math.cos(backwayAngleInRad);

    double windVectorX = Math.cos(apparentWindAngleInRad);
    double windVectorY = -Math.sin(apparentWindAngleInRad);
    // windVectorZ is null;

    double scalarProductWindAreaNormal = areaNormalVectorX * windVectorX + areaNormalVectorY * windVectorY;

    double fLiftX = areaNormalVectorX - scalarProductWindAreaNormal*windVectorX;
    double fLiftY = areaNormalVectorY - scalarProductWindAreaNormal*windVectorY;
    double fLiftZ = areaNormalVectorZ;

    double fLength = Math.sqrt(fLiftX*fLiftX+fLiftY*fLiftY+fLiftZ*fLiftZ);
    fLiftX = fLiftX/fLength;
    fLiftY = fLiftY/fLength;
    fLiftZ = fLiftZ/fLength;

    double dragX = drag*windVectorX;
    double dragY = drag*windVectorY;

    double liftX = lift*fLiftX;
    double liftY = lift*fLiftY;
    double liftZ = lift*fLiftZ;


    double FX = lift * fLiftX + drag*windVectorX;
    double FY = lift * fLiftY + drag*windVectorY;
    double FZ = lift * fLiftZ;

    double drivingForce = -FX;
    return drivingForce;
  }
}
