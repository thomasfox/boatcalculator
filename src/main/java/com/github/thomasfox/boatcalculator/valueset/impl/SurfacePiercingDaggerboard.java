package com.github.thomasfox.boatcalculator.valueset.impl;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;

public class SurfacePiercingDaggerboard extends DaggerboardOrKeel
{
  public SurfacePiercingDaggerboard()
  {
    super.removeToInput(PhysicalQuantity.WING_SPAN);
  }
}
