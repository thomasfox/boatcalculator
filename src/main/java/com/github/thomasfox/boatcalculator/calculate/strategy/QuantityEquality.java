package com.github.thomasfox.boatcalculator.calculate.strategy;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.valueset.AllValues;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * A directed equality. A quantity is known to be equal to another.
 * So if the one quantity is known, the other quantities value can be set
 * equal to the value of the first quantity.
 */
@AllArgsConstructor
@Getter
@ToString
public class QuantityEquality implements ComputationStrategy
{
  private final PhysicalQuantity sourceQuantity;

  private final String sourceSetId;

  private final PhysicalQuantity targetQuantity;

  private final String targetSetId;

  @Override
  public boolean setValue(AllValues allValues)
  {
    ValueSet sourceSet = allValues.getValueSetNonNull(sourceSetId);
    ValueSet targetSet = allValues.getValueSetNonNull(targetSetId);
    PhysicalQuantityValue knownValue = sourceSet.getKnownValue(sourceQuantity);
    if (knownValue != null && !targetSet.isValueKnown(targetQuantity))
    {
      targetSet.setCalculatedValueNoOverwrite(
          new PhysicalQuantityValue(targetQuantity, knownValue.getValue()),
          sourceSet.getDisplayName() + ":" +  sourceQuantity.getDisplayName(),
          new PhysicalQuantityValueWithSetId(knownValue, sourceSet.getId()));
      return true;
    }
    return false;
  }
}
