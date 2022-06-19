package com.github.thomasfox.boatcalculator.calculate.strategy;

import java.util.HashSet;
import java.util.Set;

import com.github.thomasfox.boatcalculator.calculate.CompareWithOldResult;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;
import com.github.thomasfox.boatcalculator.valueset.ValuesAndCalculationRules;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@ToString
@Slf4j
/**
 * Modifies the quantity targetQuantity until two other quantities equalQuantity1 and equalQuantity2 are equal.
 *
 * It is assumed that if equalQuantity1 is larger than equalQuantity2, targetQuantity must be increased,
 * whereas if equalQuantity1 is smaller than equalQuantity2, targetQuantity must be decreased.
 */
public class TwoValuesShouldBeEqualModifyThirdStrategy implements ComputationFromConvergedResultStrategy
{
  private static final double FACTOR_START_VALUE = 0.03;

  private final PhysicalQuantity equalQuantity1;

  private final String equalQuantity1SetId;

  private final PhysicalQuantity equalQuantity2;

  private final String equalQuantity2SetId;

  private final PhysicalQuantity targetQuantity;

  private final String targetSetId;

  private final double lowerCutoff;

  private final double upperCutoff;

  private Double lastDifference;

  private Double lastTrialValueDifference;

  private Double trialTargetValue = null;

  private double differenceToTargetDifferenceFactor = FACTOR_START_VALUE;

  @Override
  public void reset()
  {
    lastDifference = null;
    lastTrialValueDifference = null;
    trialTargetValue = null;
    differenceToTargetDifferenceFactor = FACTOR_START_VALUE;
  }

  @Override
  public void setStartValues(ValuesAndCalculationRules allValues)
  {
    ValueSet targetSet = allValues.getValueSetNonNull(targetSetId);
    PhysicalQuantityValue knownTargetValue = targetSet.getKnownQuantityValue(targetQuantity);
    if (knownTargetValue == null)
    {
      if (trialTargetValue == null)
      {
        trialTargetValue = lowerCutoff;
      }
      PhysicalQuantityValue newTargetValue = new SimplePhysicalQuantityValue(targetQuantity, trialTargetValue);
      targetSet.setCalculatedValueNoOverwrite(
          newTargetValue,
          getClass().getSimpleName() + " trial Value",
          true,
          new SimplePhysicalQuantityValueWithSetId(newTargetValue, targetSetId));
    }
  }

