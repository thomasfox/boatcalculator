package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValues;

public class AreaCalculator extends Calculator
{
  public AreaCalculator()
  {
    super(PhysicalQuantity.WING_AREA,
        PhysicalQuantity.WING_CHORD,
        PhysicalQuantity.WING_SPAN);
  }

  @Override
  protected double calculateWithoutChecks(PhysicalQuantityValues input)
  {
    double wingChord = input.getValue(PhysicalQuantity.WING_CHORD);
    double wingSpan = input.getValue(PhysicalQuantity.WING_SPAN);

    return wingChord*wingSpan;
  }
}
