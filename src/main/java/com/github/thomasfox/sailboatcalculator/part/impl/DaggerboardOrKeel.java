package com.github.thomasfox.sailboatcalculator.part.impl;

import com.github.thomasfox.sailboatcalculator.calculate.MaterialConstants;
import com.github.thomasfox.sailboatcalculator.part.PartType;

public class DaggerboardOrKeel extends Wing
{
  public DaggerboardOrKeel()
  {
    super(PartType.DAGGERBOARD);
    setFixedValueNoOverwrite(MaterialConstants.DENSITY_WATER);
    setFixedValueNoOverwrite(MaterialConstants.KINEMATIC_VISCOSITY_WATER);
  }

}
