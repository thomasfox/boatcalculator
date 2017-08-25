package com.github.thomasfox.wingcalculator.calculate.impl;

import java.util.Map;

import com.github.thomasfox.wingcalculator.calculate.Calculator;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;

public class LateralForceCalculator extends Calculator
{
  public LateralForceCalculator()
  {
    super(PhysicalQuantity.LATERAL_FORCE,
        PhysicalQuantity.FLOW_DIRECTION,
        PhysicalQuantity.TOTAL_DRAG,
        PhysicalQuantity.LIFT);
  }

  @Override
  protected double calculateWithoutChecks(Map<PhysicalQuantity, Double> input)
  {
    double drag = PhysicalQuantity.TOTAL_DRAG.getValueFromAvailableQuantities(input);
    double lift = PhysicalQuantity.LIFT.getValueFromAvailableQuantities(input);
    double flowDirection = PhysicalQuantity.FLOW_DIRECTION.getValueFromAvailableQuantities(input);

    double lateralForce = lift * Math.cos(flowDirection * Math.PI / 180d) + drag * Math.sin(flowDirection * Math.PI / 180d);
    return lateralForce;
  }
}
