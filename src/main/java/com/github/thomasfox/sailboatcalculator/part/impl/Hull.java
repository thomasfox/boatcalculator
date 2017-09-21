package com.github.thomasfox.sailboatcalculator.part.impl;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.part.BoatPart;
import com.github.thomasfox.sailboatcalculator.part.PartType;

public class Hull extends BoatPart
{
  public Hull()
  {
    super(PartType.HULL);
    toInput.add(PhysicalQuantity.WEIGHT);
  }
}
