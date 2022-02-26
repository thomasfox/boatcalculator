package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

public class LeverFromWeightCalculator extends Calculator
{
  public LeverFromWeightCalculator()
  {
    super(PhysicalQuantity.LEVER_WEIGHT,
        PhysicalQuantity.WEIGHT,
        PhysicalQuantity.TORQUE_BETWEEN_FORCES);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double weight = valueSet.getKnownQuantityValue(PhysicalQuantity.WEIGHT).getValue();
    double torque = valueSet.getKnownQuantityValue(PhysicalQuantity.TORQUE_BETWEEN_FORCES).getValue();

    double lever = torque / weight;
    return lever;
  }
}
