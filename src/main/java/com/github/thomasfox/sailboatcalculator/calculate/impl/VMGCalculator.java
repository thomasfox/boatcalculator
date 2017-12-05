package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.ValueSet;

public class VMGCalculator extends Calculator
{
  public VMGCalculator()
  {
    super(PhysicalQuantity.VMG,
        PhysicalQuantity.SAILING_ANGLE,
        PhysicalQuantity.VELOCITY);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double pointingAngle = valueSet.getKnownValue(PhysicalQuantity.SAILING_ANGLE).getValue();
    double velocity = valueSet.getKnownValue(PhysicalQuantity.VELOCITY).getValue();

    double vmg = velocity* Math.cos(pointingAngle * Math.PI / 180d);
    return vmg;
  }
}
