package com.github.thomasfox.boatcalculator.boat.impl;

import com.github.thomasfox.boatcalculator.boat.Boat;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.calculate.strategy.DriftToStableStateStrategy;
import com.github.thomasfox.boatcalculator.calculate.strategy.LeverSailDaggerboardStrategy;
import com.github.thomasfox.boatcalculator.calculate.strategy.QuantityEquality;
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
    values.add(new DriftToStableStateStrategy(PhysicalQuantity.ANGLE_OF_ATTACK, DaggerboardOrKeel.ID, PhysicalQuantity.DRIFT_ANGLE, BoatGlobalValues.ID, 0d));
    values.add(new QuantityEquality(PhysicalQuantity.LATERAL_FORCE, Rigg.ID, PhysicalQuantity.LIFT, DaggerboardOrKeel.ID)); // assumption: rudder has no force
    values.add(new QuantityEquality(PhysicalQuantity.APPARENT_WIND_SPEED, BoatGlobalValues.ID, PhysicalQuantity.VELOCITY, Rigg.ID));
    values.add(new QuantityEquality(PhysicalQuantity.APPARENT_WIND_SPEED, BoatGlobalValues.ID, PhysicalQuantity.VELOCITY, Crew.ID));
    values.add(new QuantityEquality(PhysicalQuantity.APPARENT_WIND_ANGLE, BoatGlobalValues.ID, PhysicalQuantity.FLOW_DIRECTION, Rigg.ID));
    values.add(new QuantityEquality(PhysicalQuantity.APPARENT_WIND_ANGLE, BoatGlobalValues.ID, PhysicalQuantity.FLOW_DIRECTION, Crew.ID));
    values.add(new QuantityEquality(PhysicalQuantity.LATERAL_FORCE, Rigg.ID, PhysicalQuantity.FORCE, LeverSailDaggerboard.ID));
    values.add(new LeverSailDaggerboardStrategy());
  }
}
