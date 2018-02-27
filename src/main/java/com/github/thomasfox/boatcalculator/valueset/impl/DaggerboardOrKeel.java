package com.github.thomasfox.boatcalculator.valueset.impl;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;

public class DaggerboardOrKeel extends Hydrofoil
{
  public static final String ID = DaggerboardOrKeel.class.getSimpleName();

  private static final String NAME = "Schwert/Kiel";

  public DaggerboardOrKeel()
  {
    super(ID, NAME);
    setFixedValueNoOverwrite(PhysicalQuantity.WAVE_MAKING_DRAG_COEFFICIENT, 0d);
    addHiddenOutput(PhysicalQuantity.WAVE_MAKING_DRAG);
    addHiddenOutput(PhysicalQuantity.WAVE_MAKING_DRAG_COEFFICIENT);
    addHiddenOutput(PhysicalQuantity.VELOCITY);
  }
}
