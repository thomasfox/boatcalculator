package com.github.thomasfox.sailboatcalculator.boat.valueset;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;

public class Rudder extends Hydrofoil
{
  public static final String ID = Rudder.class.getSimpleName();

  private static final String NAME = "Ruder";

  public Rudder()
  {
    super(ID, NAME);
    addToInput(PhysicalQuantity.LIFT);
  }
}