package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.valueset.ValueSet;

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
    double lever = valueSet.getKnownValue(PhysicalQuantity.LEVER_BETWEEN_FORCES).getValue();
    double force = valueSet.getKnownValue(PhysicalQuantity.FORCE).getValue();

    double torque = lever * force;
    return torque;
  }
}
