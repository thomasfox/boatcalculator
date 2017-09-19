package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValues;

public class ThicknessCalculator extends Calculator
{
  public ThicknessCalculator()
  {
    super(PhysicalQuantity.WING_THICKNESS,
        PhysicalQuantity.WING_RELATIVE_THICKNESS,
        PhysicalQuantity.WING_CHORD);
  }

  @Override
  protected double calculateWithoutChecks(PhysicalQuantityValues input)
  {
    double wingDepth = input.getValue(PhysicalQuantity.WING_CHORD);
    double relativeThickness = input.getValue(PhysicalQuantity.WING_RELATIVE_THICKNESS);

    return wingDepth*relativeThickness;
  }
}
