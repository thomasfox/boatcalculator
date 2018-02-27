package com.github.thomasfox.boatcalculator.boat.impl;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.calculate.strategy.QuantityEquality;
import com.github.thomasfox.boatcalculator.calculate.strategy.QuantitySum;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.valueset.SimpleValueSet;
import com.github.thomasfox.boatcalculator.valueset.impl.BoatGlobalValues;
import com.github.thomasfox.boatcalculator.valueset.impl.Crew;
import com.github.thomasfox.boatcalculator.valueset.impl.DaggerboardOrKeel;
import com.github.thomasfox.boatcalculator.valueset.impl.Hull;
import com.github.thomasfox.boatcalculator.valueset.impl.LeverSailDaggerboard;
import com.github.thomasfox.boatcalculator.valueset.impl.Rudder;

public class Dinghy extends Sailboat
{
  protected SimpleValueSet crew = new Crew();

  public Dinghy()
  {
    addValueSet(crew);

    crew.addHiddenOutput(PhysicalQuantity.VELOCITY);

    values.add(new QuantityEquality(
        PhysicalQuantity.TORQUE_BETWEEN_FORCES, LeverSailDaggerboard.ID,
        PhysicalQuantity.TORQUE_BETWEEN_FORCES, Crew.ID));
    values.add(new QuantitySum(
        new PhysicalQuantityInSet(PhysicalQuantity.LIFT, Hull.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.WEIGHT, Crew.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.WEIGHT, BoatGlobalValues.ID)));
    values.add(new QuantitySum(
        new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, BoatGlobalValues.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, Hull.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, Rudder.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, DaggerboardOrKeel.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.BRAKING_FORCE, Crew.ID)));
  }
}
