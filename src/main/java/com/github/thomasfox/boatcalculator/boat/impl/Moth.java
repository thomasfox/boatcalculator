package com.github.thomasfox.boatcalculator.boat.impl;

import java.io.File;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.calculate.strategy.ComputationStrategy;
import com.github.thomasfox.boatcalculator.calculate.strategy.IncreaseQuantityTillOtherReachesUpperLimitStrategy;
import com.github.thomasfox.boatcalculator.calculate.strategy.LiftByAngleOfAttackStrategy;
import com.github.thomasfox.boatcalculator.calculate.strategy.QuantityEquality;
import com.github.thomasfox.boatcalculator.calculate.strategy.QuantitySum;
import com.github.thomasfox.boatcalculator.calculate.strategy.TwoValuesShouldBeEqualModifyThirdStrategy;
import com.github.thomasfox.boatcalculator.gui.SwingGui;
import com.github.thomasfox.boatcalculator.interpolate.QuantityRelationLoader;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.valueset.impl.BoatGlobalValues;
import com.github.thomasfox.boatcalculator.valueset.impl.Crew;
import com.github.thomasfox.boatcalculator.valueset.impl.DaggerboardOrKeel;
import com.github.thomasfox.boatcalculator.valueset.impl.Hull;
import com.github.thomasfox.boatcalculator.valueset.impl.MainLiftingFoil;
import com.github.thomasfox.boatcalculator.valueset.impl.Rigg;
import com.github.thomasfox.boatcalculator.valueset.impl.Rudder;
import com.github.thomasfox.boatcalculator.valueset.impl.RudderLiftingFoil;
import com.github.thomasfox.boatcalculator.valueset.impl.Takeoff;

public class Moth extends Dinghy
{
  protected MainLiftingFoil mainLiftingFoil = new MainLiftingFoil();
  protected RudderLiftingFoil rudderLiftingFoil = new RudderLiftingFoil();
  protected Takeoff takeoff = new Takeoff();

