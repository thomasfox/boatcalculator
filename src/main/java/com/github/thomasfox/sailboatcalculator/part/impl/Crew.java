package com.github.thomasfox.sailboatcalculator.part.impl;

import com.github.thomasfox.sailboatcalculator.calculate.MaterialConstants;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.part.BoatPart;
import com.github.thomasfox.sailboatcalculator.part.PartType;

public class Crew extends BoatPart
{
  public Crew()
  {
    super(PartType.CREW);
    setFixedValueNoOverwrite(MaterialConstants.GRAVITY_ACCELERATION);
    toInput.add(PhysicalQuantity.WEIGHT);
  }
}
