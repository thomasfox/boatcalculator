package com.github.thomasfox.wingcalculator.boat.impl;

import com.github.thomasfox.wingcalculator.boat.Boat;
import com.github.thomasfox.wingcalculator.boat.BoatPart;
import com.github.thomasfox.wingcalculator.boat.PartType;

public class Dinghy extends Boat
{

  protected BoatPart hull = new BoatPart(PartType.HULL);

  protected BoatPart sail = new BoatPart(PartType.SAIL);

  protected BoatPart crew = new BoatPart(PartType.CREW);

  protected BoatPart daggerboard = new BoatPart(PartType.DAGGERBOARD);

  protected BoatPart rudder = new BoatPart(PartType.RUDDER);

  public Dinghy()
  {
    parts.add(hull);
    parts.add(sail);
    parts.add(crew);
    parts.add(daggerboard);
    parts.add(rudder);
  }
}
