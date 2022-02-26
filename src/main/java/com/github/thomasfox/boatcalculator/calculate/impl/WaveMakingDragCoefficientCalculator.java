package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

/**
 * https://de.wikipedia.org/wiki/Froude-Zahl
 */
public class WaveMakingDragCoefficientCalculator extends Calculator
{
  public WaveMakingDragCoefficientCalculator()
  {
    super(PhysicalQuantity.WAVE_MAKING_DRAG_COEFFICIENT,
        PhysicalQuantity.FROUDE_NUMBER_SUMBERGENCE,
        PhysicalQuantity.SUBMERGENCE_DEPTH,
        PhysicalQuantity.LIFT_COEFFICIENT,
        PhysicalQuantity.WING_CHORD);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double froudeNumber = valueSet.getKnownQuantityValue(PhysicalQuantity.FROUDE_NUMBER_SUMBERGENCE).getValue();
    if (froudeNumber == 0)
    {
      // velocity is null or submergenceDepth is infinity
      return 0;
    }
    double submergenceDepth = valueSet.getKnownQuantityValue(PhysicalQuantity.SUBMERGENCE_DEPTH).getValue();
    double liftCoefficient = valueSet.getKnownQuantityValue(PhysicalQuantity.LIFT_COEFFICIENT).getValue();
    double wingChord = valueSet.getKnownQuantityValue(PhysicalQuantity.WING_CHORD).getValue();

    double waveMakingDragCoefficient = 0.5*liftCoefficient*liftCoefficient
        *wingChord/submergenceDepth
        /froudeNumber/froudeNumber/Math.exp(2/froudeNumber/froudeNumber);
    return waveMakingDragCoefficient;
  }
}
