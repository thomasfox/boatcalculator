package com.github.thomasfox.boatcalculator.calculate.strategy;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.valueset.AllValues;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;
import com.github.thomasfox.boatcalculator.valueset.impl.DaggerboardOrKeel;
import com.github.thomasfox.boatcalculator.valueset.impl.LeverSailDaggerboard;
import com.github.thomasfox.boatcalculator.valueset.impl.Rigg;

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
  public boolean calculateAndSetValue(AllValues allValues)
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
          rigg.getDisplayName() + ":" +  PhysicalQuantity.RIGG_CENTER_OF_EFFORT_HEIGHT.getDisplayName()
              + ", " + leverSailDaggerboard.getDisplayName() + ":" +  PhysicalQuantity.WING_SPAN.getDisplayName(),
          new PhysicalQuantityValueWithSetId(riggCenterOfEffortHeight, rigg.getId()),
          new PhysicalQuantityValueWithSetId(daggerboardSpan, daggerboard.getId()));
      return true;
    }
    return false;
  }
}
