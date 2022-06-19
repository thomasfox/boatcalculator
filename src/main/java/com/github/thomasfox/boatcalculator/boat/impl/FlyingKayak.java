package com.github.thomasfox.boatcalculator.boat.impl;

import java.io.File;

import com.github.thomasfox.boatcalculator.boat.Boat;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.calculate.strategy.LiftByAngleOfAttackStrategy;
import com.github.thomasfox.boatcalculator.calculate.strategy.QuantityEquality;
import com.github.thomasfox.boatcalculator.calculate.strategy.QuantitySum;
import com.github.thomasfox.boatcalculator.calculate.strategy.StepComputationStrategy;
import com.github.thomasfox.boatcalculator.gui.SwingGui;
import com.github.thomasfox.boatcalculator.interpolate.QuantityRelationLoader;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.valueset.SimpleValueSet;
import com.github.thomasfox.boatcalculator.valueset.impl.BoatGlobalValues;
import com.github.thomasfox.boatcalculator.valueset.impl.Crew;
import com.github.thomasfox.boatcalculator.valueset.impl.DaggerboardOrKeel;
import com.github.thomasfox.boatcalculator.valueset.impl.DoubleWing;
import com.github.thomasfox.boatcalculator.valueset.impl.Hull;
import com.github.thomasfox.boatcalculator.valueset.impl.MainLiftingFoil;
import com.github.thomasfox.boatcalculator.valueset.impl.Rudder;

public class FlyingKayak extends Boat
{
  MainLiftingFoil mainLiftingFoil = new MainLiftingFoil();

  protected SimpleValueSet crew = new Crew();

  public FlyingKayak()
  {
    valuesAndCalculationRules.add(mainLiftingFoil);
    valuesAndCalculationRules.add(new QuantityEquality(PhysicalQuantity.VELOCITY, BoatGlobalValues.ID, PhysicalQuantity.VELOCITY, MainLiftingFoil.ID));
    valuesAndCalculationRules.add(new QuantityEquality(PhysicalQuantity.WING_SPAN, DaggerboardOrKeel.ID, PhysicalQuantity.SUBMERGENCE_DEPTH, MainLiftingFoil.ID));

    addValueSet(crew);
    crew.addHiddenOutput(PhysicalQuantity.VELOCITY);
    crew.setStartValue(PhysicalQuantity.MASS, 80d);

    boatGlobalValues.removeToInput(PhysicalQuantity.WIND_SPEED);
    boatGlobalValues.removeToInput(PhysicalQuantity.DRIFT_ANGLE);
    boatGlobalValues.removeToInput(PhysicalQuantity.POINTING_ANGLE);
    boatGlobalValues.setStartValue(PhysicalQuantity.MASS, 20d);
    valuesAndCalculationRules.add(new QuantitySum(
        new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, BoatGlobalValues.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, Hull.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, Rudder.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, DaggerboardOrKeel.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, MainLiftingFoil.ID)));

    rudder.setFixedValueNoOverwrite(PhysicalQuantity.ANGLE_OF_ATTACK, 0d);
    rudder.setProfileName("naca0010-il");
    rudder.setStartValue(PhysicalQuantity.WING_SPAN, 1.0);
    rudder.setStartValue(PhysicalQuantity.WING_CHORD, 0.1);
    rudder.removeToInput(PhysicalQuantity.LIFT);
    rudder.addHiddenOutput(PhysicalQuantity.INDUCED_DRAG);
    rudder.addHiddenOutput(PhysicalQuantity.INDUCED_DRAG_COEFFICIENT);
    rudder.addHiddenOutput(PhysicalQuantity.LIFT);
    rudder.addHiddenOutput(PhysicalQuantity.LIFT_COEFFICIENT);

    replaceHullWeightStrategy();
    mainLiftingFoil.setStartValueNoOverwrite(PhysicalQuantity.WING_SPAN, 2.5);
    mainLiftingFoil.setStartValueNoOverwrite(PhysicalQuantity.WING_CHORD, 0.15d);
    mainLiftingFoil.setProfileName("mh32-il");
    mainLiftingFoil.setStartValue(PhysicalQuantity.MAX_ANGLE_OF_ATTACK, 7d);


    removeValueSet(daggerboardOrKeel);
    DaggerboardOrKeel singleDaggerboard = new DaggerboardOrKeel();
    singleDaggerboard.setStartValueNoOverwrite(PhysicalQuantity.WING_SPAN, 1.0d);
    singleDaggerboard.setStartValueNoOverwrite(PhysicalQuantity.WING_CHORD, 0.16d);
    singleDaggerboard.setProfileName("m3-il");
    singleDaggerboard.setFixedValueNoOverwrite(PhysicalQuantity.ANGLE_OF_ATTACK, 0d);
    daggerboardOrKeel = new DoubleWing(singleDaggerboard, singleDaggerboard.getId(), singleDaggerboard.getDisplayName());
    addValueSet(daggerboardOrKeel);

    hull.getQuantityRelations().add(new QuantityRelationLoader().load(new File(SwingGui.HULL_DIRECTORY, "0kg.txt"), "Hull@0kg"));
    hull.getQuantityRelations().add(new QuantityRelationLoader().load(new File(SwingGui.HULL_DIRECTORY, "fastKayak_113kg.txt"), "Kayak Hull@113kg"));
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

  @Override
  public String toString()
  {
    return "Flying Kayak";
  }
}
