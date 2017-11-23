package com.github.thomasfox.sailboatcalculator.calculate.strategy;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.AllValues;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValueWithSetId;
import com.github.thomasfox.sailboatcalculator.calculate.value.ValueSet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * A sum of two values.
 */
@AllArgsConstructor
@Getter
@ToString
public class LeverSailDaggerboardStrategy implements ComputationStrategy
{
  private final String riggId;

  private final String daggerboardId;

  private final String leverSailDaggerboardId;

  @Override
  public boolean setValue(AllValues allValues)
  {
    ValueSet rigg = allValues.getValueSetNonNull(riggId);
    ValueSet daggerboard = allValues.getValueSetNonNull(daggerboardId);
    ValueSet leverSailDaggerboard = allValues.getValueSetNonNull(leverSailDaggerboardId);
    PhysicalQuantityValue riggCenterOfEffortHeight = rigg.getKnownValue(PhysicalQuantity.RIGG_CENTER_OF_EFFORT_HEIGHT);
    PhysicalQuantityValue daggerboardSpan = daggerboard.getKnownValue(PhysicalQuantity.WING_SPAN);
    if (riggCenterOfEffortHeight != null && daggerboardSpan != null && !leverSailDaggerboard.isValueKnown(PhysicalQuantity.LEVER_BETWEEN_FORCES))
    {
      leverSailDaggerboard.setCalculatedValueNoOverwrite(
          PhysicalQuantity.LEVER_BETWEEN_FORCES,
          riggCenterOfEffortHeight.getValue() + (daggerboardSpan.getValue() / 2),
          rigg.getName() + ":" +  PhysicalQuantity.RIGG_CENTER_OF_EFFORT_HEIGHT.getDisplayName()
              + ", " + leverSailDaggerboard.getName() + ":" +  PhysicalQuantity.WING_SPAN.getDisplayName(),
          new PhysicalQuantityValueWithSetId(riggCenterOfEffortHeight, rigg.getId()),
          new PhysicalQuantityValueWithSetId(daggerboardSpan, daggerboard.getId()));
      return true;
    }
    return false;
  }
}