  @Override
  public boolean stepAfterConvergence(ValuesAndCalculationRules allValues)
  {
    ValueSet targetSet = allValues.getValueSetNonNull(targetSetId);
    PhysicalQuantityValue knownTargetValue = targetSet.getKnownQuantityValue(targetQuantity);

    if (!knownTargetValue.isTrial())
    {
      // targetValue is no trial value and thus already calculated
      targetSet.removeCalculatedValue(targetQuantity);
      return false;
    }

    double trialValue = knownTargetValue.getValue();
    CalculateDifferenceResult difference = getDifference(allValues);
    if (difference == null)
    {
      // difference is not available
      return false;
    }

    if (lastDifference != null && difference.getDifference() == lastDifference)
    {
      // there was no new value calculated, or already arrived at target
      return false;
    }
    lastDifference = difference.getDifference();

    if (difference.getRelativeDifference() < CompareWithOldResult.RELATIVE_DIFFERENCE_THRESHOLD)
    {
      // already arrived at target
      return false;
    }

    double trialValueDifference = difference.getDifference() * differenceToTargetDifferenceFactor;
    if (lastTrialValueDifference != null)
    {
      if (trialValueDifference*lastTrialValueDifference < 0d // they have different sign
        && Math.abs(trialValueDifference) > Math.abs(lastTrialValueDifference) * 0.5)
      {
        trialValueDifference = trialValueDifference * 0.5;
        differenceToTargetDifferenceFactor *= 0.5;
      }
    }
    lastTrialValueDifference = trialValueDifference;
    double newTrialValue = trialValue + trialValueDifference;
    if (newTrialValue < lowerCutoff)
    {
      newTrialValue = lowerCutoff;
      differenceToTargetDifferenceFactor *= 0.5;
    }
    if (newTrialValue > upperCutoff)
    {
      newTrialValue = upperCutoff;
      differenceToTargetDifferenceFactor *= 0.5;
    }
    trialTargetValue = newTrialValue;
    targetSet.setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(targetQuantity, newTrialValue),
        getClass().getSimpleName() + " trial Value",
        true,
        difference.getValue1(),
        difference.getValue2());
    if (newTrialValue != 0
        && Math.abs(trialValueDifference/newTrialValue) > CompareWithOldResult.RELATIVE_DIFFERENCE_THRESHOLD)
    {
      return true;
    }
    return false;
  }


  private boolean step(ValuesAndCalculationRules allValues)
  {
    ValueSet targetSet = allValues.getValueSetNonNull(targetSetId);
    PhysicalQuantityValue knownTargetValue = targetSet.getKnownQuantityValue(targetQuantity);
    if (knownTargetValue == null)
    {
      PhysicalQuantityValue newTargetValue = new SimplePhysicalQuantityValue(targetQuantity, lowerCutoff);
      targetSet.setCalculatedValueNoOverwrite(
          newTargetValue,
          getClass().getSimpleName() + " trial Value",
          true,
          new SimplePhysicalQuantityValueWithSetId(newTargetValue, targetSetId));
      lastDifference = null;
      lastTrialValueDifference = null;
      differenceToTargetDifferenceFactor = FACTOR_START_VALUE;
      return true;
    }

    if (!knownTargetValue.isTrial())
    {
      // targetValue is no trial value and thus already calculated
      return false;
    }

    double trialValue = knownTargetValue.getValue();
//    double stepValue;
    CalculateDifferenceResult difference = getDifference(allValues);
    if (difference == null)
    {
      // difference is not available
      return false;
    }

    if (lastDifference != null && difference.getDifference() == lastDifference)
    {
      // there was no new value calculated, or already arrived at target
      return false;
    }
    lastDifference = difference.getDifference();

    if (difference.getRelativeDifference() < CompareWithOldResult.RELATIVE_DIFFERENCE_THRESHOLD)
    {
      // already arrived at target
      return false;
    }

    double trialValueDifference = difference.getDifference() * differenceToTargetDifferenceFactor;
    if (lastTrialValueDifference != null)
    {
      if (trialValueDifference*lastTrialValueDifference < 0d // they have different sign
        && Math.abs(trialValueDifference) > Math.abs(lastTrialValueDifference) * 0.9)
      {
        trialValueDifference = trialValueDifference * 0.5;
        differenceToTargetDifferenceFactor *= 0.5;
      }
    }
    lastTrialValueDifference = trialValueDifference;
    double newTrialValue = trialValue + trialValueDifference;
    if (newTrialValue < lowerCutoff)
    {
      newTrialValue = lowerCutoff;
      differenceToTargetDifferenceFactor *= 0.5;
    }
    if (newTrialValue > upperCutoff)
    {
      newTrialValue = upperCutoff;
      differenceToTargetDifferenceFactor *= 0.5;
    }
    targetSet.setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(targetQuantity, newTrialValue),
        getClass().getSimpleName() + " trial Value",
        true,
        difference.getValue1(),
        difference.getValue2());
    if (newTrialValue != 0
        && Math.abs(trialValueDifference/newTrialValue) > CompareWithOldResult.RELATIVE_DIFFERENCE_THRESHOLD)
    {
      return true;
    }
    return false;
  }

  private CalculateDifferenceResult getDifference(ValuesAndCalculationRules allValues)
  {
    ValueSet equalQuantity1Set = allValues.getValueSetNonNull(equalQuantity1SetId);
    ValueSet equalQuantity2Set = allValues.getValueSetNonNull(equalQuantity2SetId);
    PhysicalQuantityValue value1 =  equalQuantity1Set.getKnownQuantityValue(equalQuantity1);
    PhysicalQuantityValue value2 =  equalQuantity2Set.getKnownQuantityValue(equalQuantity2);
    if (value1 == null)
    {
      log.info("No value for " + equalQuantity1
          + " in " + equalQuantity1Set.getDisplayName()
          + " for quantity " + targetQuantity.getDisplayName() + " of " + targetSetId + ":" + targetQuantity);
    }
    if (value2 == null)
    {
      log.info("No value for " + equalQuantity2
          + " in " + equalQuantity2Set.getDisplayName()
          + " for quantity " + targetQuantity.getDisplayName() + " of " + targetSetId + ":" + targetQuantity);
    }
    if (value1 == null || value2 == null)
    {
      return null;
    }
    return new CalculateDifferenceResult(
        new SimplePhysicalQuantityValueWithSetId(value1, equalQuantity1SetId),
        new SimplePhysicalQuantityValueWithSetId(value2, equalQuantity2SetId));
  }

  @Override
  public Set<PhysicalQuantityInSet> getOutputs()
  {
    Set<PhysicalQuantityInSet> result = new HashSet<>();
    result.add(new PhysicalQuantityInSet(targetQuantity, targetSetId));
    result.add(new PhysicalQuantityInSet(equalQuantity1, equalQuantity1SetId));
    result.add(new PhysicalQuantityInSet(equalQuantity2, equalQuantity2SetId));
    return result;
  }

  @Override
  public Set<PhysicalQuantityInSet> getInputs()
  {
    Set<PhysicalQuantityInSet> result = new HashSet<>();
    return result;
  }

  private static class CalculateDifferenceResult
  {
    @Getter
    private final PhysicalQuantityValueWithSetId value1;

    @Getter
    private final PhysicalQuantityValueWithSetId value2;

    @Getter
    private final double difference;

    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append("[difference=");
      builder.append(difference);
      builder.append(", relativeDifference=");
      builder.append(relativeDifference);
      builder.append("]");
      return builder.toString();
    }

    @Getter
    private double relativeDifference;

    public CalculateDifferenceResult(
        PhysicalQuantityValueWithSetId value1,
        PhysicalQuantityValueWithSetId value2)
    {
      this.value1 = value1;
      this.value2 = value2;
      difference = value1.getValue() - value2.getValue();
      if (difference == 0d)
      {
        relativeDifference = 0d;
      }
      else
      {
        double maxAbsoluteValue = Math.max(Math.abs(value1.getValue()), Math.abs(value2.getValue()));
        double absoluteDifference = Math.abs(difference);
        relativeDifference = absoluteDifference / maxAbsoluteValue;
      }
    }
  }
}
