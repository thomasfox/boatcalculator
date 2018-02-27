package com.github.thomasfox.boatcalculator.valueset.impl;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.SimpleValueSet;

public class Hull extends SimpleValueSet
{
  public static final String ID = Hull.class.getSimpleName();

  private static final String NAME = "Rumpf";

  public Hull()
  {
    super(ID, NAME);
    addHiddenOutput(PhysicalQuantity.VELOCITY);
  }
}
