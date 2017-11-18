package com.github.thomasfox.sailboatcalculator.boat.valueset;

import com.github.thomasfox.sailboatcalculator.calculate.MaterialConstants;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;

public class DaggerboardOrKeel extends Wing
{
  public static final String ID = DaggerboardOrKeel.class.getSimpleName();

  private static final String NAME = "Schwert/Kiel";

  public DaggerboardOrKeel()
  {
    super(ID, NAME);
    setFixedValueNoOverwrite(MaterialConstants.DENSITY_WATER);
    setFixedValueNoOverwrite(MaterialConstants.KINEMATIC_VISCOSITY_WATER);
    addToInput(PhysicalQuantity.WING_CHORD);
  }

}
