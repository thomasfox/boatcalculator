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
    double heelAngleInRad = valueSet.getKnownQuantityValue(PhysicalQuantity.WINDWARD_HEEL_ANGLE).getValue()*Math.PI/180;
    double sidewayAngleInRad =
        - valueSet.getKnownQuantityValue(PhysicalQuantity.SIDEWAY_ANGLE).getValue()*Math.PI/180 - heelAngleInRad;
    double backwayAngleInRad = valueSet.getKnownQuantityValue(PhysicalQuantity.BACKWAY_ANGLE).getValue()*Math.PI/180;
    double apparentWindAngleInRad = valueSet.getKnownQuantityValue(PhysicalQuantity.APPARENT_WIND_ANGLE).getValue()*Math.PI/180;
    double halfwingSpan = valueSet.getKnownQuantityValue(PhysicalQuantity.HALFWING_SPAN).getValue();
    double height = valueSet.getKnownQuantityValue(PhysicalQuantity.CENTER_OF_EFFORT_HEIGHT).getValue();

    double areaNormalVectorX = Math.sin(backwayAngleInRad);
    double areaNormalVectorY = Math.sin(sidewayAngleInRad)*Math.cos(backwayAngleInRad);
    double areaNormalVectorZ = Math.cos(sidewayAngleInRad)*Math.cos(backwayAngleInRad);

    double windVectorX = Math.cos(apparentWindAngleInRad);
    double windVectorY = Math.sin(apparentWindAngleInRad);
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

    double ry = Math.cos(heelAngleInRad) * halfwingSpan/2 - Math.sin(heelAngleInRad) * height;
    double rz = Math.cos(heelAngleInRad) * height + Math.sin(heelAngleInRad) * halfwingSpan/2;

    double torque = ry * FZ - rz *FY;
    return torque;
  }
}
