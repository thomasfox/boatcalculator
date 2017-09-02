package com.github.thomasfox.wingcalculator.boat.impl;

import com.github.thomasfox.wingcalculator.boat.Boat;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.wingcalculator.calculate.strategy.QuantityEquality;
import com.github.thomasfox.wingcalculator.part.BoatPart;
import com.github.thomasfox.wingcalculator.part.impl.Crew;

public class Dinghy extends Boat
{
  protected BoatPart crew = new Crew();


  public Dinghy()
  {
    addPart(crew);
    crew.getQuantityEqualities().add(new QuantityEquality(PhysicalQuantity.TORQUE_BETWEEN_FORCES, leverSailDaggerboard, PhysicalQuantity.TORQUE_BETWEEN_FORCES));
  }
}
