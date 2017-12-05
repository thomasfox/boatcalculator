package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.ValueSet;

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
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double wingVelocity = valueSet.getKnownValue(PhysicalQuantity.VELOCITY).getValue();
    double wingDepth = valueSet.getKnownValue(PhysicalQuantity.WING_CHORD).getValue();
    double kinematicVelocity = valueSet.getKnownValue(PhysicalQuantity.KINEMATIC_VISCOSITY).getValue();

    return wingVelocity * wingDepth / kinematicVelocity;
  }
}
