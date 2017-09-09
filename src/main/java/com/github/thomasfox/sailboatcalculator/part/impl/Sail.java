package com.github.thomasfox.sailboatcalculator.part.impl;

import com.github.thomasfox.sailboatcalculator.calculate.MaterialConstants;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.part.PartType;

public class Sail extends Wing
{
  public Sail()
  {
    super(PartType.SAIL);
    setFixedValueNoOverwrite(MaterialConstants.DENSITY_AIR);
    setFixedValueNoOverwrite(MaterialConstants.KINEMATIC_VISCOSITY_AIR);
    toInput.add(PhysicalQuantity.LIFT_COEFFICIENT);
    toInput.add(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
  }
}
