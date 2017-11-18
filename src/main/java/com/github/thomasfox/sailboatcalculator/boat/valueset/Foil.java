package com.github.thomasfox.sailboatcalculator.boat.valueset;

import com.github.thomasfox.sailboatcalculator.calculate.MaterialConstants;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;

public abstract class Foil extends Wing
{
  public Foil(String id, String name)
  {
    super(id, name);
    addToInput(PhysicalQuantity.SECOND_MOMENT_OF_AREA);
    setFixedValueNoOverwrite(MaterialConstants.DENSITY_WATER);
    setFixedValueNoOverwrite(MaterialConstants.KINEMATIC_VISCOSITY_WATER);
  }
}
