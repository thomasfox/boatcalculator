package com.github.thomasfox.sailboatcalculator.valueset.impl;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.valueset.SimpleValueSet;

public class BoatGlobalValues extends SimpleValueSet
{
  public static final String ID = BoatGlobalValues.class.getSimpleName();

  private static final String NAME = "Boot";

  public BoatGlobalValues()
  {
    super(ID, NAME);
    addToInput(PhysicalQuantity.WIND_SPEED);
    addToInput(PhysicalQuantity.POINTING_ANGLE);
    addToInput(PhysicalQuantity.DRIFT_ANGLE);
    addToInput(PhysicalQuantity.MASS);
    addToInput(PhysicalQuantity.VELOCITY);
  }
}
