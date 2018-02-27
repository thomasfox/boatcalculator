package com.github.thomasfox.boatcalculator.valueset.impl;

import com.github.thomasfox.boatcalculator.calculate.MaterialConstants;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;

public class Rigg extends Wing
{
  public static final String ID = Rigg.class.getSimpleName();

  private static final String NAME = "Rigg";

  public Rigg()
  {
    super(ID, NAME);
    setFixedValueNoOverwrite(MaterialConstants.DENSITY_AIR);
    setFixedValueNoOverwrite(MaterialConstants.KINEMATIC_VISCOSITY_AIR);
    setFixedValueNoOverwrite(PhysicalQuantity.WAVE_MAKING_DRAG_COEFFICIENT, 0);
    addToInput(PhysicalQuantity.LIFT_COEFFICIENT);
    addToInput(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    addToInput(PhysicalQuantity.AREA);
    addToInput(PhysicalQuantity.RIGG_CENTER_OF_EFFORT_HEIGHT);
    addHiddenOutput(PhysicalQuantity.WAVE_MAKING_DRAG);
  }
}
