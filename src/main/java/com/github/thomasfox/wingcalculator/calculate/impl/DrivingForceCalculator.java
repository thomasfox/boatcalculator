package com.github.thomasfox.wingcalculator.calculate.impl;

import java.util.Map;

import com.github.thomasfox.wingcalculator.calculate.Calculator;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;

public class DrivingForceCalculator extends Calculator
{
  public DrivingForceCalculator()
  {
    super(PhysicalQuantity.DRIVING_FORCE,
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

    double drivingForce = lift * Math.sin(flowDirection * Math.PI / 180d) - drag * Math.cos(flowDirection * Math.PI / 180d);
    return drivingForce;
  }
}
