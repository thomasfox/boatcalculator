package com.github.thomasfox.sailboatcalculator.part.impl;

import com.github.thomasfox.sailboatcalculator.calculate.MaterialConstants;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.part.PartType;

public abstract class Foil extends Wing
{
  public Foil(PartType type)
  {
    super(type);
    toInput.add(PhysicalQuantity.SECOND_MOMENT_OF_AREA);
    setFixedValueNoOverwrite(MaterialConstants.DENSITY_WATER);
    setFixedValueNoOverwrite(MaterialConstants.KINEMATIC_VISCOSITY_WATER);
  }
}
