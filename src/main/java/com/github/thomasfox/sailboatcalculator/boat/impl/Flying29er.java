package com.github.thomasfox.sailboatcalculator.boat.impl;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.strategy.ComputationStrategy;
import com.github.thomasfox.sailboatcalculator.calculate.strategy.LiftByAngleOfAttackStrategy;
import com.github.thomasfox.sailboatcalculator.calculate.strategy.QuantityEquality;
import com.github.thomasfox.sailboatcalculator.calculate.strategy.QuantitySum;
import com.github.thomasfox.sailboatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.sailboatcalculator.valueset.impl.BoatGlobalValues;
import com.github.thomasfox.sailboatcalculator.valueset.impl.Crew;
import com.github.thomasfox.sailboatcalculator.valueset.impl.DaggerboardOrKeel;
import com.github.thomasfox.sailboatcalculator.valueset.impl.DoubleWing;
import com.github.thomasfox.sailboatcalculator.valueset.impl.Hull;
import com.github.thomasfox.sailboatcalculator.valueset.impl.MainLiftingFoil;
import com.github.thomasfox.sailboatcalculator.valueset.impl.Rudder;

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
    daggerboardOrKeel = new DoubleWing(singleDaggerboard, singleDaggerboard.getId(), singleDaggerboard.getDisplayName());
    addValueSet(daggerboardOrKeel);
  }

  private void replaceHullWeightStrategy()
  {
    ComputationStrategy weightStrategy = null;
    for (ComputationStrategy computationStrategy : values.getComputationStrategies())
    {
      if (computationStrategy instanceof QuantitySum
          && ((QuantitySum) computationStrategy).getTarget().equals(new PhysicalQuantityInSet(PhysicalQuantity.MASS, Hull.ID)))
      {
        weightStrategy = computationStrategy;
        break;
      }
    }
    values.remove(weightStrategy);
    values.add(new LiftByAngleOfAttackStrategy(
        new PhysicalQuantityInSet(PhysicalQuantity.MASS, Crew.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.MASS, BoatGlobalValues.ID)));
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

  @Override
  public String toString()
  {
    return "Flying 29er";
  }

}
