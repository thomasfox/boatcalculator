package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

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
    double wingVelocity = valueSet.getKnownQuantityValue(PhysicalQuantity.VELOCITY).getValue();
    double wingDepth = valueSet.getKnownQuantityValue(PhysicalQuantity.WING_CHORD).getValue();
    double kinematicVelocity = valueSet.getKnownQuantityValue(PhysicalQuantity.KINEMATIC_VISCOSITY).getValue();

    return wingVelocity * wingDepth / kinematicVelocity;
  }
}
