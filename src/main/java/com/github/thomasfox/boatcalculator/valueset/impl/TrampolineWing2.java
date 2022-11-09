package com.github.thomasfox.boatcalculator.valueset.impl;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;

public class TrampolineWing2 extends TrampolineWing
{
  public static final String ID = TrampolineWing2.class.getSimpleName();

  private static final String NAME = "TrampolinWing2";

  public TrampolineWing2()
  {
    super(ID, NAME);
    addHiddenOutput(PhysicalQuantity.WING_SPAN_IN_MEDIUM);
    addHiddenOutput(PhysicalQuantity.INDUCED_DRAG);
    addHiddenOutput(PhysicalQuantity.INDUCED_DRAG_COEFFICIENT);
    addHiddenOutput(PhysicalQuantity.SIDEWAY_ANGLE);
    addHiddenOutput(PhysicalQuantity.BACKWAY_ANGLE);
    addHiddenOutput(PhysicalQuantity.WING_CHORD);
    removeToInput(PhysicalQuantity.WING_CHORD);
    removeToInput(PhysicalQuantity.SIDEWAY_ANGLE);
    removeToInput(PhysicalQuantity.BACKWAY_ANGLE);
    removeToInput(PhysicalQuantity.HALFWING_SPAN);
    removeToInput(PhysicalQuantity.CENTER_OF_EFFORT_HEIGHT);
  }
}
