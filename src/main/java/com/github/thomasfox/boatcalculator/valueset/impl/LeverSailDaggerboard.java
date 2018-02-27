package com.github.thomasfox.boatcalculator.valueset.impl;

import com.github.thomasfox.boatcalculator.valueset.SimpleValueSet;

public class LeverSailDaggerboard extends SimpleValueSet
{
  public static final String ID = LeverSailDaggerboard.class.getSimpleName();

  private static final String NAME = "Hebel Schwert/Segel";

  public LeverSailDaggerboard()
  {
    super(ID, NAME);
  }
}
