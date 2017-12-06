package com.github.thomasfox.sailboatcalculator.boat.valueset;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;

public class SurfacePiercingDaggerboard extends DaggerboardOrKeel
{
  public SurfacePiercingDaggerboard()
  {
    super.removeToInput(PhysicalQuantity.WING_SPAN);
  }
}
