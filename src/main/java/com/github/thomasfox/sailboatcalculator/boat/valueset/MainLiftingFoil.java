package com.github.thomasfox.sailboatcalculator.boat.valueset;

public class MainLiftingFoil extends Hydrofoil
{
  public static final String ID = MainLiftingFoil.class.getSimpleName();

  private static final String NAME = "Haupttragfl�che";

  public MainLiftingFoil()
  {
    super(ID, NAME);
  }
}
