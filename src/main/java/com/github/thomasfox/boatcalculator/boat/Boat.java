package com.github.thomasfox.boatcalculator.boat;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.calculate.strategy.QuantityEquality;
import com.github.thomasfox.boatcalculator.calculate.strategy.ReduceSpanInMediumWhenFoilingStrategy;
import com.github.thomasfox.boatcalculator.interpolate.QuantityRelationLoader;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;
import com.github.thomasfox.boatcalculator.valueset.ValuesAndCalculationRules;
import com.github.thomasfox.boatcalculator.valueset.impl.BoatGlobalValues;
import com.github.thomasfox.boatcalculator.valueset.impl.DaggerboardOrKeel;
import com.github.thomasfox.boatcalculator.valueset.impl.Hull;
import com.github.thomasfox.boatcalculator.valueset.impl.Rudder;
import com.github.thomasfox.boatcalculator.valueset.impl.Water;

public abstract class Boat
{
  protected BoatGlobalValues boatGlobalValues = new BoatGlobalValues();

  protected Water water = new Water();

  protected Hull hull = new Hull();

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
    addValueSet(rudder);
    addValueSet(water);

    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.VELOCITY, BoatGlobalValues.ID,
        PhysicalQuantity.VELOCITY, Rudder.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.VELOCITY, BoatGlobalValues.ID,
        PhysicalQuantity.VELOCITY, DaggerboardOrKeel.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.VELOCITY, BoatGlobalValues.ID,
        PhysicalQuantity.VELOCITY, Hull.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.KINEMATIC_VISCOSITY, Water.ID,
        PhysicalQuantity.KINEMATIC_VISCOSITY, Rudder.ID));
    valuesAndCalculationRules.add(new ReduceSpanInMediumWhenFoilingStrategy(rudder));

    Reader kinematicViscosityWaterRader = new InputStreamReader(getClass().getResourceAsStream("/kinematicViscosity_water.txt"));
    water.getQuantityRelations().add(new QuantityRelationLoader().load(kinematicViscosityWaterRader, "kinematicViscosityWater"));
    water.setStartValueNoOverwrite(PhysicalQuantity.TEMPERATURE, 15);
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
