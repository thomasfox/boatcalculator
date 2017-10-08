package com.github.thomasfox.sailboatcalculator.boat;

import java.util.Set;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.strategy.DriftToStableStateStrategy;
import com.github.thomasfox.sailboatcalculator.calculate.strategy.LeverSailDaggerboardStrategy;
import com.github.thomasfox.sailboatcalculator.calculate.strategy.QuantityEquality;
import com.github.thomasfox.sailboatcalculator.calculate.value.AllValues;
import com.github.thomasfox.sailboatcalculator.calculate.value.NamedValueSet;
import com.github.thomasfox.sailboatcalculator.part.BoatPart;
import com.github.thomasfox.sailboatcalculator.part.PartType;
import com.github.thomasfox.sailboatcalculator.part.impl.DaggerboardOrKeel;
import com.github.thomasfox.sailboatcalculator.part.impl.Hull;
import com.github.thomasfox.sailboatcalculator.part.impl.Rigg;
import com.github.thomasfox.sailboatcalculator.part.impl.Rudder;

public abstract class Boat
{
  protected static final String LEVER_SAIL_DAGGERBOARD_ID = "leverSailDaggerboard";

  public static final String BOAT_ID = "boat";

  protected NamedValueSet boat = new NamedValueSet(BOAT_ID, "Boot");

  protected NamedValueSet leverSailDaggerboard = new NamedValueSet(LEVER_SAIL_DAGGERBOARD_ID, "Hebel Schwert/Segel");

  protected Rigg rigg = new Rigg();

  protected Hull hull = new Hull();

  protected DaggerboardOrKeel daggerboardOrKeel = new DaggerboardOrKeel();

  protected Rudder rudder = new Rudder();

  protected AllValues values = new AllValues();

  public Boat()
  {
    addPart(hull);
    addPart(rigg);
    addPart(daggerboardOrKeel);
    addPart(rudder);

    values.add(leverSailDaggerboard);

    boat.addToInput(PhysicalQuantity.WIND_SPEED);
    boat.addToInput(PhysicalQuantity.POINTING_ANGLE);
    boat.addToInput(PhysicalQuantity.DRIFT_ANGLE);
    boat.addToInput(PhysicalQuantity.WEIGHT);
    values.add(boat);

    values.add(new DriftToStableStateStrategy(PhysicalQuantity.ANGLE_OF_ATTACK, PartType.DAGGERBOARD.name(), PhysicalQuantity.DRIFT_ANGLE, BOAT_ID, 0d));
    values.add(new QuantityEquality(PhysicalQuantity.VELOCITY, BOAT_ID, PhysicalQuantity.VELOCITY, PartType.RUDDER.name()));
    values.add(new QuantityEquality(PhysicalQuantity.VELOCITY, BOAT_ID, PhysicalQuantity.VELOCITY, PartType.DAGGERBOARD.name()));
    values.add(new QuantityEquality(PhysicalQuantity.VELOCITY, BOAT_ID, PhysicalQuantity.VELOCITY, PartType.HULL.name()));
    values.add(new QuantityEquality(PhysicalQuantity.LATERAL_FORCE, PartType.RIGG.name(), PhysicalQuantity.LIFT, PartType.DAGGERBOARD.name())); // assumption: rudder has no force
    values.add(new QuantityEquality(PhysicalQuantity.APPARENT_WIND_SPEED, BOAT_ID, PhysicalQuantity.VELOCITY, PartType.RIGG.name()));
    values.add(new QuantityEquality(PhysicalQuantity.APPARENT_WIND_ANGLE, BOAT_ID, PhysicalQuantity.FLOW_DIRECTION, PartType.RIGG.name()));
    values.add(new QuantityEquality(PhysicalQuantity.APPARENT_WIND_ANGLE, BOAT_ID, PhysicalQuantity.FLOW_DIRECTION, PartType.RIGG.name()));
    values.add(new QuantityEquality(PhysicalQuantity.LATERAL_FORCE, PartType.RIGG.name(), PhysicalQuantity.FORCE, LEVER_SAIL_DAGGERBOARD_ID));
    values.add(new LeverSailDaggerboardStrategy(PartType.RIGG.name(), PartType.DAGGERBOARD.name(), LEVER_SAIL_DAGGERBOARD_ID));
}

  public Set<NamedValueSet> getNamedValueSets()
  {
    return values.getNamedValueSets();
  }

  public NamedValueSet getNamedValueSetNonNull(String name)
  {
    return values.getNamedValueSetNonNull(name);
  }

  public void addPart(BoatPart part)
  {
    values.add(part);
  }

  public void calculate()
  {
    values.calculate();
  }
}
