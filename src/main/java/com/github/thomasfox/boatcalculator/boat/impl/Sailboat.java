package com.github.thomasfox.boatcalculator.boat.impl;

import com.github.thomasfox.boatcalculator.boat.Boat;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.calculate.strategy.DriftToStableStateStrategy;
import com.github.thomasfox.boatcalculator.calculate.strategy.LeverSailDaggerboardStrategy;
import com.github.thomasfox.boatcalculator.calculate.strategy.QuantityDifference;
import com.github.thomasfox.boatcalculator.calculate.strategy.QuantityEquality;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.valueset.impl.BoatGlobalValues;
import com.github.thomasfox.boatcalculator.valueset.impl.Crew;
import com.github.thomasfox.boatcalculator.valueset.impl.DaggerboardOrKeel;
import com.github.thomasfox.boatcalculator.valueset.impl.LeverSailDaggerboard;
import com.github.thomasfox.boatcalculator.valueset.impl.Rigg;

public class Sailboat extends Boat
{
  protected Rigg rigg = new Rigg();

  public Sailboat()
  {
    addValueSet(rigg);
    addValueSet(leverSailDaggerboard);
    valuesAndCalculationRules.add(new DriftToStableStateStrategy(PhysicalQuantity.ANGLE_OF_ATTACK, DaggerboardOrKeel.ID, PhysicalQuantity.DRIFT_ANGLE, BoatGlobalValues.ID, 0d));
    valuesAndCalculationRules.add(new QuantityEquality(PhysicalQuantity.LATERAL_FORCE, Rigg.ID, PhysicalQuantity.LIFT, DaggerboardOrKeel.ID)); // assumption: rudder has no force
    valuesAndCalculationRules.add(new QuantityEquality(PhysicalQuantity.APPARENT_WIND_SPEED, BoatGlobalValues.ID, PhysicalQuantity.VELOCITY, Rigg.ID));
    valuesAndCalculationRules.add(new QuantityEquality(PhysicalQuantity.APPARENT_WIND_SPEED, BoatGlobalValues.ID, PhysicalQuantity.VELOCITY, Crew.ID));
    valuesAndCalculationRules.add(new QuantityEquality(PhysicalQuantity.APPARENT_WIND_ANGLE, BoatGlobalValues.ID, PhysicalQuantity.FLOW_DIRECTION, Rigg.ID));
    valuesAndCalculationRules.add(new QuantityEquality(PhysicalQuantity.APPARENT_WIND_ANGLE, BoatGlobalValues.ID, PhysicalQuantity.FLOW_DIRECTION, Crew.ID));
    valuesAndCalculationRules.add(new QuantityEquality(PhysicalQuantity.LATERAL_FORCE, Rigg.ID, PhysicalQuantity.FORCE, LeverSailDaggerboard.ID));
    valuesAndCalculationRules.add(new QuantityDifference(
        new PhysicalQuantityInSet(PhysicalQuantity.FORWARD_FORCE, BoatGlobalValues.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.DRIVING_FORCE, Rigg.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, BoatGlobalValues.ID)));
    valuesAndCalculationRules.add(new LeverSailDaggerboardStrategy());
  }
}
