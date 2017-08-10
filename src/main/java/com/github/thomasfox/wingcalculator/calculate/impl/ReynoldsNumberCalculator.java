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
    double wingVelocity = PhysicalQuantity.WING_VELOCITY.getValueFromAvailableQuantities(input);
    double wingDepth = PhysicalQuantity.WING_DEPTH.getValueFromAvailableQuantities(input);
    double kinematicVelocity = PhysicalQuantity.KINEMATIC_VISCOSITY.getValueFromAvailableQuantities(input);
    return wingVelocity * wingDepth / kinematicVelocity;
  }
}
