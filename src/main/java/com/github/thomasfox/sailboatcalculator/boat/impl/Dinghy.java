package com.github.thomasfox.sailboatcalculator.boat.impl;

import com.github.thomasfox.sailboatcalculator.boat.Boat;
import com.github.thomasfox.sailboatcalculator.boat.valueset.BoatGlobalValues;
import com.github.thomasfox.sailboatcalculator.boat.valueset.Crew;
import com.github.thomasfox.sailboatcalculator.boat.valueset.DaggerboardOrKeel;
import com.github.thomasfox.sailboatcalculator.boat.valueset.Hull;
import com.github.thomasfox.sailboatcalculator.boat.valueset.LeverSailDaggerboard;
import com.github.thomasfox.sailboatcalculator.boat.valueset.Rudder;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.strategy.QuantityEquality;
import com.github.thomasfox.sailboatcalculator.calculate.strategy.QuantitySum;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityInSet;
import com.github.thomasfox.sailboatcalculator.calculate.value.SimpleValueSet;

public class Dinghy extends Boat
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
        new PhysicalQuantityInSet(PhysicalQuantity.WEIGHT, Hull.ID),
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
