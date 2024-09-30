package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

/**
 * Siehe http://www.wasser-wissen.de/abwasserlexikon/r/reynoldszahl.htm
 */
public class ChordFromReynoldsNumberCalculator extends Calculator
{
  public ChordFromReynoldsNumberCalculator()
  {
    super(PhysicalQuantity.WING_CHORD,
        PhysicalQuantity.VELOCITY,
        PhysicalQuantity.REYNOLDS_NUMBER,
        PhysicalQuantity.KINEMATIC_VISCOSITY);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double wingVelocity = valueSet.getKnownQuantityValue(PhysicalQuantity.VELOCITY).getValue();
    double reynoldsNumber = valueSet.getKnownQuantityValue(PhysicalQuantity.REYNOLDS_NUMBER).getValue();
    double kinematicVelocity = valueSet.getKnownQuantityValue(PhysicalQuantity.KINEMATIC_VISCOSITY).getValue();

    return reynoldsNumber * kinematicVelocity / wingVelocity;
  }
}
