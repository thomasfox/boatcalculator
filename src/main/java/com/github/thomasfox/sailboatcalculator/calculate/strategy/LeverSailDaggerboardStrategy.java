package com.github.thomasfox.sailboatcalculator.calculate.strategy;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.AllValues;
import com.github.thomasfox.sailboatcalculator.calculate.value.NamedValueSet;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValueWithSetName;

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
    NamedValueSet rigg = allValues.getNamedValueSetNonNull(riggId);
    NamedValueSet daggerboard = allValues.getNamedValueSetNonNull(daggerboardId);
    NamedValueSet leverSailDaggerboard = allValues.getNamedValueSetNonNull(leverSailDaggerboardId);
    PhysicalQuantityValue riggCenterOfEffortHeight = rigg.getKnownValue(PhysicalQuantity.RIGG_CENTER_OF_EFFORT_HEIGHT);
    PhysicalQuantityValue daggerboardSpan = daggerboard.getKnownValue(PhysicalQuantity.WING_SPAN);
    if (riggCenterOfEffortHeight != null && daggerboardSpan != null && !leverSailDaggerboard.isValueKnown(PhysicalQuantity.LEVER_BETWEEN_FORCES))
    {
      leverSailDaggerboard.setCalculatedValueNoOverwrite(
          PhysicalQuantity.LEVER_BETWEEN_FORCES,
          riggCenterOfEffortHeight.getValue() + (daggerboardSpan.getValue() / 2),
          rigg.getName() + ":" +  PhysicalQuantity.RIGG_CENTER_OF_EFFORT_HEIGHT.getDisplayName()
              + ", " + leverSailDaggerboard.getName() + ":" +  PhysicalQuantity.WING_SPAN.getDisplayName(),
          new PhysicalQuantityValueWithSetName(riggCenterOfEffortHeight, rigg.getName()),
          new PhysicalQuantityValueWithSetName(daggerboardSpan, daggerboard.getName()));
      return true;
    }
    return false;
  }
}
