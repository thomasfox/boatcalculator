package com.github.thomasfox.wingcalculator.boat.impl;

import com.github.thomasfox.wingcalculator.boat.Boat;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.wingcalculator.calculate.strategy.QuantityEquality;
import com.github.thomasfox.wingcalculator.part.BoatPart;
import com.github.thomasfox.wingcalculator.part.PartType;
import com.github.thomasfox.wingcalculator.part.impl.Crew;

public class Dinghy extends Boat
{
  protected BoatPart crew = new Crew();


  public Dinghy()
  {
    addPart(crew);
    values.add(new QuantityEquality(PhysicalQuantity.TORQUE_BETWEEN_FORCES, LEVER_SAIL_DAGGERBOARD_ID, PhysicalQuantity.TORQUE_BETWEEN_FORCES, PartType.CREW.name()));
  }
}
