package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

public class TorqueForBentWingCalculator extends Calculator
{
  public TorqueForBentWingCalculator()
  {
    super(PhysicalQuantity.TORQUE_BETWEEN_FORCES,
        PhysicalQuantity.WINDWARD_HEEL_ANGLE,
        PhysicalQuantity.SIDEWAY_ANGLE,
        PhysicalQuantity.BACKWAY_ANGLE,
        PhysicalQuantity.APPARENT_WIND_ANGLE,
        PhysicalQuantity.TOTAL_DRAG,
        PhysicalQuantity.HALFWING_SPAN,
        PhysicalQuantity.LEVER_BETWEEN_FORCES,
        PhysicalQuantity.LIFT);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double drag = valueSet.getKnownQuantityValue(PhysicalQuantity.TOTAL_DRAG).getValue();
    double lift = valueSet.getKnownQuantityValue(PhysicalQuantity.LIFT).getValue();
    double sidewayAngleInRad =
        (valueSet.getKnownQuantityValue(PhysicalQuantity.SIDEWAY_ANGLE).getValue()
          + valueSet.getKnownQuantityValue(PhysicalQuantity.WINDWARD_HEEL_ANGLE).getValue())*Math.PI/180;
    double backwayAngleInRad = valueSet.getKnownQuantityValue(PhysicalQuantity.BACKWAY_ANGLE).getValue()*Math.PI/180;
    double apparentWindAngleInRad = valueSet.getKnownQuantityValue(PhysicalQuantity.APPARENT_WIND_ANGLE).getValue()*Math.PI/180;
    double halfwingSpan = valueSet.getKnownQuantityValue(PhysicalQuantity.HALFWING_SPAN).getValue();
    double height = valueSet.getKnownQuantityValue(PhysicalQuantity.LEVER_BETWEEN_FORCES).getValue();

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

    double FY = lift * fLiftY + drag*windVectorY;
    double FZ = lift * fLiftZ;

    double forceAmountYZ = Math.sqrt(FY*FY+FZ*FZ);

    double scalarProductForceR = -halfwingSpan/2 * FY + height * FZ;

    double torqueY = -halfwingSpan/2*forceAmountYZ-scalarProductForceR*FY/forceAmountYZ;
    double torqueZ = height*forceAmountYZ-scalarProductForceR*FZ/forceAmountYZ;

    double torque = Math.sqrt(torqueY*torqueY+torqueZ*torqueZ);
    return torque;
  }
}
