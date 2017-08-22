package com.github.thomasfox.wingcalculator.boat;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.github.thomasfox.wingcalculator.part.BoatPart;

public abstract class Boat
{
  protected final Set<BoatPart> parts = new LinkedHashSet<>();

  public Set<BoatPart> getParts()
  {
    return Collections.unmodifiableSet(parts);
  }
}
