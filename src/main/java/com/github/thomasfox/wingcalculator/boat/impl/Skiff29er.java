package com.github.thomasfox.wingcalculator.boat.impl;

import java.io.File;

import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.wingcalculator.calculate.strategy.IncreaseQuantityTillOtherReachesUpperLimitStrategy;
import com.github.thomasfox.wingcalculator.calculate.strategy.QuantityEquality;
import com.github.thomasfox.wingcalculator.calculate.strategy.TwoValuesShouldBeEqualModifyThirdStrategy;
import com.github.thomasfox.wingcalculator.gui.SwingGui;
import com.github.thomasfox.wingcalculator.interpolate.QuantityRelationsLoader;
import com.github.thomasfox.wingcalculator.part.PartType;

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
    sail.setStartValueNoOverwrite(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT, 0.03); // rough estimate
    sail.setStartValueNoOverwrite(PhysicalQuantity.ANGLE_OF_ATTACK, 20); // rough estimate
    crew.setStartValueNoOverwrite(PhysicalQuantity.WEIGHT, 80d); // single person
    leverSailDaggerboard.setStartValueNoOverwrite(PhysicalQuantity.LEVER_BETWEEN_FORCES, 4); // rough estimate
    hull.setStartValueNoOverwrite(PhysicalQuantity.WEIGHT, 204d);
    hull.getQuantityRelations().add(new QuantityRelationsLoader().load(new File(SwingGui.HULL_DIRECTORY, "29er.txt"), "29er Hull"));

    values.add(new TwoValuesShouldBeEqualModifyThirdStrategy(
        PhysicalQuantity.DRIVING_FORCE, PartType.SAIL.name(),
        PhysicalQuantity.TOTAL_DRAG, PartType.HULL.name(),
        PhysicalQuantity.VELOCITY, EXTERNAL_SETTINGS_ID,
        0d, 100d));
    values.add(new IncreaseQuantityTillOtherReachesUpperLimitStrategy(PhysicalQuantity.LEVER_WEIGHT, PartType.CREW.name(), 1.8, PhysicalQuantity.LIFT_COEFFICIENT, PartType.SAIL.name(), 1.2)); // 1.8m: single person, CoG 91.5 cm from rim in trapeze; caMax=1.2: rough estimate
    values.add(new QuantityEquality(PhysicalQuantity.VELOCITY, EXTERNAL_SETTINGS_ID, PhysicalQuantity.VELOCITY, PartType.HULL.name()));
  }
}
