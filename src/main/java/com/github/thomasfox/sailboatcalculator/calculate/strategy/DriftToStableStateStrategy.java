package com.github.thomasfox.sailboatcalculator.calculate.strategy;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.AllValues;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValueWithSetName;
import com.github.thomasfox.sailboatcalculator.calculate.value.ValueSet;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class DriftToStableStateStrategy implements ComputationStrategy
{
  private final PhysicalQuantity sourceQuantity;

  private final String sourceQuantitySetId;

  private final PhysicalQuantity targetQuantity;

  private final String targetQuantitySetId;

  private final double targetQuantityStart;

  @Override
  public boolean setValue(AllValues allValues)
  {
    ValueSet targetQuantitySet = allValues.getValueSetNonNull(targetQuantitySetId);
    PhysicalQuantityValue targetValue = targetQuantitySet.getKnownValue(targetQuantity);
    if (targetValue != null)
    {
      return false;
    }

    ValueSet sourceQuantitySet = allValues.getValueSetNonNull(sourceQuantitySetId);
    PhysicalQuantityValue sourceQuantityValue = sourceQuantitySet.getKnownValue(sourceQuantity);
    if (sourceQuantityValue != null)
    {
      return false;
    }

    AllValues allValuesForCalculation = new AllValues(allValues);
    allValuesForCalculation.moveCalculatedValuesToStartValues();
    sourceQuantityValue = applyAndRecalculateSourceValue(20, allValuesForCalculation, targetQuantityStart);
    if (sourceQuantityValue == null)
    {
      return false;
    }
    targetQuantitySet.setCalculatedValueNoOverwrite(
        targetQuantity,
        sourceQuantityValue.getValue(),
        "Drift towards " + sourceQuantitySet.getName() + ": " + sourceQuantity.getDisplayName(),
        new PhysicalQuantityValueWithSetName(sourceQuantityValue, sourceQuantitySet.getName()));

    return true;
  }

  private PhysicalQuantityValue applyAndRecalculateSourceValue(int cutoff, AllValues allValues, double targetValue)
  {
    if (cutoff <= 0)
    {
      throw new IllegalStateException("Could not calculate " + targetQuantity
          + " in " + targetQuantitySetId
          + " within cutoff , last value was " + targetQuantityStart);
    }
    clearComputedValuesAndSetTargetValue(targetValue, allValues);
    allValues.calculate();
    ValueSet sourceQuantitySet = allValues.getValueSetNonNull(sourceQuantitySetId);
    PhysicalQuantityValue sourceValue = sourceQuantitySet.getKnownValue(sourceQuantity);
    if (sourceValue == null)
    {
      return null;
    }

    if (sourceValue.getValue() == targetValue || (targetValue != 0 && Math.abs(sourceValue.getValue() - targetValue) < Math.abs(targetValue) / 1000d))
    {
      return sourceValue;
    }
    return applyAndRecalculateSourceValue(cutoff - 1, allValues, sourceValue.getValue());
  }

  private void clearComputedValuesAndSetTargetValue(double targetValue, AllValues allValues)
  {
    allValues.clearCalculatedValues();
    ValueSet scannedQuantitySet = allValues.getValueSetNonNull(targetQuantitySetId);
    scannedQuantitySet.setCalculatedValueNoOverwrite(targetQuantity, targetValue, getClass().getSimpleName() + " trial value");
  }
}
