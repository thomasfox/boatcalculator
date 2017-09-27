package com.github.thomasfox.sailboatcalculator.boat.impl;

import com.github.thomasfox.sailboatcalculator.boat.Boat;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.strategy.QuantityEquality;
import com.github.thomasfox.sailboatcalculator.calculate.strategy.QuantitySum;
import com.github.thomasfox.sailboatcalculator.part.BoatPart;
import com.github.thomasfox.sailboatcalculator.part.PartType;
import com.github.thomasfox.sailboatcalculator.part.impl.Crew;

public class Dinghy extends Boat
{
  protected BoatPart crew = new Crew();


  public Dinghy()
  {
    addPart(crew);
    values.add(new QuantityEquality(PhysicalQuantity.TORQUE_BETWEEN_FORCES, LEVER_SAIL_DAGGERBOARD_ID, PhysicalQuantity.TORQUE_BETWEEN_FORCES, PartType.CREW.name()));
    values.add(new QuantitySum(PhysicalQuantity.WEIGHT, PartType.CREW.name(), PhysicalQuantity.WEIGHT, BOAT_ID, PhysicalQuantity.WEIGHT, PartType.HULL.name()));
  }
}
