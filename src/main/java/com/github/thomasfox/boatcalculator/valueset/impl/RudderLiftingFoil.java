package com.github.thomasfox.boatcalculator.valueset.impl;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;

public class RudderLiftingFoil extends Hydrofoil
{
  public static final String ID = RudderLiftingFoil.class.getSimpleName();

  private static final String NAME = "Rudertragfläche";

  public RudderLiftingFoil()
  {
    super(ID, NAME);
    setStartValue(PhysicalQuantity.MAX_ANGLE_OF_ATTACK, 5d);
    setFixedValueNoOverwrite(PhysicalQuantity.SURFACE_PIERCING_DRAG_COEFFICIENT, 0d);
    addToInput(PhysicalQuantity.MAX_ANGLE_OF_ATTACK);
    addToInput(PhysicalQuantity.ANGLE_OF_ATTACK);
    addHiddenOutput(PhysicalQuantity.SURFACE_PIERCING_DRAG);
    addHiddenOutput(PhysicalQuantity.SURFACE_PIERCING_DRAG_COEFFICIENT);
  }
}
