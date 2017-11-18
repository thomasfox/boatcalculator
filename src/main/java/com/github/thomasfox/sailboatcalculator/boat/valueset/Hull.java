package com.github.thomasfox.sailboatcalculator.boat.valueset;

import com.github.thomasfox.sailboatcalculator.calculate.value.SimpleValueSet;

public class Hull extends SimpleValueSet
{
  public static final String ID = Hull.class.getSimpleName();

  private static final String NAME = "Rumpf";

  public Hull()
  {
    super(ID, NAME);
  }
}
