package com.github.thomasfox.boatcalculator.boat;

import java.util.List;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.calculate.strategy.QuantityEquality;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;
import com.github.thomasfox.boatcalculator.valueset.ValuesAndCalculationRules;
import com.github.thomasfox.boatcalculator.valueset.impl.BoatGlobalValues;
import com.github.thomasfox.boatcalculator.valueset.impl.DaggerboardOrKeel;
import com.github.thomasfox.boatcalculator.valueset.impl.Hull;
import com.github.thomasfox.boatcalculator.valueset.impl.LeverSailDaggerboard;
import com.github.thomasfox.boatcalculator.valueset.impl.Rudder;

public abstract class Boat
{
  protected BoatGlobalValues boatGlobalValues = new BoatGlobalValues();

  protected LeverSailDaggerboard leverSailDaggerboard = new LeverSailDaggerboard();

  protected Hull hull = new Hull();

  protected ValueSet daggerboardOrKeel = new DaggerboardOrKeel();

  protected Rudder rudder = new Rudder();

  protected ValuesAndCalculationRules valuesAndCalculationRules = new ValuesAndCalculationRules();

  /**
   * Holds all values and all calculation rules of a boat.
   * The values are held in ValueSets, each of which describes a boat part
   * or an aspect of the boat.
   * The global values applying to the whole boat are held in the value set
   * boatGlobalValues.
   *
   * This class is basically a wrapper around an AllValues object,
   * to have named parts which can be easily accessed.
   */
  public Boat()
  {
    addValueSet(boatGlobalValues);
    addValueSet(hull);
    addValueSet(daggerboardOrKeel);
    addValueSet(rudder);

    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.VELOCITY, BoatGlobalValues.ID,
        PhysicalQuantity.VELOCITY, Rudder.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.VELOCITY, BoatGlobalValues.ID,
        PhysicalQuantity.VELOCITY, DaggerboardOrKeel.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.VELOCITY, BoatGlobalValues.ID,
        PhysicalQuantity.VELOCITY, Hull.ID));
}

  public List<ValueSet> getValueSets()
  {
    return valuesAndCalculationRules.getValueSets();
  }

  public ValueSet getValueSetNonNull(String name)
  {
    return valuesAndCalculationRules.getValueSetNonNull(name);
  }

  public void addValueSet(ValueSet toAdd)
  {
    valuesAndCalculationRules.add(toAdd);
  }

  public boolean removeValueSet(ValueSet toRemove)
  {
    return valuesAndCalculationRules.remove(toRemove);
  }

  public boolean calculate()
  {
    // new CalculateTakeoffVelocitiesStrategy().setValue(values);
    return valuesAndCalculationRules.calculate(null, 500);
  }
}
