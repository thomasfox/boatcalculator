package com.github.thomasfox.boatcalculator.valueset.impl;

import com.github.thomasfox.boatcalculator.calculate.MaterialConstants;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;

public abstract class Hydrofoil extends Wing
{
  public Hydrofoil(String id, String name)
  {
    super(id, name);
    setFixedValueNoOverwrite(MaterialConstants.DENSITY_WATER);
    addToInput(PhysicalQuantity.WING_CHORD);
    addToInput(PhysicalQuantity.SECOND_MOMENT_OF_AREA);
    addHiddenOutput(PhysicalQuantity.VELOCITY); // same velocity as boat
  }
}
