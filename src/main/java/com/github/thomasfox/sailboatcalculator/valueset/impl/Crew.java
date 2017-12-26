package com.github.thomasfox.sailboatcalculator.valueset.impl;

import com.github.thomasfox.sailboatcalculator.calculate.MaterialConstants;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.valueset.SimpleValueSet;

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
    addToInput(PhysicalQuantity.MASS);
    addToInput(PhysicalQuantity.AREA);
    addToInput(PhysicalQuantity.PARASITIC_DRAG_COEFFICIENT);
  }
}
