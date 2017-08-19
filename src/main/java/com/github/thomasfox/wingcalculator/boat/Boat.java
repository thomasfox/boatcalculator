package com.github.thomasfox.wingcalculator.boat;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class Boat
{
  protected final Set<BoatPart> parts = new HashSet<>();

  public Set<BoatPart> getParts()
  {
    return Collections.unmodifiableSet(parts);
  }
}
