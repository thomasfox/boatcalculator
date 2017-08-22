package com.github.thomasfox.wingcalculator.boat.impl;

import com.github.thomasfox.wingcalculator.boat.Boat;
import com.github.thomasfox.wingcalculator.part.BoatPart;
import com.github.thomasfox.wingcalculator.part.impl.Crew;
import com.github.thomasfox.wingcalculator.part.impl.Daggerboard;
import com.github.thomasfox.wingcalculator.part.impl.Hull;
import com.github.thomasfox.wingcalculator.part.impl.Rudder;
import com.github.thomasfox.wingcalculator.part.impl.Sail;

public class Dinghy extends Boat
{
  protected BoatPart hull = new Hull();

  protected BoatPart sail = new Sail();

  protected BoatPart crew = new Crew();

  protected BoatPart daggerboard = new Daggerboard();

  protected BoatPart rudder = new Rudder();

  public Dinghy()
  {
    parts.add(hull);
    parts.add(sail);
    parts.add(crew);
    parts.add(daggerboard);
    parts.add(rudder);
  }
}
