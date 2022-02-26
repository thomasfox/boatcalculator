package com.github.thomasfox.boatcalculator.calculate.strategy;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.github.thomasfox.boatcalculator.calculate.CalculationResult;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;
import com.github.thomasfox.boatcalculator.valueset.ValuesAndCalculationRules;

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
  public boolean step(ValuesAndCalculationRules allValues)
  {
    ValueSet sourceSet = allValues.getValueSetNonNull(sourceSetId);
    ValueSet targetSet = allValues.getValueSetNonNull(targetSetId);
    PhysicalQuantityValue knownValue = sourceSet.getKnownQuantityValue(sourceQuantity);
    PhysicalQuantityValue sourceValue = sourceSet.getKnownQuantityValue(sourceQuantity);
    PhysicalQuantityValue targetValue = targetSet.getKnownQuantityValue(targetQuantity);
    if (sourceValue != null && (targetValue == null || targetValue.isTrial()))
    {
      Double oldResult = Optional.ofNullable(targetValue)
          .map(PhysicalQuantityValue::getValue)
          .orElse(null);
      CalculationResult calculationResult = new CalculationResult(
          sourceValue.getValue(),
          oldResult,
          sourceValue.isTrial());

      targetSet.setCalculatedValueNoOverwrite(
          new SimplePhysicalQuantityValue(targetQuantity, knownValue.getValue()),
          sourceSet.getDisplayName() + ":" +  sourceQuantity.getDisplayName(),
          knownValue.isTrial(),
          new SimplePhysicalQuantityValueWithSetId(knownValue, sourceSet.getId()));

      return !calculationResult.relativeDifferenceIsBelowThreshold();
    }
    return false;
  }

  @Override
  public Set<PhysicalQuantityInSet> getOutputs()
  {
    Set<PhysicalQuantityInSet> result = new HashSet<>();
    result.add(new PhysicalQuantityInSet(targetQuantity, targetSetId));
    return result;
  }

  @Override
  public Set<PhysicalQuantityInSet> getInputs()
  {
    Set<PhysicalQuantityInSet> result = new HashSet<>();
    result.add(new PhysicalQuantityInSet(sourceQuantity, sourceSetId));
    return result;
  }
}
