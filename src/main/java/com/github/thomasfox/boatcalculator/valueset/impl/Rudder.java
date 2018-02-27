package com.github.thomasfox.boatcalculator.valueset.impl;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;

public class Rudder extends Hydrofoil
{
  public static final String ID = Rudder.class.getSimpleName();

  private static final String NAME = "Ruder";

  public Rudder()
  {
    super(ID, NAME);
    addToInput(PhysicalQuantity.LIFT);
    setFixedValueNoOverwrite(PhysicalQuantity.WAVE_MAKING_DRAG_COEFFICIENT, 0d);
    addHiddenOutput(PhysicalQuantity.WAVE_MAKING_DRAG);
    addHiddenOutput(PhysicalQuantity.WAVE_MAKING_DRAG_COEFFICIENT);
    addHiddenOutput(PhysicalQuantity.VELOCITY);
  }
}
