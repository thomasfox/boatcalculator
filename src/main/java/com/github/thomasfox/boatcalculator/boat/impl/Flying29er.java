package com.github.thomasfox.boatcalculator.boat.impl;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.calculate.strategy.LiftByAngleOfAttackStrategy;
import com.github.thomasfox.boatcalculator.calculate.strategy.QuantityEquality;
import com.github.thomasfox.boatcalculator.calculate.strategy.QuantitySum;
import com.github.thomasfox.boatcalculator.calculate.strategy.StepComputationStrategy;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.valueset.impl.BoatGlobalValues;
import com.github.thomasfox.boatcalculator.valueset.impl.Crew;
import com.github.thomasfox.boatcalculator.valueset.impl.DaggerboardOrKeel;
import com.github.thomasfox.boatcalculator.valueset.impl.DoubleWing;
import com.github.thomasfox.boatcalculator.valueset.impl.Hull;
import com.github.thomasfox.boatcalculator.valueset.impl.MainLiftingFoil;
import com.github.thomasfox.boatcalculator.valueset.impl.Rudder;

public class Flying29er extends Skiff29er
{
  MainLiftingFoil mainLiftingFoil = new MainLiftingFoil();

  public Flying29er()
  {
    valuesAndCalculationRules.add(mainLiftingFoil);

    valuesAndCalculationRules.add(new QuantityEquality(PhysicalQuantity.VELOCITY, BoatGlobalValues.ID, PhysicalQuantity.VELOCITY, MainLiftingFoil.ID));
    valuesAndCalculationRules.add(new QuantityEquality(PhysicalQuantity.WING_SPAN, DaggerboardOrKeel.ID, PhysicalQuantity.SUBMERGENCE_DEPTH, MainLiftingFoil.ID));

    replaceHullWeightStrategy();
    replaceTotalDragStrategy();
    mainLiftingFoil.setStartValueNoOverwrite(PhysicalQuantity.WING_SPAN, 1.7d);
    mainLiftingFoil.setStartValueNoOverwrite(PhysicalQuantity.WING_CHORD, 0.231d);
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
    StepComputationStrategy weightStrategy = null;
    for (StepComputationStrategy computationStrategy : valuesAndCalculationRules.getStepComputationStrategies())
    {
      if (computationStrategy instanceof QuantitySum
          && ((QuantitySum) computationStrategy).getTarget().equals(new PhysicalQuantityInSet(PhysicalQuantity.LIFT, Hull.ID)))
      {
        weightStrategy = computationStrategy;
        break;
      }
    }
    valuesAndCalculationRules.remove(weightStrategy);
    valuesAndCalculationRules.add(new LiftByAngleOfAttackStrategy(
        new PhysicalQuantityInSet[] {
            new PhysicalQuantityInSet(PhysicalQuantity.WEIGHT, Crew.ID),
            new PhysicalQuantityInSet(PhysicalQuantity.WEIGHT, BoatGlobalValues.ID)
          },
        new PhysicalQuantityInSet[] {
            new PhysicalQuantityInSet(PhysicalQuantity.LIFT, MainLiftingFoil.ID)
          },
        new PhysicalQuantityInSet[] {
            new PhysicalQuantityInSet(PhysicalQuantity.ANGLE_OF_ATTACK, MainLiftingFoil.ID)
          },
        new PhysicalQuantityInSet(PhysicalQuantity.MAX_ANGLE_OF_ATTACK, MainLiftingFoil.ID)
        ));
  }

  protected void replaceTotalDragStrategy()
  {
    StepComputationStrategy totalDragStrategy = null;
    for (StepComputationStrategy computationStrategy : valuesAndCalculationRules.getStepComputationStrategies())
    {
      if (computationStrategy instanceof QuantitySum
          && ((QuantitySum) computationStrategy).getTarget().equals(new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, BoatGlobalValues.ID)))
      {
        totalDragStrategy = computationStrategy;
        break;
      }
    }
    valuesAndCalculationRules.remove(totalDragStrategy);
    valuesAndCalculationRules.add(new QuantitySum(
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
