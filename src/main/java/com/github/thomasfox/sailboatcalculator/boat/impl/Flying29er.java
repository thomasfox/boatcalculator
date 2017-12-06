package com.github.thomasfox.sailboatcalculator.boat.impl;

import com.github.thomasfox.sailboatcalculator.boat.valueset.BoatGlobalValues;
import com.github.thomasfox.sailboatcalculator.boat.valueset.Crew;
import com.github.thomasfox.sailboatcalculator.boat.valueset.DaggerboardOrKeel;
import com.github.thomasfox.sailboatcalculator.boat.valueset.DoubleWing;
import com.github.thomasfox.sailboatcalculator.boat.valueset.Hull;
import com.github.thomasfox.sailboatcalculator.boat.valueset.MainLiftingFoil;
import com.github.thomasfox.sailboatcalculator.boat.valueset.Rudder;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.strategy.ComputationStrategy;
import com.github.thomasfox.sailboatcalculator.calculate.strategy.LiftByAngleOfAttackStrategy;
import com.github.thomasfox.sailboatcalculator.calculate.strategy.QuantityEquality;
import com.github.thomasfox.sailboatcalculator.calculate.strategy.QuantitySum;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityInSet;

public class Flying29er extends Skiff29er
{
  MainLiftingFoil mainLiftingFoil = new MainLiftingFoil();

  public Flying29er()
  {
    values.add(mainLiftingFoil);

    values.add(new QuantityEquality(PhysicalQuantity.VELOCITY, BoatGlobalValues.ID, PhysicalQuantity.VELOCITY, MainLiftingFoil.ID));
    replaceHullWeightStrategy();
    replaceTotalDragStrategy();
    mainLiftingFoil.setStartValueNoOverwrite(PhysicalQuantity.WING_SPAN, 1.7d);
    mainLiftingFoil.setStartValueNoOverwrite(PhysicalQuantity.WING_CHORD, 0.231d);
    mainLiftingFoil.setStartValueNoOverwrite(PhysicalQuantity.SUBMERGENCE_DEPTH, 1.5d);
    mainLiftingFoil.setProfileName("mh32-il");

    removeValueSet(daggerboardOrKeel);
    DaggerboardOrKeel singleDaggerboard = new DaggerboardOrKeel();
    singleDaggerboard.setStartValueNoOverwrite(PhysicalQuantity.WING_SPAN, 1.5d);
    singleDaggerboard.setStartValueNoOverwrite(PhysicalQuantity.WING_CHORD, 0.16d);
    singleDaggerboard.setProfileName("m3-il");
    daggerboardOrKeel = new DoubleWing(singleDaggerboard, singleDaggerboard.getId(), singleDaggerboard.getName());
    addValueSet(daggerboardOrKeel);
  }

  private void replaceHullWeightStrategy()
  {
    ComputationStrategy weightStrategy = null;
    for (ComputationStrategy computationStrategy : values.getComputationStrategies())
    {
      if (computationStrategy instanceof QuantitySum
          && ((QuantitySum) computationStrategy).getTarget().equals(new PhysicalQuantityInSet(PhysicalQuantity.WEIGHT, Hull.ID)))
      {
        weightStrategy = computationStrategy;
        break;
      }
    }
    values.remove(weightStrategy);
    values.add(new LiftByAngleOfAttackStrategy(
        10d,
        new PhysicalQuantityInSet(PhysicalQuantity.WEIGHT, Crew.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.WEIGHT, BoatGlobalValues.ID)));
  }

  protected void replaceTotalDragStrategy()
  {
    ComputationStrategy totalDragStrategy = null;
    for (ComputationStrategy computationStrategy : values.getComputationStrategies())
    {
      if (computationStrategy instanceof QuantitySum
          && ((QuantitySum) computationStrategy).getTarget().equals(new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, BoatGlobalValues.ID)))
      {
        totalDragStrategy = computationStrategy;
        break;
      }
    }
    values.remove(totalDragStrategy);
    values.add(new QuantitySum(
        new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, BoatGlobalValues.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, Hull.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, Rudder.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, DaggerboardOrKeel.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, MainLiftingFoil.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.BRAKING_FORCE, Crew.ID)));
  }
}
