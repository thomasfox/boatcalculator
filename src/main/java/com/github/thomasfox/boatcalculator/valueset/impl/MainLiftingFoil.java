package com.github.thomasfox.boatcalculator.valueset.impl;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;

public class MainLiftingFoil extends Hydrofoil
{
  public static final String ID = MainLiftingFoil.class.getSimpleName();

  private static final String NAME = "Haupttragfläche";

  public MainLiftingFoil()
  {
    super(ID, NAME);
    setStartValue(PhysicalQuantity.MAX_ANGLE_OF_ATTACK, 5d);
    setStartValue(PhysicalQuantity.WING_INNER_CHORD, 0.11);
    setStartValue(PhysicalQuantity.WING_OUTER_CHORD, 0.11);
    setFixedValueNoOverwrite(PhysicalQuantity.SURFACE_PIERCING_DRAG_COEFFICIENT, 0d);
    addToInput(PhysicalQuantity.MAX_ANGLE_OF_ATTACK);
    removeToInput(PhysicalQuantity.WING_CHORD);
    removeStartValue(PhysicalQuantity.WING_CHORD);
    addToInput(PhysicalQuantity.WING_INNER_CHORD);
    addToInput(PhysicalQuantity.WING_OUTER_CHORD);
    addHiddenOutput(PhysicalQuantity.SURFACE_PIERCING_DRAG);
    addHiddenOutput(PhysicalQuantity.SURFACE_PIERCING_DRAG_COEFFICIENT);
  }
}
