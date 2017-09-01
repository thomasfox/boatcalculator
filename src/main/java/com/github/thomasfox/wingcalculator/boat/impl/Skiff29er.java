package com.github.thomasfox.wingcalculator.boat.impl;

import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;

public class Skiff29er extends Dinghy
{
  public Skiff29er()
  {
    rudder.setStartValueNoOverwrite(PhysicalQuantity.WING_SPAN, 0.77d); // full blade span 92 cm, estimated box size 15 cm
    rudder.setStartValueNoOverwrite(PhysicalQuantity.WING_CHORD, 0.223d);
    daggerboardOrKeel.setStartValueNoOverwrite(PhysicalQuantity.WING_SPAN, 0.985d); // full blade span 118.5 cm, estimated box size 20 cm
    daggerboardOrKeel.setStartValueNoOverwrite(PhysicalQuantity.WING_CHORD, 0.32d);
    sail.setStartValueNoOverwrite(PhysicalQuantity.WING_SPAN, 6d); // rough estimate
    sail.setStartValueNoOverwrite(PhysicalQuantity.WING_CHORD, 2d); // rough estimate
    sail.setStartValueNoOverwrite(PhysicalQuantity.LIFT_COEFFICIENT, 1.2); // rough estimate
    sail.setStartValueNoOverwrite(PhysicalQuantity.TOTAL_DRAG_COEFFICIENT, 0.15); // rough estimate
    sail.setStartValueNoOverwrite(PhysicalQuantity.ANGLE_OF_ATTACK, 20); // rough estimate
    crew.setStartValueNoOverwrite(PhysicalQuantity.WEIGHT, 80d); // single person
    leverSailDaggerboard.setStartValueNoOverwrite(PhysicalQuantity.LEVER_BETWEEN_FORCES, 4); // rough estimate
  }
}
