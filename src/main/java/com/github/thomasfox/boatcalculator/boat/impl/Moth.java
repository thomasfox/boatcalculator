package com.github.thomasfox.boatcalculator.boat.impl;

import java.io.File;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.calculate.strategy.LeverSailDaggerboardStrategy;
import com.github.thomasfox.boatcalculator.calculate.strategy.LiftAndAngleOfAttackStrategy;
import com.github.thomasfox.boatcalculator.calculate.strategy.MothRideoutHeelAngleStrategy;
import com.github.thomasfox.boatcalculator.calculate.strategy.QuantityEquality;
import com.github.thomasfox.boatcalculator.calculate.strategy.QuantitySum;
import com.github.thomasfox.boatcalculator.calculate.strategy.QuantityTimesMinusOne;
import com.github.thomasfox.boatcalculator.calculate.strategy.StepComputationStrategy;
import com.github.thomasfox.boatcalculator.calculate.strategy.TwoValuesShouldBeEqualModifyThirdStrategy;
import com.github.thomasfox.boatcalculator.gui.SwingGui;
import com.github.thomasfox.boatcalculator.interpolate.QuantityRelationLoader;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;
import com.github.thomasfox.boatcalculator.valueset.impl.BoatGlobalValues;
import com.github.thomasfox.boatcalculator.valueset.impl.Crew;
import com.github.thomasfox.boatcalculator.valueset.impl.DaggerboardOrKeel;
import com.github.thomasfox.boatcalculator.valueset.impl.Hull;
import com.github.thomasfox.boatcalculator.valueset.impl.Hydrofoil;
import com.github.thomasfox.boatcalculator.valueset.impl.MainLiftingFoil;
import com.github.thomasfox.boatcalculator.valueset.impl.Rigg;
import com.github.thomasfox.boatcalculator.valueset.impl.Rudder;
import com.github.thomasfox.boatcalculator.valueset.impl.RudderLiftingFoil;
import com.github.thomasfox.boatcalculator.valueset.impl.Takeoff;
import com.github.thomasfox.boatcalculator.valueset.impl.TrampolineLeewardWing;
import com.github.thomasfox.boatcalculator.valueset.impl.TrampolineWindwardWing;
import com.github.thomasfox.boatcalculator.valueset.impl.Water;

public class Moth extends Dinghy
{
  protected MainLiftingFoil mainLiftingFoil = new MainLiftingFoil();
  protected RudderLiftingFoil rudderLiftingFoil = new RudderLiftingFoil();
  protected TrampolineLeewardWing trampolineLeewardWing = new TrampolineLeewardWing();
  protected TrampolineWindwardWing trampolineWindwardWing = new TrampolineWindwardWing();
  protected Takeoff takeoff = new Takeoff();

