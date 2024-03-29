package com.github.thomasfox.boatcalculator.valueset.impl;

import com.github.thomasfox.boatcalculator.calculate.MaterialConstants;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;

public class TrampolineWing extends Wing
{
  public static final String ID = TrampolineWing.class.getSimpleName();

  private static final String NAME = "TrampolinWing";

  public TrampolineWing()
  {
    this(ID, NAME);
  }

  public TrampolineWing(String id, String name)
  {
    super(id, name);
    removeToInput(PhysicalQuantity.WING_SPAN);
    addToInput(PhysicalQuantity.HALFWING_SPAN);
    setFixedValueNoOverwrite(MaterialConstants.DENSITY_AIR);
    setFixedValueNoOverwrite(MaterialConstants.KINEMATIC_VISCOSITY_AIR);
    setFixedValueNoOverwrite(PhysicalQuantity.WAVE_MAKING_DRAG_COEFFICIENT, 0);
    setFixedValueNoOverwrite(PhysicalQuantity.SURFACE_PIERCING_DRAG_COEFFICIENT, 0);
    addHiddenOutput(PhysicalQuantity.WING_SPAN_IN_MEDIUM);
    addHiddenOutput(PhysicalQuantity.WAVE_MAKING_DRAG);
    addHiddenOutput(PhysicalQuantity.SURFACE_PIERCING_DRAG);
    addToInput(PhysicalQuantity.WING_CHORD);
    addToInput(PhysicalQuantity.SIDEWAY_ANGLE);
    addToInput(PhysicalQuantity.BACKWAY_ANGLE);
    addToInput(PhysicalQuantity.CENTER_OF_EFFORT_HEIGHT);
    setProfileName("flat-plate-ar1");
  }
}
