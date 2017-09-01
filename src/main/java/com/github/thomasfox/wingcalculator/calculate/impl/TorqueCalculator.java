package com.github.thomasfox.wingcalculator.calculate.impl;

import java.util.Map;

import com.github.thomasfox.wingcalculator.calculate.Calculator;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;

public class TorqueCalculator extends Calculator
{
  public TorqueCalculator()
  {
    super(PhysicalQuantity.TORQUE_BETWEEN_FORCES,
        PhysicalQuantity.LEVER_BETWEEN_FORCES,
        PhysicalQuantity.FORCE);
  }

  @Override
  protected double calculateWithoutChecks(Map<PhysicalQuantity, Double> input)
  {
    double lever = PhysicalQuantity.LEVER_BETWEEN_FORCES.getValueFromAvailableQuantities(input);
    double force = PhysicalQuantity.FORCE.getValueFromAvailableQuantities(input);

    double torque = lever * force;
    return torque;
  }
}
