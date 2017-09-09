package com.github.thomasfox.sailboatcalculator.calculate.impl;

import java.util.Map;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;

public class VMGCalculator extends Calculator
{
  public VMGCalculator()
  {
    super(PhysicalQuantity.VMG,
        PhysicalQuantity.POINTING_ANGLE,
        PhysicalQuantity.VELOCITY);
  }

  @Override
  protected double calculateWithoutChecks(Map<PhysicalQuantity, Double> input)
  {
    double pointingAngle = PhysicalQuantity.POINTING_ANGLE.getValueFromAvailableQuantities(input);
    double velocity = PhysicalQuantity.VELOCITY.getValueFromAvailableQuantities(input);

    double vmg = velocity* Math.cos(pointingAngle * Math.PI / 180d);
    return vmg;
  }
}
