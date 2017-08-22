package com.github.thomasfox.wingcalculator.part.impl;

import com.github.thomasfox.wingcalculator.calculate.MaterialConstants;
import com.github.thomasfox.wingcalculator.part.PartType;

public class Rudder extends Wing
{
  public Rudder()
  {
    super(PartType.RUDDER);
    setFixedValueNoOverwide(MaterialConstants.DENSITY_WATER);
    setFixedValueNoOverwide(MaterialConstants.KINEMATIC_VISCOSITY_WATER);
  }

}
