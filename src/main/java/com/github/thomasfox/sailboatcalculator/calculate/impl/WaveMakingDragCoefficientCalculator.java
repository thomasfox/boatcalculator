package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.valueset.ValueSet;

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
    double froudeNumber = valueSet.getKnownValue(PhysicalQuantity.FROUDE_NUMBER_SUMBERGENCE).getValue();
    double submergenceDepth = valueSet.getKnownValue(PhysicalQuantity.SUBMERGENCE_DEPTH).getValue();
    double liftCoefficient = valueSet.getKnownValue(PhysicalQuantity.LIFT_COEFFICIENT).getValue();
    double wingChord = valueSet.getKnownValue(PhysicalQuantity.WING_CHORD).getValue();

    double waveMakingDragCoefficient = 0.5*liftCoefficient*liftCoefficient
        *wingChord/submergenceDepth
        /froudeNumber/froudeNumber/Math.exp(2/froudeNumber/froudeNumber);
    return waveMakingDragCoefficient;
  }
}
