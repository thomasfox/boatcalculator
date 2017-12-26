package com.github.thomasfox.sailboatcalculator.boat;

import java.util.List;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.strategy.DriftToStableStateStrategy;
import com.github.thomasfox.sailboatcalculator.calculate.strategy.LeverSailDaggerboardStrategy;
import com.github.thomasfox.sailboatcalculator.calculate.strategy.QuantityEquality;
import com.github.thomasfox.sailboatcalculator.valueset.AllValues;
import com.github.thomasfox.sailboatcalculator.valueset.ValueSet;
import com.github.thomasfox.sailboatcalculator.valueset.impl.BoatGlobalValues;
import com.github.thomasfox.sailboatcalculator.valueset.impl.Crew;
import com.github.thomasfox.sailboatcalculator.valueset.impl.DaggerboardOrKeel;
import com.github.thomasfox.sailboatcalculator.valueset.impl.Hull;
import com.github.thomasfox.sailboatcalculator.valueset.impl.LeverSailDaggerboard;
import com.github.thomasfox.sailboatcalculator.valueset.impl.Rigg;
import com.github.thomasfox.sailboatcalculator.valueset.impl.Rudder;

public abstract class Boat
{
  protected BoatGlobalValues boatGlobalValues = new BoatGlobalValues();

  protected LeverSailDaggerboard leverSailDaggerboard = new LeverSailDaggerboard();

  protected Rigg rigg = new Rigg();

  protected Hull hull = new Hull();

  protected ValueSet daggerboardOrKeel = new DaggerboardOrKeel();

  protected Rudder rudder = new Rudder();

  protected AllValues values = new AllValues();

  public Boat()
  {
    addValueSet(boatGlobalValues);
    addValueSet(rigg);
    addValueSet(leverSailDaggerboard);
    addValueSet(hull);
    addValueSet(daggerboardOrKeel);
    addValueSet(rudder);

    values.add(new DriftToStableStateStrategy(PhysicalQuantity.ANGLE_OF_ATTACK, DaggerboardOrKeel.ID, PhysicalQuantity.DRIFT_ANGLE, BoatGlobalValues.ID, 0d));
    values.add(new QuantityEquality(PhysicalQuantity.VELOCITY, BoatGlobalValues.ID, PhysicalQuantity.VELOCITY, Rudder.ID));
    values.add(new QuantityEquality(PhysicalQuantity.VELOCITY, BoatGlobalValues.ID, PhysicalQuantity.VELOCITY, DaggerboardOrKeel.ID));
    values.add(new QuantityEquality(PhysicalQuantity.VELOCITY, BoatGlobalValues.ID, PhysicalQuantity.VELOCITY, Hull.ID));
    values.add(new QuantityEquality(PhysicalQuantity.LATERAL_FORCE, Rigg.ID, PhysicalQuantity.LIFT, DaggerboardOrKeel.ID)); // assumption: rudder has no force
    values.add(new QuantityEquality(PhysicalQuantity.APPARENT_WIND_SPEED, BoatGlobalValues.ID, PhysicalQuantity.VELOCITY, Rigg.ID));
    values.add(new QuantityEquality(PhysicalQuantity.APPARENT_WIND_SPEED, BoatGlobalValues.ID, PhysicalQuantity.VELOCITY, Crew.ID));
    values.add(new QuantityEquality(PhysicalQuantity.APPARENT_WIND_ANGLE, BoatGlobalValues.ID, PhysicalQuantity.FLOW_DIRECTION, Rigg.ID));
    values.add(new QuantityEquality(PhysicalQuantity.APPARENT_WIND_ANGLE, BoatGlobalValues.ID, PhysicalQuantity.FLOW_DIRECTION, Crew.ID));
    values.add(new QuantityEquality(PhysicalQuantity.LATERAL_FORCE, Rigg.ID, PhysicalQuantity.FORCE, LeverSailDaggerboard.ID));
    values.add(new LeverSailDaggerboardStrategy());
}

  public List<ValueSet> getValueSets()
  {
    return values.getValueSets();
  }

  public ValueSet getValueSetNonNull(String name)
  {
    return values.getValueSetNonNull(name);
  }

  public void addValueSet(ValueSet toAdd)
  {
    values.add(toAdd);
  }

  public boolean removeValueSet(ValueSet toRemove)
  {
    return values.remove(toRemove);
  }

  public void calculate()
  {
    values.calculate(null);
  }
}
