package com.github.thomasfox.wingcalculator.calculate.strategy;

import com.github.thomasfox.wingcalculator.calculate.NamedValueSet;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantityValue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

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
