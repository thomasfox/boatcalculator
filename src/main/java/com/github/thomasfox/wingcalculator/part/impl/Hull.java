package com.github.thomasfox.wingcalculator.part.impl;

import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.wingcalculator.part.BoatPart;
import com.github.thomasfox.wingcalculator.part.PartType;

public class Hull extends BoatPart
{
  public Hull()
  {
    super(PartType.HULL);
    toInput.add(PhysicalQuantity.WEIGHT);
  }
}
