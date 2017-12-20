package com.github.thomasfox.sailboatcalculator.valueset.impl;

import com.github.thomasfox.sailboatcalculator.valueset.SimpleValueSet;

public class Hull extends SimpleValueSet
{
  public static final String ID = Hull.class.getSimpleName();

  private static final String NAME = "Rumpf";

  public Hull()
  {
    super(ID, NAME);
  }
}
