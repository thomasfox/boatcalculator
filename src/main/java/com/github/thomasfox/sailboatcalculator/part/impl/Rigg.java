package com.github.thomasfox.sailboatcalculator.part.impl;

import com.github.thomasfox.sailboatcalculator.calculate.MaterialConstants;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.part.PartType;

public class Rigg extends Wing
{
  public Rigg()
  {
    super(PartType.RIGG);
    setFixedValueNoOverwrite(MaterialConstants.DENSITY_AIR);
    setFixedValueNoOverwrite(MaterialConstants.KINEMATIC_VISCOSITY_AIR);
    addToInput(PhysicalQuantity.LIFT_COEFFICIENT);
    addToInput(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    addToInput(PhysicalQuantity.WING_AREA);
    addToInput(PhysicalQuantity.RIGG_CENTER_OF_EFFORT_HEIGHT);
  }
}