  public Moth()
  {
    addValueSet(mainLiftingFoil);
    addValueSet(rudderLiftingFoil);
    addValueSet(takeoff);

    valuesAndCalculationRules.add(new QuantityEquality(PhysicalQuantity.VELOCITY, BoatGlobalValues.ID, PhysicalQuantity.VELOCITY, MainLiftingFoil.ID));
    valuesAndCalculationRules.add(new QuantityEquality(PhysicalQuantity.WING_SPAN, DaggerboardOrKeel.ID, PhysicalQuantity.SUBMERGENCE_DEPTH, MainLiftingFoil.ID));

    valuesAndCalculationRules.add(new QuantityEquality(PhysicalQuantity.VELOCITY, BoatGlobalValues.ID, PhysicalQuantity.VELOCITY, RudderLiftingFoil.ID));
    valuesAndCalculationRules.add(new QuantityEquality(PhysicalQuantity.WING_SPAN, Rudder.ID, PhysicalQuantity.SUBMERGENCE_DEPTH, RudderLiftingFoil.ID));

    mainLiftingFoil.setStartValueNoOverwrite(PhysicalQuantity.WING_SPAN, 1d); // 1.13 for current mach 2.41
    mainLiftingFoil.setStartValueNoOverwrite(PhysicalQuantity.WING_CHORD, 0.11d); // 0.073 for current mach 2.41
    mainLiftingFoil.setProfileName("n63412-il");
    mainLiftingFoil.setFixedValueNoOverwrite(
        PhysicalQuantity.MODULUS_OF_ELASTICITY,
        PhysicalQuantity.MODULUS_OF_ELASTICITY.getFixedValue().doubleValue());

    ((DaggerboardOrKeel) daggerboardOrKeel).setStartValueNoOverwrite(PhysicalQuantity.WING_SPAN, 1d);
    ((DaggerboardOrKeel) daggerboardOrKeel).setStartValueNoOverwrite(PhysicalQuantity.WING_CHORD, 0.12d);
    ((DaggerboardOrKeel) daggerboardOrKeel).setProfileName("e521-il");

    rudderLiftingFoil.setStartValueNoOverwrite(PhysicalQuantity.WING_SPAN, 0.7d);
    rudderLiftingFoil.setStartValueNoOverwrite(PhysicalQuantity.WING_CHORD, 0.08d);
    rudderLiftingFoil.setProfileName("n63412-il");
    rudderLiftingFoil.setFixedValueNoOverwrite(
        PhysicalQuantity.MODULUS_OF_ELASTICITY,
        PhysicalQuantity.MODULUS_OF_ELASTICITY.getFixedValue().doubleValue());

    rudder.setStartValueNoOverwrite(PhysicalQuantity.WING_SPAN, 1d);
    rudder.setStartValueNoOverwrite(PhysicalQuantity.WING_CHORD, 0.12d);
    rudder.setStartValueNoOverwrite(PhysicalQuantity.LIFT, 0d); // assuming negligible force on the rudder for no heel
    rudder.setProfileName("e521-il");

    rigg.setStartValueNoOverwrite(PhysicalQuantity.WING_SPAN, 5.3d); // from north-sails-international-moth-speed-guide
    rigg.setStartValueNoOverwrite(PhysicalQuantity.AREA, 8d);
    rigg.setStartValueNoOverwrite(PhysicalQuantity.RIGG_CENTER_OF_EFFORT_HEIGHT, 2); // rough estimate

    crew.setStartValueNoOverwrite(PhysicalQuantity.MASS, 80d); // single person
    crew.setStartValueNoOverwrite(PhysicalQuantity.AREA, 0.58); // 1.8m * 0.3m
    crew.setStartValueNoOverwrite(PhysicalQuantity.PARASITIC_DRAG_COEFFICIENT, 0.5); // rough estimate

    boatGlobalValues.setStartValueNoOverwrite(PhysicalQuantity.MASS, 40d); // waszp

    hull.getQuantityRelations().add(new QuantityRelationLoader().load(new File(SwingGui.HULL_DIRECTORY, "0kg.txt"), "Hull@0kg"));
    hull.getQuantityRelations().add(new QuantityRelationLoader().load(new File(SwingGui.HULL_DIRECTORY, "moth_27kg.txt"), "Moth@27kg"));
    hull.getQuantityRelations().add(new QuantityRelationLoader().load(new File(SwingGui.HULL_DIRECTORY, "moth_54kg.txt"), "Moth@54kg"));
    hull.getQuantityRelations().add(new QuantityRelationLoader().load(new File(SwingGui.HULL_DIRECTORY, "moth_108kg.txt"), "Moth@108kg"));
    hull.getQuantityRelations().add(new QuantityRelationLoader().load(new File(SwingGui.HULL_DIRECTORY, "moth_158kg_interpolated.txt"), "Moth@158kgInterpolated"));

    hull.setStartValueNoOverwrite(PhysicalQuantity.AREA, 0.375); // 1,5m * 25cm)
    hull.setStartValueNoOverwrite(PhysicalQuantity.TOTAL_DRAG_COEFFICIENT, 0.5); // rough estimate

    valuesAndCalculationRules.add(new TwoValuesShouldBeEqualModifyThirdStrategy(
        PhysicalQuantity.DRIVING_FORCE, Rigg.ID,
        PhysicalQuantity.TOTAL_DRAG, BoatGlobalValues.ID,
        PhysicalQuantity.VELOCITY, BoatGlobalValues.ID,
        0d, 15d));
    valuesAndCalculationRules.add(new IncreaseQuantityTillOtherReachesUpperLimitStrategy(
        PhysicalQuantity.LEVER_WEIGHT, Crew.ID, 1.25,
        PhysicalQuantity.LIFT_COEFFICIENT_3D, Rigg.ID, 1.55)); // caMax=1.55: rough estimate from windsurf sail paper
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.VELOCITY, BoatGlobalValues.ID,
        PhysicalQuantity.VELOCITY, Hull.ID));
    replaceHullWeightStrategy();
    replaceTotalDragStrategy();
  }

  private void replaceHullWeightStrategy()
  {
    ComputationStrategy weightStrategy = null;
    for (ComputationStrategy computationStrategy : valuesAndCalculationRules.getComputationStrategies())
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
            new PhysicalQuantityInSet(PhysicalQuantity.LIFT, MainLiftingFoil.ID),
            new PhysicalQuantityInSet(PhysicalQuantity.LIFT, RudderLiftingFoil.ID)
          },
        new PhysicalQuantityInSet[] {
            new PhysicalQuantityInSet(PhysicalQuantity.ANGLE_OF_ATTACK, MainLiftingFoil.ID),
            new PhysicalQuantityInSet(PhysicalQuantity.ANGLE_OF_ATTACK, RudderLiftingFoil.ID)
          },
        new PhysicalQuantityInSet(PhysicalQuantity.MAX_ANGLE_OF_ATTACK, MainLiftingFoil.ID)
        ));
  }

  protected void replaceTotalDragStrategy()
  {
    ComputationStrategy totalDragStrategy = null;
    for (ComputationStrategy computationStrategy : valuesAndCalculationRules.getComputationStrategies())
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
