package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

/**
 * https://de.wikipedia.org/wiki/Dynamischer_Auftrieb
 */
public class LiftCoefficient3DFromLiftCoefficientCalculator extends Calculator
{
  public LiftCoefficient3DFromLiftCoefficientCalculator()
  {
    super(PhysicalQuantity.LIFT_COEFFICIENT_3D,
        PhysicalQuantity.LIFT_COEFFICIENT,
        PhysicalQuantity.WING_SPAN_IN_MEDIUM,
        PhysicalQuantity.AREA_IN_MEDIUM);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double liftCoefficient = valueSet.getKnownQuantityValue(PhysicalQuantity.LIFT_COEFFICIENT).getValue();
    double wingSpan = valueSet.getKnownQuantityValue(PhysicalQuantity.WING_SPAN_IN_MEDIUM).getValue();
    double wingArea = valueSet.getKnownQuantityValue(PhysicalQuantity.AREA_IN_MEDIUM).getValue();

    double aspectRatio = wingSpan * wingSpan / wingArea;
    double result = liftCoefficient / (1 + 2 / aspectRatio);
    return result;
  }
}
