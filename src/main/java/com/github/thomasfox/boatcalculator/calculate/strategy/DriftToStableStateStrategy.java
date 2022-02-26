package com.github.thomasfox.boatcalculator.calculate.strategy;

import java.util.HashSet;
import java.util.Set;

import com.github.thomasfox.boatcalculator.calculate.CalculationResult;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.progress.CalculationState;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;
import com.github.thomasfox.boatcalculator.valueset.ValuesAndCalculationRules;

import lombok.ToString;

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
  public boolean step(ValuesAndCalculationRules allValues)
  {
    ValueSet targetSet = allValues.getValueSetNonNull(target.getSetId());
    PhysicalQuantity targetQuantity = target.getPhysicalQuantity();
    PhysicalQuantityValue knownTargetValue = targetSet.getKnownQuantityValue(targetQuantity);
    if (knownTargetValue == null)
    {
     PhysicalQuantityValue newTargetValue = new SimplePhysicalQuantityValue(
         targetQuantity,
         targetQuantityStart);
      targetSet.setCalculatedValueNoOverwrite(
          newTargetValue,
          getClass().getSimpleName() + " trial value",
          true,
          new SimplePhysicalQuantityValueWithSetId(newTargetValue, target.getSetId()));
      knownTargetValue = targetSet.getKnownQuantityValue(targetQuantity);
    }

    if (!knownTargetValue.isTrial())
    {
      // targetValue is no trial value and thus already calculated
      return false;
    }

    PhysicalQuantity sourceQuantity = source.getPhysicalQuantity();
    ValueSet sourceSet = allValues.getValueSetNonNull(source.getSetId());
    PhysicalQuantityValue knownSourceValue = sourceSet.getKnownQuantityValue(sourceQuantity);

    if (knownSourceValue != null && !knownSourceValue.isTrial())
    {
      // sourceValue is no trial value and thus already calculated
      return false;
    }

    if (knownSourceValue == null)
    {
      // we cannot currently drift because source was not calculated
      return false;
    }

    CalculationState.set(target.getSetId() + ":" + target.getPhysicalQuantity(), knownSourceValue.getValue());
    PhysicalQuantityValue newTargetValue = new SimplePhysicalQuantityValue(
        targetQuantity,
        knownSourceValue.getValue());
    targetSet.setCalculatedValue(
        newTargetValue,
        getClass().getSimpleName() + " trial value",
        true,
        new SimplePhysicalQuantityValueWithSetId(knownSourceValue, source.getSetId()));

    CalculationResult sourceTargetDifference = new CalculationResult(knownSourceValue.getValue(), knownTargetValue.getValue() , true);

    return !sourceTargetDifference.relativeDifferenceIsBelowThreshold();
  }

  @Override
  public Set<PhysicalQuantityInSet> getOutputs()
  {
    Set<PhysicalQuantityInSet> result = new HashSet<>();
    return result;
  }

  @Override
  public Set<PhysicalQuantityInSet> getInputs()
  {
    Set<PhysicalQuantityInSet> result = new HashSet<>();
    result.add(target);
    result.add(source);
    return result;
  }

}
