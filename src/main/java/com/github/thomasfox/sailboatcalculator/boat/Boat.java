package com.github.thomasfox.sailboatcalculator.boat;

import java.util.Set;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.strategy.QuantityEquality;
import com.github.thomasfox.sailboatcalculator.calculate.value.AllValues;
import com.github.thomasfox.sailboatcalculator.calculate.value.NamedValueSet;
import com.github.thomasfox.sailboatcalculator.part.BoatPart;
import com.github.thomasfox.sailboatcalculator.part.PartType;
import com.github.thomasfox.sailboatcalculator.part.impl.DaggerboardOrKeel;
import com.github.thomasfox.sailboatcalculator.part.impl.Hull;
import com.github.thomasfox.sailboatcalculator.part.impl.Rudder;
import com.github.thomasfox.sailboatcalculator.part.impl.Sail;

public abstract class Boat
{
  protected static final String LEVER_SAIL_DAGGERBOARD_ID = "leverSailDaggerboard";

  public static final String EXTERNAL_SETTINGS_ID = "externalSettings";

  protected NamedValueSet externalSettings = new NamedValueSet(EXTERNAL_SETTINGS_ID, "Externe Parameter");

  protected NamedValueSet leverSailDaggerboard = new NamedValueSet(LEVER_SAIL_DAGGERBOARD_ID, "Hebel Schwert/Segel");

  protected BoatPart sail = new Sail();

  protected BoatPart hull = new Hull();

  protected BoatPart daggerboardOrKeel = new DaggerboardOrKeel();

  protected BoatPart rudder = new Rudder();

  protected AllValues values = new AllValues();

  public Boat()
  {
    addPart(hull);
    addPart(sail);
    addPart(daggerboardOrKeel);
    addPart(rudder);

    leverSailDaggerboard.addToInput(PhysicalQuantity.LEVER_BETWEEN_FORCES);
    values.add(leverSailDaggerboard);

    externalSettings.addToInput(PhysicalQuantity.WIND_SPEED);
    externalSettings.addToInput(PhysicalQuantity.POINTING_ANGLE);
    externalSettings.addToInput(PhysicalQuantity.DRIFT_ANGLE);
    values.add(externalSettings);

//    values.add(new DriftToStableStateStrategy(PhysicalQuantity.ANGLE_OF_ATTACK, PartType.DAGGERBOARD.name(), PhysicalQuantity.DRIFT_ANGLE, EXTERNAL_SETTINGS_ID, 0d));
    values.add(new QuantityEquality(PhysicalQuantity.VELOCITY, EXTERNAL_SETTINGS_ID, PhysicalQuantity.VELOCITY, PartType.RUDDER.name()));
    values.add(new QuantityEquality(PhysicalQuantity.VELOCITY, EXTERNAL_SETTINGS_ID, PhysicalQuantity.VELOCITY, PartType.DAGGERBOARD.name()));
    values.add(new QuantityEquality(PhysicalQuantity.LATERAL_FORCE, PartType.SAIL.name(), PhysicalQuantity.LIFT, PartType.DAGGERBOARD.name())); // assumption: rudder has no force
    values.add(new QuantityEquality(PhysicalQuantity.APPARENT_WIND_SPEED, EXTERNAL_SETTINGS_ID, PhysicalQuantity.VELOCITY, PartType.SAIL.name()));
    values.add(new QuantityEquality(PhysicalQuantity.APPARENT_WIND_ANGLE, EXTERNAL_SETTINGS_ID, PhysicalQuantity.FLOW_DIRECTION, PartType.SAIL.name()));
    values.add(new QuantityEquality(PhysicalQuantity.APPARENT_WIND_ANGLE, EXTERNAL_SETTINGS_ID, PhysicalQuantity.FLOW_DIRECTION, PartType.SAIL.name()));
    values.add(new QuantityEquality(PhysicalQuantity.LATERAL_FORCE, PartType.SAIL.name(), PhysicalQuantity.FORCE, LEVER_SAIL_DAGGERBOARD_ID));
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
