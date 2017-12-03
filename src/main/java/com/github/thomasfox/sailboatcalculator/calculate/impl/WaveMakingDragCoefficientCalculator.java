package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValues;

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
  protected double calculateWithoutChecks(PhysicalQuantityValues input)
  {
    double froudeNumber = input.getValue(PhysicalQuantity.FROUDE_NUMBER_SUMBERGENCE);
    double submergenceDepth = input.getValue(PhysicalQuantity.SUBMERGENCE_DEPTH);
    double liftCoefficient = input.getValue(PhysicalQuantity.LIFT_COEFFICIENT);
    double wingChord = input.getValue(PhysicalQuantity.WING_CHORD);

    double waveMakingDragCoefficient = 0.5*liftCoefficient*liftCoefficient
        *wingChord/submergenceDepth
        /froudeNumber/froudeNumber/Math.exp(2/froudeNumber/froudeNumber);
    return waveMakingDragCoefficient;
  }
}
