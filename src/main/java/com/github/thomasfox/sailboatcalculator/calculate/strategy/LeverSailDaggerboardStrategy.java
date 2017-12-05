package com.github.thomasfox.sailboatcalculator.calculate.strategy;

import com.github.thomasfox.sailboatcalculator.boat.valueset.DaggerboardOrKeel;
import com.github.thomasfox.sailboatcalculator.boat.valueset.LeverSailDaggerboard;
import com.github.thomasfox.sailboatcalculator.boat.valueset.Rigg;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.AllValues;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValueWithSetId;
import com.github.thomasfox.sailboatcalculator.calculate.value.ValueSet;

import lombok.Getter;
import lombok.ToString;

/**
 * Calculates the Lever between Rigg and Daggerboard
 * from the center of effort height of the rigg
 * and the span of the daggerboard.
 */
@Getter
@ToString
public class LeverSailDaggerboardStrategy implements ComputationStrategy
{
  @Override
  public boolean setValue(AllValues allValues)
  {
    ValueSet rigg = allValues.getValueSetNonNull(Rigg.ID);
    ValueSet daggerboard = allValues.getValueSetNonNull(DaggerboardOrKeel.ID);
    ValueSet leverSailDaggerboard = allValues.getValueSetNonNull(LeverSailDaggerboard.ID);
    PhysicalQuantityValue riggCenterOfEffortHeight = rigg.getKnownValue(PhysicalQuantity.RIGG_CENTER_OF_EFFORT_HEIGHT);
    PhysicalQuantityValue daggerboardSpan = daggerboard.getKnownValue(PhysicalQuantity.WING_SPAN);
    if (riggCenterOfEffortHeight != null && daggerboardSpan != null && !leverSailDaggerboard.isValueKnown(PhysicalQuantity.LEVER_BETWEEN_FORCES))
    {
      leverSailDaggerboard.setCalculatedValueNoOverwrite(
          new PhysicalQuantityValue(
              PhysicalQuantity.LEVER_BETWEEN_FORCES,
              riggCenterOfEffortHeight.getValue() + (daggerboardSpan.getValue() / 2)),
          rigg.getName() + ":" +  PhysicalQuantity.RIGG_CENTER_OF_EFFORT_HEIGHT.getDisplayName()
              + ", " + leverSailDaggerboard.getName() + ":" +  PhysicalQuantity.WING_SPAN.getDisplayName(),
          new PhysicalQuantityValueWithSetId(riggCenterOfEffortHeight, rigg.getId()),
          new PhysicalQuantityValueWithSetId(daggerboardSpan, daggerboard.getId()));
      return true;
    }
    return false;
  }
}
