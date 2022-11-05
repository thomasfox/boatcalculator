package com.github.thomasfox.boatcalculator.boat.impl;

import java.io.File;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.calculate.strategy.IncreaseQuantityTillOtherReachesUpperLimitStrategy;
import com.github.thomasfox.boatcalculator.calculate.strategy.QuantityEquality;
import com.github.thomasfox.boatcalculator.calculate.strategy.TwoValuesShouldBeEqualModifyThirdStrategy;
import com.github.thomasfox.boatcalculator.gui.SwingGui;
import com.github.thomasfox.boatcalculator.interpolate.QuantityRelationLoader;
import com.github.thomasfox.boatcalculator.valueset.impl.BoatGlobalValues;
import com.github.thomasfox.boatcalculator.valueset.impl.Crew;
import com.github.thomasfox.boatcalculator.valueset.impl.DaggerboardOrKeel;
import com.github.thomasfox.boatcalculator.valueset.impl.Hull;
import com.github.thomasfox.boatcalculator.valueset.impl.Rigg;

public class Skiff29er extends Dinghy
{
  public Skiff29er()
  {
    rudder.setStartValueNoOverwrite(PhysicalQuantity.WING_SPAN, 0.77d); // full blade span 92 cm, estimated box size 15 cm
    rudder.setStartValueNoOverwrite(PhysicalQuantity.WING_CHORD, 0.223d);
    rudder.setStartValueNoOverwrite(PhysicalQuantity.LIFT, 0d); // assuming negligible force on the rudder for no heel
    rudder.setProfileName("naca0010-il");
    ((DaggerboardOrKeel) daggerboardOrKeel).setStartValueNoOverwrite(PhysicalQuantity.WING_SPAN, 0.985d); // full blade span 118.5 cm, estimated box size 20 cm
    ((DaggerboardOrKeel) daggerboardOrKeel).setStartValueNoOverwrite(PhysicalQuantity.WING_CHORD, 0.32d);
    ((DaggerboardOrKeel) daggerboardOrKeel).setProfileName("naca0010-il");
    rigg.setStartValueNoOverwrite(PhysicalQuantity.WING_SPAN_IN_MEDIUM, 6d); // rough estimate
    rigg.setStartValueNoOverwrite(PhysicalQuantity.AREA_IN_MEDIUM, 12d); // no gennaker taken into account
    crew.setStartValueNoOverwrite(PhysicalQuantity.MASS, 80d); // single person
    crew.setStartValueNoOverwrite(PhysicalQuantity.AREA_IN_MEDIUM, 0.58); // 1.8m * 0.3m
    crew.setStartValueNoOverwrite(PhysicalQuantity.PARASITIC_DRAG_COEFFICIENT, 0.5); // rough estimate
    rigg.setStartValueNoOverwrite(PhysicalQuantity.CENTER_OF_EFFORT_HEIGHT, 3); // rough estimate based on COG of Sail plan
    boatGlobalValues.setStartValueNoOverwrite(PhysicalQuantity.MASS, 100d); // old boat, nominal weight is 90 kg
    boatGlobalValues.setFixedValueNoOverwrite(PhysicalQuantity.RIDING_HEIGHT, 0d);
    hull.getQuantityRelations().add(new QuantityRelationLoader().load(new File(SwingGui.HULL_DIRECTORY, "0kg.txt"), "Hull@0kg"));
    hull.getQuantityRelations().add(new QuantityRelationLoader().load(new File(SwingGui.HULL_DIRECTORY, "29er_204kg.txt"), "29er Hull@204kg"));
    hull.setStartValueNoOverwrite(PhysicalQuantity.AREA_IN_MEDIUM, 0.375); // 1,5m * 25cm)
    hull.setStartValueNoOverwrite(PhysicalQuantity.TOTAL_DRAG_COEFFICIENT, 0.5); // rough estimate

    valuesAndCalculationRules.add(new TwoValuesShouldBeEqualModifyThirdStrategy(
        PhysicalQuantity.DRIVING_FORCE, Rigg.ID,
        PhysicalQuantity.TOTAL_DRAG, BoatGlobalValues.ID,
        PhysicalQuantity.VELOCITY, BoatGlobalValues.ID,
        0d, 10d));
    valuesAndCalculationRules.add(new IncreaseQuantityTillOtherReachesUpperLimitStrategy(
        PhysicalQuantity.LEVER_WEIGHT, Crew.ID, 1.8,
        PhysicalQuantity.LIFT_COEFFICIENT_3D, Rigg.ID, 1.5)); // 1.8m: single person, CoG 91.5 cm from rim in trapeze; caMax=1.5: from flying optimist
    valuesAndCalculationRules.add(new QuantityEquality(
        PhysicalQuantity.VELOCITY, BoatGlobalValues.ID,
        PhysicalQuantity.VELOCITY, Hull.ID));
  }

  @Override
  public String toString()
  {
    return "29er";
  }
}
