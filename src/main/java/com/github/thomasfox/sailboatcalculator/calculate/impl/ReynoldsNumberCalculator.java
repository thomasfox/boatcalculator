package com.github.thomasfox.sailboatcalculator.calculate.impl;

import java.util.Map;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;

/**
 * Siehe http://www.wasser-wissen.de/abwasserlexikon/r/reynoldszahl.htm
 */
public class ReynoldsNumberCalculator extends Calculator
{
  public ReynoldsNumberCalculator()
  {
    super(PhysicalQuantity.REYNOLDS_NUMBER,
        PhysicalQuantity.VELOCITY,
        PhysicalQuantity.WING_CHORD,
        PhysicalQuantity.KINEMATIC_VISCOSITY);
  }

  @Override
  protected double calculateWithoutChecks(Map<PhysicalQuantity, Double> input)
  {
    double wingVelocity = PhysicalQuantity.VELOCITY.getValueFromAvailableQuantities(input);
    double wingDepth = PhysicalQuantity.WING_CHORD.getValueFromAvailableQuantities(input);
    double kinematicVelocity = PhysicalQuantity.KINEMATIC_VISCOSITY.getValueFromAvailableQuantities(input);
    return wingVelocity * wingDepth / kinematicVelocity;
  }
}
