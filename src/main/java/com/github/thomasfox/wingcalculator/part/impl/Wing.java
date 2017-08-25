package com.github.thomasfox.wingcalculator.part.impl;

import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.wingcalculator.part.BoatPart;
import com.github.thomasfox.wingcalculator.part.PartType;

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
    toInput.add(PhysicalQuantity.WING_SPAN);
    toInput.add(PhysicalQuantity.WING_CHORD);
  }
}
