package com.github.thomasfox.sailboatcalculator.calculate.impl;

import com.github.thomasfox.sailboatcalculator.calculate.Calculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.valueset.ValueSet;

public class ThicknessCalculator extends Calculator
{
  public ThicknessCalculator()
  {
    super(PhysicalQuantity.WING_THICKNESS,
        PhysicalQuantity.WING_RELATIVE_THICKNESS,
        PhysicalQuantity.WING_CHORD);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double wingDepth = valueSet.getKnownValue(PhysicalQuantity.WING_CHORD).getValue();
    double relativeThickness = valueSet.getKnownValue(PhysicalQuantity.WING_RELATIVE_THICKNESS).getValue();

    return wingDepth*relativeThickness;
  }
}
