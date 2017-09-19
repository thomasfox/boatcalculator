package com.github.thomasfox.sailboatcalculator.calculate.strategy;

import com.github.thomasfox.sailboatcalculator.calculate.value.AllValues;
import com.github.thomasfox.sailboatcalculator.calculate.value.NamedValueSet;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValue;

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
    NamedValueSet sourceSet = allValues.getNamedValueSetNonNull(sourceSetId);
    NamedValueSet targetSet = allValues.getNamedValueSetNonNull(targetSetId);
    PhysicalQuantityValue knownValue = sourceSet.getKnownValue(sourceQuantity);
    if (knownValue != null && !targetSet.isValueKnown(targetQuantity))
    {
      targetSet.setCalculatedValueNoOverwrite(targetQuantity, knownValue.getValue());
      return true;
    }
    return false;
  }
}
