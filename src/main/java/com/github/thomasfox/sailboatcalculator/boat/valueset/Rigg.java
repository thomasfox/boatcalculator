package com.github.thomasfox.sailboatcalculator.boat.valueset;

import com.github.thomasfox.sailboatcalculator.calculate.MaterialConstants;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;

public class Rigg extends Wing
{
  public static final String ID = Rigg.class.getSimpleName();

  private static final String NAME = "Rigg";

  public Rigg()
  {
    super(ID, NAME);
    setFixedValueNoOverwrite(MaterialConstants.DENSITY_AIR);
    setFixedValueNoOverwrite(MaterialConstants.KINEMATIC_VISCOSITY_AIR);
    addToInput(PhysicalQuantity.LIFT_COEFFICIENT);
    addToInput(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    addToInput(PhysicalQuantity.WING_AREA);
    addToInput(PhysicalQuantity.RIGG_CENTER_OF_EFFORT_HEIGHT);
  }
}
