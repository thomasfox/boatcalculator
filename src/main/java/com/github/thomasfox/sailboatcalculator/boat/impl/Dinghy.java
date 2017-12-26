package com.github.thomasfox.sailboatcalculator.boat.impl;

import com.github.thomasfox.sailboatcalculator.boat.Boat;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.strategy.QuantityEquality;
import com.github.thomasfox.sailboatcalculator.calculate.strategy.QuantitySum;
import com.github.thomasfox.sailboatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.sailboatcalculator.valueset.SimpleValueSet;
import com.github.thomasfox.sailboatcalculator.valueset.impl.BoatGlobalValues;
import com.github.thomasfox.sailboatcalculator.valueset.impl.Crew;
import com.github.thomasfox.sailboatcalculator.valueset.impl.DaggerboardOrKeel;
import com.github.thomasfox.sailboatcalculator.valueset.impl.Hull;
import com.github.thomasfox.sailboatcalculator.valueset.impl.LeverSailDaggerboard;
import com.github.thomasfox.sailboatcalculator.valueset.impl.Rudder;

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
        new PhysicalQuantityInSet(PhysicalQuantity.MASS, Hull.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.MASS, Crew.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.MASS, BoatGlobalValues.ID)));
    values.add(new QuantitySum(
        new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, BoatGlobalValues.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, Hull.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, Rudder.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, DaggerboardOrKeel.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.BRAKING_FORCE, Crew.ID)));
  }
}
