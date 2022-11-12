package com.github.thomasfox.boatcalculator.boat.impl;

import java.io.File;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
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
import com.github.thomasfox.boatcalculator.valueset.impl.TrampolineWing1;
import com.github.thomasfox.boatcalculator.valueset.impl.TrampolineWing2;
import com.github.thomasfox.boatcalculator.valueset.impl.Water;

public class Moth extends Dinghy
{
  protected MainLiftingFoil mainLiftingFoil = new MainLiftingFoil();
  protected RudderLiftingFoil rudderLiftingFoil = new RudderLiftingFoil();
  protected TrampolineWing1 trampolineWing1 = new TrampolineWing1();
  protected TrampolineWing2 trampolineWing2 = new TrampolineWing2();
  protected Takeoff takeoff = new Takeoff();

  public Moth()
  {
    addValueSet(mainLiftingFoil);
    addValueSet(rudderLiftingFoil);
    addValueSet(trampolineWing1);
    addValueSet(trampolineWing2);
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
        PhysicalQuantity.APPARENT_WIND_ANGLE, TrampolineWing1.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.APPARENT_WIND_ANGLE, BoatGlobalValues.ID,
        PhysicalQuantity.APPARENT_WIND_ANGLE, TrampolineWing2.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.HALFWING_SPAN, TrampolineWing1.ID,
        PhysicalQuantity.WING_SPAN_IN_MEDIUM, TrampolineWing1.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.HALFWING_SPAN, TrampolineWing2.ID,
        PhysicalQuantity.WING_SPAN_IN_MEDIUM, TrampolineWing2.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.APPARENT_WIND_SPEED, BoatGlobalValues.ID,
        PhysicalQuantity.VELOCITY, TrampolineWing1.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.APPARENT_WIND_SPEED, BoatGlobalValues.ID,
        PhysicalQuantity.VELOCITY, TrampolineWing2.ID));

    valuesAndCalculationRules.add(new QuantityTimesMinusOne(
        PhysicalQuantity.SIDEWAY_ANGLE, TrampolineWing1.ID,
        PhysicalQuantity.SIDEWAY_ANGLE, TrampolineWing2.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.BACKWAY_ANGLE, TrampolineWing1.ID,
        PhysicalQuantity.BACKWAY_ANGLE, TrampolineWing2.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.WING_CHORD, TrampolineWing1.ID,
        PhysicalQuantity.WING_CHORD, TrampolineWing2.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.HALFWING_SPAN, TrampolineWing1.ID,
        PhysicalQuantity.HALFWING_SPAN, TrampolineWing2.ID));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.CENTER_OF_EFFORT_HEIGHT, TrampolineWing1.ID,
        PhysicalQuantity.CENTER_OF_EFFORT_HEIGHT, TrampolineWing2.ID));

    mainLiftingFoil.setStartValueNoOverwrite(PhysicalQuantity.WING_SPAN, 1d); // 1.13 for current mach 2.41
    mainLiftingFoil.setStartValueNoOverwrite(PhysicalQuantity.WING_CHORD, 0.11d); // 0.073 for current mach 2.41
    mainLiftingFoil.setProfileName("n63412-il");
    mainLiftingFoil.setFixedValueNoOverwrite(
        PhysicalQuantity.MODULUS_OF_ELASTICITY,
        PhysicalQuantity.MODULUS_OF_ELASTICITY.getFixedValue().doubleValue());
    mainLiftingFoil.addHiddenOutput(PhysicalQuantity.WING_SPAN_IN_MEDIUM);

    ((Hydrofoil) daggerboardOrKeel).setStartValueNoOverwrite(PhysicalQuantity.WING_SPAN, 1d);
    ((Hydrofoil) daggerboardOrKeel).setStartValueNoOverwrite(PhysicalQuantity.WING_CHORD, 0.12d);
    ((Hydrofoil) daggerboardOrKeel).setProfileName("e521-il");

    rudderLiftingFoil.setStartValueNoOverwrite(PhysicalQuantity.WING_SPAN, 0.7d);
    rudderLiftingFoil.setStartValueNoOverwrite(PhysicalQuantity.WING_CHORD, 0.08d);
    rudderLiftingFoil.setProfileName("n63412-il");
    rudderLiftingFoil.setFixedValueNoOverwrite(
        PhysicalQuantity.MODULUS_OF_ELASTICITY,
        PhysicalQuantity.MODULUS_OF_ELASTICITY.getFixedValue().doubleValue());
    rudderLiftingFoil.addHiddenOutput(PhysicalQuantity.WING_SPAN_IN_MEDIUM);

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
    // Area and parasitic drag coefficient together roughly get to a parasitic drag of 17 N at a speed of 6.1 m/s
    // which was measured by Beaver. This contains the parasitic drag from both boat and crew.
    crew.setStartValueNoOverwrite(PhysicalQuantity.AREA_IN_MEDIUM, 1);
    crew.setStartValueNoOverwrite(PhysicalQuantity.PARASITIC_DRAG_COEFFICIENT, 0.75);
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

    trampolineWing1.setStartValue(PhysicalQuantity.SIDEWAY_ANGLE, 20);
    trampolineWing1.setStartValue(PhysicalQuantity.BACKWAY_ANGLE, 0);
    trampolineWing1.setStartValue(PhysicalQuantity.WING_CHORD, 2.5);
    trampolineWing1.setStartValue(PhysicalQuantity.HALFWING_SPAN, 1.25);
    trampolineWing1.getQuantityRelations().add(new QuantityRelationLoader().load(new File(SwingGui.HULL_DIRECTORY, "flatPlate_140000_ar05.txt"), "flatPlate@140000,0.5"));
    trampolineWing2.getQuantityRelations().add(new QuantityRelationLoader().load(new File(SwingGui.HULL_DIRECTORY, "flatPlate_140000_ar05.txt"), "flatPlate@140000,0.5"));

    valuesAndCalculationRules.add(new TwoValuesShouldBeEqualModifyThirdStrategy(
        PhysicalQuantity.DRIVING_FORCE, Rigg.ID,
        PhysicalQuantity.TOTAL_DRAG, BoatGlobalValues.ID,
        PhysicalQuantity.VELOCITY, BoatGlobalValues.ID,
        0d, 15d));
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.VELOCITY, BoatGlobalValues.ID,
        PhysicalQuantity.VELOCITY, Hull.ID));
    valuesAndCalculationRules.add(new MothRideoutHeelAngleStrategy());
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
        new PhysicalQuantityInSet(PhysicalQuantity.BRAKING_FORCE, Crew.ID)));
  }

  @Override
  public String toString()
  {
    return "Moth";
  }
}
