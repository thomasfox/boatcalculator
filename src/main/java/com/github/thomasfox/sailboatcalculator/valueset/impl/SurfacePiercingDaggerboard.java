package com.github.thomasfox.sailboatcalculator.valueset.impl;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;

public class SurfacePiercingDaggerboard extends DaggerboardOrKeel
{
  public SurfacePiercingDaggerboard()
  {
    super.removeToInput(PhysicalQuantity.WING_SPAN);
  }
}
