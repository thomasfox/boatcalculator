package com.github.thomasfox.wingcalculator.boat;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.github.thomasfox.wingcalculator.calculate.NamedValueSet;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.wingcalculator.equality.QuantityEquality;
import com.github.thomasfox.wingcalculator.part.BoatPart;
import com.github.thomasfox.wingcalculator.part.impl.Daggerboard;
import com.github.thomasfox.wingcalculator.part.impl.Rudder;
import com.github.thomasfox.wingcalculator.part.impl.Sail;

public abstract class Boat extends NamedValueSet
{
  private final Set<BoatPart> parts = new LinkedHashSet<>();

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

  public void addPart(BoatPart part)
  {
    parts.add(part);
    if (part instanceof Rudder || part instanceof Daggerboard)
    {
      part.getQuantityEqualities().add(new QuantityEquality(PhysicalQuantity.VELOCITY, this, PhysicalQuantity.VELOCITY));
    }
    if (part instanceof Sail)
    {
      part.getQuantityEqualities().add(new QuantityEquality(PhysicalQuantity.APPARENT_WIND_SPEED, this, PhysicalQuantity.VELOCITY));
      part.getQuantityEqualities().add(new QuantityEquality(PhysicalQuantity.APPARENT_WIND_ANGLE, this, PhysicalQuantity.FLOW_DIRECTION));
    }
  }
}
