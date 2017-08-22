package com.github.thomasfox.wingcalculator.boat;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.github.thomasfox.wingcalculator.calculate.NamedValueSet;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.wingcalculator.part.BoatPart;

public abstract class Boat extends NamedValueSet
{
  protected final Set<BoatPart> parts = new LinkedHashSet<>();

  public Boat()
  {
    toInput.add(PhysicalQuantity.VELOCITY);
    toInput.add(PhysicalQuantity.POINTING_ANGLE);
    toInput.add(PhysicalQuantity.WIND_SPEED);
  }

  public Set<BoatPart> getParts()
  {
    return Collections.unmodifiableSet(parts);
  }

  @Override
  public String getName()
  {
    return "Boot";
  }
}
