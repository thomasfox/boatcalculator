package com.github.thomasfox.sailboatcalculator.part.impl;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.part.BoatPart;
import com.github.thomasfox.sailboatcalculator.part.PartType;

import lombok.Getter;
import lombok.Setter;

public abstract class Wing extends BoatPart
{
  @Getter
  @Setter
  private String profileName;

  public Wing(PartType type)
  {
    super(type);
    addToInput(PhysicalQuantity.WING_SPAN);
    setFixedValueNoOverwrite(PhysicalQuantity.NCRIT, 9d);
  }
}
