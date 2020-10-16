package com.github.thomasfox.boatcalculator.valueset.impl;

import com.github.thomasfox.boatcalculator.valueset.SimpleValueSet;

/**
 * Enthält die Daten, wann das Boot abhebt (der Rumpf keinen Widerstand mehr hat)
 */
public class Takeoff extends SimpleValueSet
{
  public static final String ID = Takeoff.class.getSimpleName();

  private static final String NAME = "Abheben";

  public Takeoff()
  {
    super(ID, NAME);
  }
}
