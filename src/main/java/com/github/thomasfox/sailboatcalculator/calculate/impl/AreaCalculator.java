package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.ValueSet;

public class AreaCalculator extends Calculator
{
  public AreaCalculator()
  {
    super(PhysicalQuantity.WING_AREA,
        PhysicalQuantity.WING_CHORD,
        PhysicalQuantity.WING_SPAN);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double wingChord = valueSet.getKnownValue(PhysicalQuantity.WING_CHORD).getValue();
    double wingSpan = valueSet.getKnownValue(PhysicalQuantity.WING_SPAN).getValue();

    return wingChord*wingSpan;
  }
}
