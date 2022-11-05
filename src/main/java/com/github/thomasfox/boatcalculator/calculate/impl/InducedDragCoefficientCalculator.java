package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

/**
 * https://de.wikipedia.org/wiki/Induzierter_Luftwiderstand
 * Laut Hoerner Oswaldfaktor in der Größenordnung von 1 % -> wird vernachlaessigt
 */
public class InducedDragCoefficientCalculator extends Calculator
{
  public InducedDragCoefficientCalculator()
  {
    super(PhysicalQuantity.INDUCED_DRAG_COEFFICIENT,
        PhysicalQuantity.WING_SPAN_IN_MEDIUM,
        PhysicalQuantity.AREA_IN_MEDIUM,
        PhysicalQuantity.LIFT_COEFFICIENT);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double wingSpan = valueSet.getKnownQuantityValue(PhysicalQuantity.WING_SPAN_IN_MEDIUM).getValue();
    double wingArea = valueSet.getKnownQuantityValue(PhysicalQuantity.AREA_IN_MEDIUM).getValue();
    double liftCoefficient = valueSet.getKnownQuantityValue(PhysicalQuantity.LIFT_COEFFICIENT).getValue();
    if (liftCoefficient > 3)
    {
      // for low velocities, the calculated lift coefficient can be huge. This of course is not realistic.
      // So we impose a maximum lift coefficient of 3, which still unrealistic in practice,
      // but small enough to get the order of magnitude of induced drag and large enough to not
      // change the calculated drag in any desirable operation mode
      liftCoefficient = 3;
    }

    double aspectRatio = wingSpan * wingSpan / wingArea;
    double result = liftCoefficient * liftCoefficient / Math.PI / aspectRatio;
    return result;
  }
}
