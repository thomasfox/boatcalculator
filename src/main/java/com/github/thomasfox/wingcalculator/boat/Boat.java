package com.github.thomasfox.wingcalculator.boat;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.github.thomasfox.wingcalculator.calculate.NamedValueSet;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.wingcalculator.calculate.strategy.ComputationStrategy;
import com.github.thomasfox.wingcalculator.calculate.strategy.QuantityEquality;
import com.github.thomasfox.wingcalculator.part.BoatPart;
import com.github.thomasfox.wingcalculator.part.impl.DaggerboardOrKeel;
import com.github.thomasfox.wingcalculator.part.impl.Hull;
import com.github.thomasfox.wingcalculator.part.impl.Rudder;
import com.github.thomasfox.wingcalculator.part.impl.Sail;

public abstract class Boat extends NamedValueSet
{
  private final Set<BoatPart> parts = new LinkedHashSet<>();

  private final Set<NamedValueSet> qualifiedValues = new LinkedHashSet<>();

  protected NamedValueSet leverSailDaggerboard = new NamedValueSet("Hebel Schwert/Segel");

  protected BoatPart sail = new Sail();

  protected BoatPart hull = new Hull();

  protected BoatPart daggerboardOrKeel = new DaggerboardOrKeel();

  protected BoatPart rudder = new Rudder();

  public Boat()
  {
    super("Boot");

    addPart(hull);
    addPart(sail);
    addPart(daggerboardOrKeel);
    addPart(rudder);

    rudder.addComputationStrategy(new QuantityEquality(PhysicalQuantity.VELOCITY, this, PhysicalQuantity.VELOCITY));
    daggerboardOrKeel.addComputationStrategy(new QuantityEquality(PhysicalQuantity.VELOCITY, this, PhysicalQuantity.VELOCITY));
    daggerboardOrKeel.addComputationStrategy(new QuantityEquality(PhysicalQuantity.LATERAL_FORCE, sail, PhysicalQuantity.LIFT)); // assumption: rudder has no force
    sail.addComputationStrategy(new QuantityEquality(PhysicalQuantity.APPARENT_WIND_SPEED, this, PhysicalQuantity.VELOCITY));
    sail.addComputationStrategy(new QuantityEquality(PhysicalQuantity.APPARENT_WIND_ANGLE, this, PhysicalQuantity.FLOW_DIRECTION));
    sail.addComputationStrategy(new QuantityEquality(PhysicalQuantity.APPARENT_WIND_ANGLE, this, PhysicalQuantity.FLOW_DIRECTION));

    toInput.add(PhysicalQuantity.VELOCITY);
    toInput.add(PhysicalQuantity.WIND_SPEED);
    toInput.add(PhysicalQuantity.POINTING_ANGLE);

    leverSailDaggerboard.getComputationStrategies().add(new QuantityEquality(PhysicalQuantity.LATERAL_FORCE, sail, PhysicalQuantity.FORCE));
    leverSailDaggerboard.addToInput(PhysicalQuantity.LEVER_BETWEEN_FORCES);
    qualifiedValues.add(leverSailDaggerboard);
  }

  public Set<BoatPart> getParts()
  {
    return Collections.unmodifiableSet(parts);
  }

  public Set<NamedValueSet> getNamedValueSets()
  {
    Set<NamedValueSet> result = new LinkedHashSet<>();
    result.add(this);
    result.addAll(parts);
    result.addAll(qualifiedValues);
    return Collections.unmodifiableSet(result);
  }

  public void addPart(BoatPart part)
  {
    parts.add(part);
  }

  public void calculate(ComputationStrategy strategyToOmit)
  {
    boolean changed;
    int cutoff = 100;
    do
    {
      changed = false;
      for (NamedValueSet namedValueSet : getNamedValueSets())
      {
        boolean partChanged = namedValueSet.calculateSinglePass(strategyToOmit);
        changed = changed || partChanged;
      }
      cutoff--;
    }
    while (changed && cutoff > 0);
  }
}