  public Moth()
  {
    addValueSet(mainLiftingFoil);
    addValueSet(rudderLiftingFoil);
    addValueSet(trampolineLeewardWing);
    addValueSet(trampolineWindwardWing);
    addValueSet(takeoff);

    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.VELOCITY, BoatGlobalValues.ID,
        PhysicalQuantity.VELOCITY, MainLiftingFoil.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.WING_SPAN_IN_MEDIUM, DaggerboardOrKeel.ID,
        PhysicalQuantity.SUBMERGENCE_DEPTH, MainLiftingFoil.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.KINEMATIC_VISCOSITY, Water.ID,
        PhysicalQuantity.KINEMATIC_VISCOSITY, MainLiftingFoil.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.WING_SPAN, MainLiftingFoil.ID,
        PhysicalQuantity.WING_SPAN_IN_MEDIUM, MainLiftingFoil.ID));

    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.VELOCITY, BoatGlobalValues.ID,
        PhysicalQuantity.VELOCITY, RudderLiftingFoil.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.WING_SPAN_IN_MEDIUM, Rudder.ID,
        PhysicalQuantity.SUBMERGENCE_DEPTH, RudderLiftingFoil.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.KINEMATIC_VISCOSITY, Water.ID,
        PhysicalQuantity.KINEMATIC_VISCOSITY, RudderLiftingFoil.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.WING_SPAN, RudderLiftingFoil.ID,
        PhysicalQuantity.WING_SPAN_IN_MEDIUM, RudderLiftingFoil.ID));

    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.APPARENT_WIND_ANGLE, BoatGlobalValues.ID,
        PhysicalQuantity.APPARENT_WIND_ANGLE, TrampolineLeewardWing.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.APPARENT_WIND_ANGLE, BoatGlobalValues.ID,
        PhysicalQuantity.APPARENT_WIND_ANGLE, TrampolineWindwardWing.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.HALFWING_SPAN, TrampolineLeewardWing.ID,
        PhysicalQuantity.WING_SPAN_IN_MEDIUM, TrampolineLeewardWing.ID));
    valuesAndCalculationRules.add(new QuantityTimesMinusOne(
        PhysicalQuantity.HALFWING_SPAN, TrampolineWindwardWing.ID,
        PhysicalQuantity.WING_SPAN_IN_MEDIUM, TrampolineWindwardWing.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.APPARENT_WIND_SPEED, BoatGlobalValues.ID,
        PhysicalQuantity.VELOCITY, TrampolineLeewardWing.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.APPARENT_WIND_SPEED, BoatGlobalValues.ID,
        PhysicalQuantity.VELOCITY, TrampolineWindwardWing.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.WINDWARD_HEEL_ANGLE, BoatGlobalValues.ID,
        PhysicalQuantity.WINDWARD_HEEL_ANGLE, TrampolineLeewardWing.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.WINDWARD_HEEL_ANGLE, BoatGlobalValues.ID,
        PhysicalQuantity.WINDWARD_HEEL_ANGLE, TrampolineWindwardWing.ID));

    valuesAndCalculationRules.add(new QuantityTimesMinusOne(
        PhysicalQuantity.SIDEWAY_ANGLE, TrampolineLeewardWing.ID,
        PhysicalQuantity.SIDEWAY_ANGLE, TrampolineWindwardWing.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.BACKWAY_ANGLE, TrampolineLeewardWing.ID,
        PhysicalQuantity.BACKWAY_ANGLE, TrampolineWindwardWing.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.WING_CHORD, TrampolineLeewardWing.ID,
        PhysicalQuantity.WING_CHORD, TrampolineWindwardWing.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.CENTER_OF_EFFORT_HEIGHT, TrampolineLeewardWing.ID,
        PhysicalQuantity.CENTER_OF_EFFORT_HEIGHT, TrampolineWindwardWing.ID));

    mainLiftingFoil.setStartValueNoOverwrite(PhysicalQuantity.WING_SPAN, 1d); // 1.13 for current mach 2.41
    mainLiftingFoil.setStartValueNoOverwrite(PhysicalQuantity.WING_CHORD, 0.11d); // 0.073 for current mach 2.41
    mainLiftingFoil.setProfileName("n63412-il");
    mainLiftingFoil.setFixedValueNoOverwrite(
        PhysicalQuantity.MODULUS_OF_ELASTICITY,
        PhysicalQuantity.MODULUS_OF_ELASTICITY.getFixedValue());
    mainLiftingFoil.addHiddenOutput(PhysicalQuantity.WING_SPAN_IN_MEDIUM);

    ((Hydrofoil) daggerboardOrKeel).setStartValueNoOverwrite(PhysicalQuantity.WING_SPAN, 1d);
    ((Hydrofoil) daggerboardOrKeel).setStartValueNoOverwrite(PhysicalQuantity.WING_CHORD, 0.12d);
    (daggerboardOrKeel).setProfileName("e521-il");

    rudderLiftingFoil.setStartValueNoOverwrite(PhysicalQuantity.WING_SPAN, 0.7d);
    rudderLiftingFoil.setStartValueNoOverwrite(PhysicalQuantity.WING_CHORD, 0.08d);
    rudderLiftingFoil.setProfileName("n63412-il");
    rudderLiftingFoil.setFixedValueNoOverwrite(
        PhysicalQuantity.MODULUS_OF_ELASTICITY,
        PhysicalQuantity.MODULUS_OF_ELASTICITY.getFixedValue());
    rudderLiftingFoil.addHiddenOutput(PhysicalQuantity.WING_SPAN_IN_MEDIUM);
    rudderLiftingFoil.removeToInput(PhysicalQuantity.MAX_ANGLE_OF_ATTACK);
    rudderLiftingFoil.addToInput(PhysicalQuantity.LIFT);
    rudderLiftingFoil.addToInput(PhysicalQuantity.AREA_IN_MEDIUM);
    rudderLiftingFoil.addToInput(PhysicalQuantity.BENDING);

    rudder.setStartValueNoOverwrite(PhysicalQuantity.WING_SPAN, 1d);
    rudder.setStartValueNoOverwrite(PhysicalQuantity.WING_CHORD, 0.12d);
    rudder.setStartValueNoOverwrite(PhysicalQuantity.LIFT, 0d); // assuming negligible force on the rudder for no heel
    rudder.setProfileName("e521-il");

    rigg.setStartValueNoOverwrite(PhysicalQuantity.WING_SPAN, 5.3d); // from north-sails-international-moth-speed-guide
    rigg.setStartValueNoOverwrite(PhysicalQuantity.AREA_IN_MEDIUM, 8d);
    rigg.setStartValueNoOverwrite(PhysicalQuantity.CENTER_OF_EFFORT_HEIGHT, 3.08); // from picture with Hyde A2m sail

    crew.setStartValueNoOverwrite(PhysicalQuantity.MASS, 80d); // single person
    crew.setStartValueNoOverwrite(PhysicalQuantity.MAX_LEVER_WEIGHT, 1.25);
    crew.setStartValueNoOverwrite(PhysicalQuantity.CENTER_OF_EFFORT_HEIGHT, 1.25);
    // Beaver measured a parasitic drag of 17N(0°)/14N(15°)/13N(30°) by both boat and crew at 6.1 m/s and a relative wind angle of 24 degrees.
    // We put hull and crew drag in this number, but not the drag from the trampoline wings.
    // As (constant boat+crew drag plus trampoline drag) vs heel angle differs substantially from beaver's measured value,
    // gauging the boat+crew drag is difficult. Assumed here is that at a heel angle of 20°, the total force is 14N
    crew.setStartValueNoOverwrite(PhysicalQuantity.AREA_IN_MEDIUM, 1);
    crew.setStartValueNoOverwrite(PhysicalQuantity.PARASITIC_DRAG_COEFFICIENT, 0.37);
    crew.addToInput(PhysicalQuantity.MAX_LEVER_WEIGHT);
    crew.addToInput(PhysicalQuantity.CENTER_OF_EFFORT_HEIGHT);

    boatGlobalValues.setStartValueNoOverwrite(PhysicalQuantity.MASS, 40d); // waszp.
    boatGlobalValues.setStartValueNoOverwrite(PhysicalQuantity.RIDING_HEIGHT, 0.3);
    boatGlobalValues.setStartValueNoOverwrite(PhysicalQuantity.MAX_WINDWARD_HEEL_ANGLE, 20);
    boatGlobalValues.setStartValueNoOverwrite(PhysicalQuantity.CENTER_OF_EFFORT_HEIGHT, 1.25); // height of the center of mass of the complete rigged boat above bottom of the boat. TODO check should be approximately at boom level
    boatGlobalValues.addToInput(PhysicalQuantity.RIDING_HEIGHT);
    boatGlobalValues.addToInput(PhysicalQuantity.MAX_WINDWARD_HEEL_ANGLE);
    boatGlobalValues.addToInput(PhysicalQuantity.CENTER_OF_EFFORT_HEIGHT);
    boatGlobalValues.removeStartValue(PhysicalQuantity.WINDWARD_HEEL_ANGLE);

    hull.getQuantityRelations().add(new QuantityRelationLoader().load(new File(SwingGui.HULL_DIRECTORY, "0kg.txt"), "Hull@0kg"));
    hull.getQuantityRelations().add(new QuantityRelationLoader().load(new File(SwingGui.HULL_DIRECTORY, "moth_27kg.txt"), "Moth@27kg"));
    hull.getQuantityRelations().add(new QuantityRelationLoader().load(new File(SwingGui.HULL_DIRECTORY, "moth_54kg.txt"), "Moth@54kg"));
    hull.getQuantityRelations().add(new QuantityRelationLoader().load(new File(SwingGui.HULL_DIRECTORY, "moth_108kg.txt"), "Moth@108kg"));
    hull.getQuantityRelations().add(new QuantityRelationLoader().load(new File(SwingGui.HULL_DIRECTORY, "moth_158kg_interpolated.txt"), "Moth@158kgInterpolated"));

    trampolineLeewardWing.setStartValue(PhysicalQuantity.SIDEWAY_ANGLE, 20);
    trampolineLeewardWing.setStartValue(PhysicalQuantity.BACKWAY_ANGLE, 0);
    trampolineLeewardWing.setStartValue(PhysicalQuantity.WING_CHORD, 2);
    trampolineLeewardWing.setStartValue(PhysicalQuantity.HALFWING_SPAN, 1.25);
    trampolineLeewardWing.setStartValue(PhysicalQuantity.CENTER_OF_EFFORT_HEIGHT, 1.125);
    trampolineLeewardWing.addHiddenOutput(PhysicalQuantity.WINDWARD_HEEL_ANGLE);
    trampolineLeewardWing.addHiddenOutput(PhysicalQuantity.VELOCITY);
    trampolineLeewardWing.addHiddenOutput(PhysicalQuantity.APPARENT_WIND_ANGLE);
    trampolineLeewardWing.addHiddenOutput(PhysicalQuantity.LIFT_COEFFICIENT);
    trampolineLeewardWing.addHiddenOutput(PhysicalQuantity.INDUCED_DRAG_COEFFICIENT);
    trampolineLeewardWing.addHiddenOutput(PhysicalQuantity.INDUCED_DRAG);
    trampolineWindwardWing.addHiddenOutput(PhysicalQuantity.WINDWARD_HEEL_ANGLE);
    trampolineWindwardWing.addHiddenOutput(PhysicalQuantity.VELOCITY);
    trampolineWindwardWing.addHiddenOutput(PhysicalQuantity.APPARENT_WIND_ANGLE);
    trampolineWindwardWing.addHiddenOutput(PhysicalQuantity.CENTER_OF_EFFORT_HEIGHT);
    trampolineWindwardWing.addHiddenOutput(PhysicalQuantity.LIFT_COEFFICIENT);
    trampolineWindwardWing.addHiddenOutput(PhysicalQuantity.INDUCED_DRAG_COEFFICIENT);
    trampolineWindwardWing.addHiddenOutput(PhysicalQuantity.INDUCED_DRAG);
    trampolineWindwardWing.setStartValue(PhysicalQuantity.HALFWING_SPAN, -1.125);

    valuesAndCalculationRules.add(new TwoValuesShouldBeEqualModifyThirdStrategy(
        PhysicalQuantity.DRIVING_FORCE, Rigg.ID,
        PhysicalQuantity.TOTAL_DRAG, BoatGlobalValues.ID,
        PhysicalQuantity.VELOCITY, BoatGlobalValues.ID,
        0d, 15d));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.VELOCITY, BoatGlobalValues.ID,
        PhysicalQuantity.VELOCITY, Hull.ID));
    valuesAndCalculationRules.add(new MothRideoutHeelAngleStrategy());
    valuesAndCalculationRules.add(new LeverSailDaggerboardStrategy(trampolineLeewardWing, trampolineLeewardWing));
    valuesAndCalculationRules.add(new LeverSailDaggerboardStrategy(trampolineWindwardWing, trampolineWindwardWing));

    replaceHullWeightStrategy();
    replaceTotalDragStrategy();
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
    valuesAndCalculationRules.add(new LiftAndAngleOfAttackStrategy(
        new PhysicalQuantityInSet[] {
            new PhysicalQuantityInSet(PhysicalQuantity.WEIGHT, Crew.ID),
            new PhysicalQuantityInSet(PhysicalQuantity.WEIGHT, BoatGlobalValues.ID)
          },
        new ValueSet[] {mainLiftingFoil, rudderLiftingFoil},
        new ValueSet[] {rigg},
        new ValueSet[] {daggerboardOrKeel, rudder},
        new PhysicalQuantityInSet(PhysicalQuantity.MAX_ANGLE_OF_ATTACK, MainLiftingFoil.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.WINDWARD_HEEL_ANGLE, BoatGlobalValues.ID)
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
        new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, RudderLiftingFoil.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.BRAKING_FORCE, Crew.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.BRAKING_FORCE, TrampolineLeewardWing.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.BRAKING_FORCE, TrampolineWindwardWing.ID)));
  }

  @Override
  public String toString()
  {
    return "Moth";
  }
}
