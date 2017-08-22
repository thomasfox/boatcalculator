package com.github.thomasfox.wingcalculator.part.impl;

import com.github.thomasfox.wingcalculator.calculate.MaterialConstants;
import com.github.thomasfox.wingcalculator.part.PartType;

public class Daggerboard extends Wing
{
  public Daggerboard()
  {
    super(PartType.DAGGERBOARD);
    setFixedValueNoOverwide(MaterialConstants.DENSITY_WATER);
    setFixedValueNoOverwide(MaterialConstants.KINEMATIC_VISCOSITY_WATER);
  }

}
