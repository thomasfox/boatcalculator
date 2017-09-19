package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValues;

public class TorqueCalculator extends Calculator
{
  public TorqueCalculator()
  {
    super(PhysicalQuantity.TORQUE_BETWEEN_FORCES,
        PhysicalQuantity.LEVER_BETWEEN_FORCES,
        PhysicalQuantity.FORCE);
  }

  @Override
  protected double calculateWithoutChecks(PhysicalQuantityValues input)
  {
    double lever = input.getValue(PhysicalQuantity.LEVER_BETWEEN_FORCES);
    double force = input.getValue(PhysicalQuantity.FORCE);

    double torque = lever * force;
    return torque;
  }
}
