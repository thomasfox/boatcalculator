package com.github.thomasfox.sailboatcalculator.boat.valueset;

import com.github.thomasfox.sailboatcalculator.calculate.MaterialConstants;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.SimpleValueSet;

public class Crew extends SimpleValueSet
{
  public static final String ID = Crew.class.getSimpleName();

  private static final String NAME = "Besatzung";

  public Crew()
  {
    super(ID, NAME);
    setFixedValueNoOverwrite(MaterialConstants.GRAVITY_ACCELERATION);
    setFixedValueNoOverwrite(MaterialConstants.DENSITY_AIR);
    setFixedValueNoOverwrite(MaterialConstants.KINEMATIC_VISCOSITY_AIR);
    addToInput(PhysicalQuantity.WEIGHT);
    addToInput(PhysicalQuantity.WING_AREA);
    addToInput(PhysicalQuantity.PARASITIC_DRAG_COEFFICIENT);
  }
}
