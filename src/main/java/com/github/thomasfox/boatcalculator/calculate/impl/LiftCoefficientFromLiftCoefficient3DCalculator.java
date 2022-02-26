package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

/**
 * https://de.wikipedia.org/wiki/Dynamischer_Auftrieb
 */
public class LiftCoefficientFromLiftCoefficient3DCalculator extends Calculator
{
  public LiftCoefficientFromLiftCoefficient3DCalculator()
  {
    super(PhysicalQuantity.LIFT_COEFFICIENT,
        PhysicalQuantity.LIFT_COEFFICIENT_3D,
        PhysicalQuantity.WING_SPAN,
        PhysicalQuantity.AREA);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double liftCoefficient3d = valueSet.getKnownQuantityValue(PhysicalQuantity.LIFT_COEFFICIENT_3D).getValue();
    double wingSpan = valueSet.getKnownQuantityValue(PhysicalQuantity.WING_SPAN).getValue();
    double wingArea = valueSet.getKnownQuantityValue(PhysicalQuantity.AREA).getValue();

    double aspectRatio = wingSpan * wingSpan / wingArea;
    double result = liftCoefficient3d * (1 + 2 / aspectRatio);
    return result;
  }
}
