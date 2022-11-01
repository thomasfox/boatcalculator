package com.github.thomasfox.boatcalculator.valueset.impl;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.SimpleValueSet;

public class Water extends SimpleValueSet
{
  public static final String ID = Water.class.getSimpleName();

  private static final String NAME = "Wasser";

  public Water()
  {
    super(ID, NAME);
    addToInput(PhysicalQuantity.TEMPERATURE);
  }
}
