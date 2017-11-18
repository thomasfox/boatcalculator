package com.github.thomasfox.sailboatcalculator.boat.impl;

import com.github.thomasfox.sailboatcalculator.boat.Boat;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.strategy.QuantityEquality;
import com.github.thomasfox.sailboatcalculator.calculate.strategy.QuantitySum;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityInSet;
import com.github.thomasfox.sailboatcalculator.part.BoatPart;
import com.github.thomasfox.sailboatcalculator.part.PartType;
import com.github.thomasfox.sailboatcalculator.part.impl.Crew;

public class Dinghy extends Boat
{
  protected BoatPart crew = new Crew();


  public Dinghy()
  {
    addPart(crew);

    crew.addHiddenOutput(PhysicalQuantity.VELOCITY);

    values.add(new QuantityEquality(PhysicalQuantity.TORQUE_BETWEEN_FORCES, LEVER_SAIL_DAGGERBOARD_ID, PhysicalQuantity.TORQUE_BETWEEN_FORCES, PartType.CREW.name()));
    values.add(new QuantitySum(
        new PhysicalQuantityInSet(PhysicalQuantity.WEIGHT, PartType.HULL.name()),
        new PhysicalQuantityInSet(PhysicalQuantity.WEIGHT, PartType.CREW.name()),
        new PhysicalQuantityInSet(PhysicalQuantity.WEIGHT, BOAT_ID)));
    values.add(new QuantitySum(
        new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, BOAT_ID),
        new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, PartType.HULL.name()),
        new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, PartType.RUDDER.name()),
        new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, PartType.DAGGERBOARD.name()),
        new PhysicalQuantityInSet(PhysicalQuantity.BRAKING_FORCE, PartType.CREW.name())));
  }
}
