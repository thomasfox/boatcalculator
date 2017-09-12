package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantityValues;

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
  protected double calculateWithoutChecks(PhysicalQuantityValues input)
  {
    double wingVelocity = input.getValue(PhysicalQuantity.VELOCITY);
    double wingDepth = input.getValue(PhysicalQuantity.WING_CHORD);
    double kinematicVelocity = input.getValue(PhysicalQuantity.KINEMATIC_VISCOSITY);

    return wingVelocity * wingDepth / kinematicVelocity;
  }
}
