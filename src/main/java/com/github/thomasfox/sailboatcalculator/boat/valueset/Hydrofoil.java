package com.github.thomasfox.sailboatcalculator.boat.valueset;

import com.github.thomasfox.sailboatcalculator.calculate.MaterialConstants;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;

public abstract class Hydrofoil extends Wing
{
  public Hydrofoil(String id, String name)
  {
    super(id, name);
    setFixedValueNoOverwrite(MaterialConstants.DENSITY_WATER);
    setFixedValueNoOverwrite(MaterialConstants.KINEMATIC_VISCOSITY_WATER);
    addToInput(PhysicalQuantity.WING_CHORD);
  }
}