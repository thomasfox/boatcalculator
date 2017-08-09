package com.github.thomasfox.wingcalculator.calculate.impl;

import java.util.Map;

import com.github.thomasfox.wingcalculator.calculate.Calculator;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;

/**
 * Siehe http://www.wasser-wissen.de/abwasserlexikon/r/reynoldszahl.htm
 */
public class ReynoldsNumberCalculator extends Calculator
{
  public ReynoldsNumberCalculator()
  {
    super(PhysicalQuantity.REYNOLDS_NUMBER,
        PhysicalQuantity.WING_VELOCITY,
        PhysicalQuantity.WING_DEPTH,
        PhysicalQuantity.KINEMATIC_VISCOSITY);
  }

  @Override
  protected double calculateWithoutChecks(Map<PhysicalQuantity, Double> input)
  {
    double wingVelocity = getValueOf(PhysicalQuantity.WING_VELOCITY, input);
    double wingDepth = getValueOf(PhysicalQuantity.WING_DEPTH, input);
    double kinematicVelocity = getValueOf(PhysicalQuantity.KINEMATIC_VISCOSITY, input);
    return wingVelocity * wingDepth / kinematicVelocity;
  }
}
