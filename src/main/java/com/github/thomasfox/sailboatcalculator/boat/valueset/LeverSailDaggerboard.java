package com.github.thomasfox.sailboatcalculator.boat.valueset;

import com.github.thomasfox.sailboatcalculator.calculate.value.SimpleValueSet;

public class LeverSailDaggerboard extends SimpleValueSet
{
  public static final String ID = LeverSailDaggerboard.class.getSimpleName();

  private static final String NAME = "Hebel Schwert/Segel";

  public LeverSailDaggerboard()
  {
    super(ID, NAME);
  }
}
