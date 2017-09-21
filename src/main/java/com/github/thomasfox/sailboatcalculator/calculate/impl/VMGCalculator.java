package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValues;

public class VMGCalculator extends Calculator
{
  public VMGCalculator()
  {
    super(PhysicalQuantity.VMG,
        PhysicalQuantity.SAILING_ANGLE,
        PhysicalQuantity.VELOCITY);
  }

  @Override
  protected double calculateWithoutChecks(PhysicalQuantityValues input)
  {
    double pointingAngle = input.getValue(PhysicalQuantity.SAILING_ANGLE);
    double velocity = input.getValue(PhysicalQuantity.VELOCITY);

    double vmg = velocity* Math.cos(pointingAngle * Math.PI / 180d);
    return vmg;
  }
}
