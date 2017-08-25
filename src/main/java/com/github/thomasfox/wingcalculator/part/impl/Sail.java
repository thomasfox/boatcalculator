package com.github.thomasfox.wingcalculator.part.impl;

import com.github.thomasfox.wingcalculator.calculate.MaterialConstants;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.wingcalculator.part.PartType;

public class Sail extends Wing
{
  public Sail()
  {
    super(PartType.SAIL);
    setFixedValueNoOverwrite(MaterialConstants.DENSITY_AIR);
    setFixedValueNoOverwrite(MaterialConstants.KINEMATIC_VISCOSITY_AIR);
    toInput.add(PhysicalQuantity.LIFT_COEFFICIENT);
    toInput.add(PhysicalQuantity.TOTAL_DRAG_COEFFICIENT);
  }
}
