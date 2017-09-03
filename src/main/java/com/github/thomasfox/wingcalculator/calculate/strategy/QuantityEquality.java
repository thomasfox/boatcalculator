package com.github.thomasfox.wingcalculator.calculate.strategy;

import com.github.thomasfox.wingcalculator.calculate.NamedValueSet;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantityValue;

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

  private final NamedValueSet sourceSet;

  private final PhysicalQuantity targetQuantity;

  @Override
  public boolean setValue(NamedValueSet targetSet)
  {
    PhysicalQuantityValue knownValue = sourceSet.getKnownValue(sourceQuantity);
    if (knownValue != null && !targetSet.isValueKnown(targetQuantity))
    {
      targetSet.setCalculatedValueNoOverwrite(targetQuantity, knownValue.getValue());
      return true;
    }
    return false;
  }


}
