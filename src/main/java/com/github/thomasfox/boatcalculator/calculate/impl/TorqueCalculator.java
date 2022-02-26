package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

public class TorqueCalculator extends Calculator
{
  public TorqueCalculator()
  {
    super(PhysicalQuantity.TORQUE_BETWEEN_FORCES,
        PhysicalQuantity.LEVER_BETWEEN_FORCES,
        PhysicalQuantity.FORCE);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double lever = valueSet.getKnownQuantityValue(PhysicalQuantity.LEVER_BETWEEN_FORCES).getValue();
    double force = valueSet.getKnownQuantityValue(PhysicalQuantity.FORCE).getValue();

    double torque = lever * force;
    return torque;
  }
}
