package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

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
    double wingDepth = valueSet.getKnownQuantityValue(PhysicalQuantity.WING_CHORD).getValue();
    double relativeThickness = valueSet.getKnownQuantityValue(PhysicalQuantity.WING_RELATIVE_THICKNESS).getValue();

    return wingDepth*relativeThickness;
  }
}
