package com.github.thomasfox.sailboatcalculator.boat.valueset;

import com.github.thomasfox.sailboatcalculator.calculate.MaterialConstants;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;

public class Rudder extends Wing
{
  public static final String ID = Rudder.class.getSimpleName();

  private static final String NAME = "Ruder";

  public Rudder()
  {
    super(ID, NAME);
    setFixedValueNoOverwrite(MaterialConstants.DENSITY_WATER);
    setFixedValueNoOverwrite(MaterialConstants.KINEMATIC_VISCOSITY_WATER);
    addToInput(PhysicalQuantity.WING_CHORD);
    addToInput(PhysicalQuantity.LIFT);
  }

}
