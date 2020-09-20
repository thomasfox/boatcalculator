package com.github.thomasfox.boatcalculator.valueset.impl;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;

public class RudderLiftingFoil extends Hydrofoil
{
  public static final String ID = RudderLiftingFoil.class.getSimpleName();

  private static final String NAME = "Rudertragfläche";

  public RudderLiftingFoil()
  {
    super(ID, NAME);
    addToInput(PhysicalQuantity.MAX_ANGLE_OF_ATTACK);
    setStartValue(PhysicalQuantity.MAX_ANGLE_OF_ATTACK, 10d);
    addToInput(PhysicalQuantity.ANGLE_OF_ATTACK);
  }
}
