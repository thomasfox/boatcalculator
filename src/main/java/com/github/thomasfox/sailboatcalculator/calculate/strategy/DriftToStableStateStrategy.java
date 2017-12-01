package com.github.thomasfox.sailboatcalculator.calculate.strategy;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.AllValues;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityInSet;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValueWithSetId;
import com.github.thomasfox.sailboatcalculator.progress.CalculationState;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * A strategy to calculate two initially unknown values, source and target,
 * so that they end up equal to another.
 *
 * This strategy requires that source can be calculated if target is known.
 *
 * This is achieved by setting the target value to a initial start value,
 * calculating the source value from it,
 * setting the target value to the source value,
 * again calculating the source value from it,
 * and so on until source and target are sufficiently equal.
 */
@ToString
@Slf4j
public class DriftToStableStateStrategy implements ComputationStrategy
{
  private final PhysicalQuantityInSet source;

  private final PhysicalQuantityInSet target;

  private final double targetQuantityStart;

  public DriftToStableStateStrategy(
      PhysicalQuantity sourceQuantity,
      String sourceSetId,
      PhysicalQuantity targetQuantity,
      String targetSetId,
      double targetQuantityStart)
  {
    this.source = new PhysicalQuantityInSet(sourceQuantity, sourceSetId);
    this.target = new PhysicalQuantityInSet(targetQuantity, targetSetId);
    this.targetQuantityStart = targetQuantityStart;
  }

  @Override
  public boolean setValue(AllValues allValues)
  {
    if (allValues.isValueKnown(target))
    {
      return false;
    }

    if (allValues.isValueKnown(source))
    {
      return false;
    }

    AllValues allValuesForCalculation = new AllValues(allValues);
    allValuesForCalculation.moveCalculatedValuesToStartValues();
    Double sourceValue
        = applyAndRecalculateSourceValue(20, allValuesForCalculation, targetQuantityStart);
    if (sourceValue == null)
    {
      return false;
    }
    allValues.setCalculatedValueNoOverwrite(
        target,
        sourceValue,
        "Drift towards " + allValues.getName(source),
        new PhysicalQuantityValueWithSetId(source.getPhysicalQuantity(), sourceValue, source.getValueSetId()));

    return true;
  }

  private Double applyAndRecalculateSourceValue(int cutoff, AllValues allValues, double targetValue)
  {
    log.info("Trying value " + targetValue + " for drifting " + target);
    CalculationState.set(target.toString(), targetValue);
    if (cutoff <= 0)
    {
      throw new IllegalStateException("Could not calculate "
          + allValues.getName(target)
          + " within cutoff , last value was " + targetQuantityStart);
    }
    clearComputedValuesAndSetTargetValue(targetValue, allValues);
    allValues.calculate();
    Double sourceValue = allValues.getKnownValue(source);
    if (sourceValue == null)
    {
      log.info("No calculated value for " + source + " was calculated for start value " + targetValue + " of " + target);
      return null;
    }

    if (sourceValue == targetValue || (targetValue != 0 && Math.abs(sourceValue - targetValue) < Math.abs(targetValue) / 1000d))
    {
      return sourceValue;
    }
    return applyAndRecalculateSourceValue(cutoff - 1, allValues, sourceValue);
  }

  private void clearComputedValuesAndSetTargetValue(double targetValue, AllValues allValues)
  {
    allValues.clearCalculatedValues();
    allValues.setCalculatedValueNoOverwrite(target, targetValue, getClass().getSimpleName() + " trial value");
  }
}
