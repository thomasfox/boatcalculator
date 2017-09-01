package com.github.thomasfox.wingcalculator.part.impl;

import com.github.thomasfox.wingcalculator.calculate.MaterialConstants;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.wingcalculator.part.BoatPart;
import com.github.thomasfox.wingcalculator.part.PartType;

public class Crew extends BoatPart
{
  public Crew()
  {
    super(PartType.CREW);
    setFixedValueNoOverwrite(MaterialConstants.GRAVITY_ACCELERATION);
    toInput.add(PhysicalQuantity.WEIGHT);
  }
}
