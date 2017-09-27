package com.github.thomasfox.sailboatcalculator.part.impl;

import com.github.thomasfox.sailboatcalculator.calculate.MaterialConstants;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.part.PartType;

public class Rudder extends Wing
{
  public Rudder()
  {
    super(PartType.RUDDER);
    setFixedValueNoOverwrite(MaterialConstants.DENSITY_WATER);
    setFixedValueNoOverwrite(MaterialConstants.KINEMATIC_VISCOSITY_WATER);
    toInput.add(PhysicalQuantity.WING_CHORD);
  }

}
