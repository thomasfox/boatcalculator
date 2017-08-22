package com.github.thomasfox.wingcalculator.part.impl;

import com.github.thomasfox.wingcalculator.calculate.MaterialConstants;
import com.github.thomasfox.wingcalculator.part.PartType;

public class Sail extends Wing
{
  public Sail()
  {
    super(PartType.SAIL);
    setFixedValueNoOverwide(MaterialConstants.DENSITY_AIR);
    setFixedValueNoOverwide(MaterialConstants.KINEMATIC_VISCOSITY_AIR);
  }

}
