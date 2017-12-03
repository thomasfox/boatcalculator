package com.github.thomasfox.sailboatcalculator.boat.valueset;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;

public class MainLiftingFoil extends Hydrofoil
{
  public static final String ID = MainLiftingFoil.class.getSimpleName();

  private static final String NAME = "Haupttragfläche";

  public MainLiftingFoil()
  {
    super(ID, NAME);
    addToInput(PhysicalQuantity.SUBMERGENCE_DEPTH);
  }
}
