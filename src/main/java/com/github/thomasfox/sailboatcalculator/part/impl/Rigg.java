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
    toInput.add(PhysicalQuantity.LIFT_COEFFICIENT);
    toInput.add(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    toInput.add(PhysicalQuantity.WING_AREA);
    toInput.add(PhysicalQuantity.RIGG_CENTER_OF_EFFORT_HEIGHT);
  }
}
