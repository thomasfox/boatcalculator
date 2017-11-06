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
    setFixedValueNoOverwrite(MaterialConstants.DENSITY_AIR);
    setFixedValueNoOverwrite(MaterialConstants.KINEMATIC_VISCOSITY_AIR);
    toInput.add(PhysicalQuantity.WEIGHT);
    toInput.add(PhysicalQuantity.WING_AREA);
    toInput.add(PhysicalQuantity.PARASITIC_DRAG_COEFFICIENT);
  }
}
