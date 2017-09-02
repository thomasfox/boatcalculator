package com.github.thomasfox.wingcalculator.boat.impl;

import java.io.File;

import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.wingcalculator.equality.QuantityEquality;
import com.github.thomasfox.wingcalculator.gui.SwingGui;
import com.github.thomasfox.wingcalculator.interpolate.QuantityRelationsLoader;

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
    sail.setStartValueNoOverwrite(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT, 0.03); // rough estimate
    sail.setStartValueNoOverwrite(PhysicalQuantity.ANGLE_OF_ATTACK, 20); // rough estimate
    crew.setStartValueNoOverwrite(PhysicalQuantity.WEIGHT, 80d); // single person
    leverSailDaggerboard.setStartValueNoOverwrite(PhysicalQuantity.LEVER_BETWEEN_FORCES, 4); // rough estimate
    hull.setStartValueNoOverwrite(PhysicalQuantity.WEIGHT, 204d);
    hull.getQuantityRelations().add(new QuantityRelationsLoader().load(new File(SwingGui.HULL_DIRECTORY, "29er.txt"), "29er Hull"));
    hull.getQuantityEqualities().add(new QuantityEquality(PhysicalQuantity.VELOCITY, this, PhysicalQuantity.VELOCITY));
  }
}
