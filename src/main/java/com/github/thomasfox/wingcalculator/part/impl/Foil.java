package com.github.thomasfox.wingcalculator.part.impl;

import com.github.thomasfox.wingcalculator.calculate.MaterialConstants;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.wingcalculator.part.PartType;

public abstract class Foil extends Wing
{
  public Foil(PartType type)
  {
    super(type);
    toInput.add(PhysicalQuantity.SECOND_MOMENT_OF_AREA);
    setFixedValueNoOverwide(MaterialConstants.DENSITY_WATER);
    setFixedValueNoOverwide(MaterialConstants.KINEMATIC_VISCOSITY_WATER);
  }
}
