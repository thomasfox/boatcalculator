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
 * A directed equality. A quantity is known to be equal to another.
 * So if the one quantity is known, the other quantities value can be set
 * equal to the value of the first quantity.
 */
@AllArgsConstructor
@Getter
@ToString
public class QuantitySum implements ComputationStrategy
{
  private final PhysicalQuantity sourceQuantity1;

  private final String sourceSetId1;

  private final PhysicalQuantity sourceQuantity2;

  private final String sourceSetId2;

  private final PhysicalQuantity targetQuantity;

  private final String targetSetId;

  @Override
  public boolean setValue(AllValues allValues)
  {
    NamedValueSet sourceSet1 = allValues.getNamedValueSetNonNull(sourceSetId1);
    NamedValueSet sourceSet2 = allValues.getNamedValueSetNonNull(sourceSetId2);
    NamedValueSet targetSet = allValues.getNamedValueSetNonNull(targetSetId);
    PhysicalQuantityValue knownValue1 = sourceSet1.getKnownValue(sourceQuantity1);
    PhysicalQuantityValue knownValue2 = sourceSet2.getKnownValue(sourceQuantity2);
    if (knownValue1 != null && knownValue2 != null && !targetSet.isValueKnown(targetQuantity))
    {
      targetSet.setCalculatedValueNoOverwrite(
          targetQuantity,
          knownValue1.getValue() + knownValue2.getValue(),
          sourceSet1.getName() + ":" +  sourceQuantity1.getDisplayName()
              + "+" + sourceSet2.getName() + ":" +  sourceQuantity2.getDisplayName(),
          new PhysicalQuantityValueWithSetName(knownValue1, sourceSet1.getName()),
          new PhysicalQuantityValueWithSetName(knownValue2, sourceSet2.getName()));
      return true;
    }
    return false;
  }
}
