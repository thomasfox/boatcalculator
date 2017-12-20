package com.github.thomasfox.sailboatcalculator.valueset.impl;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;

public class MainLiftingFoil extends Hydrofoil
{
  public static final String ID = MainLiftingFoil.class.getSimpleName();

  private static final String NAME = "Haupttragfläche";

  public MainLiftingFoil()
  {
    super(ID, NAME);
    addToInput(PhysicalQuantity.SUBMERGENCE_DEPTH);
    addToInput(PhysicalQuantity.MAX_ANGLE_OF_ATTACK);
    setStartValue(PhysicalQuantity.MAX_ANGLE_OF_ATTACK, 10d);
  }
}
