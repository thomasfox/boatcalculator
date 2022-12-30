package com.github.thomasfox.boatcalculator.valueset.impl;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;

public class TrampolineWindwardWing extends TrampolineWing
{
  public static final String ID = TrampolineWindwardWing.class.getSimpleName();

  private static final String NAME = "TrampolinWindwardWing";

  public TrampolineWindwardWing()
  {
    super(ID, NAME);
    addHiddenOutput(PhysicalQuantity.WING_SPAN_IN_MEDIUM);
    addHiddenOutput(PhysicalQuantity.SIDEWAY_ANGLE);
    addHiddenOutput(PhysicalQuantity.BACKWAY_ANGLE);
    addHiddenOutput(PhysicalQuantity.WING_CHORD);
    removeToInput(PhysicalQuantity.WING_CHORD);
    removeToInput(PhysicalQuantity.SIDEWAY_ANGLE);
    removeToInput(PhysicalQuantity.BACKWAY_ANGLE);
    removeToInput(PhysicalQuantity.CENTER_OF_EFFORT_HEIGHT);
  }
}
