package com.github.thomasfox.sailboatcalculator.calculate.strategy;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.AllValues;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.calculate.value.ValueSet;
import com.github.thomasfox.sailboatcalculator.progress.CalculationState;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@ToString
@Slf4j
public class TwoValuesShouldBeEqualModifyThirdStrategy implements ComputationStrategy
{
  private static double CUTOFF = 1E-4;

  private static int MAX_TRIES = 100;

  private static int TRIAL_INTERVALS = 20;

  private final PhysicalQuantity equalQuantity1;

  private final String equalQuantity1SetId;

  private final PhysicalQuantity equalQuantity2;

  private final String equalQuantity2SetId;

  private final PhysicalQuantity targetQuantity;

  private final String targetSetId;

  private final double lowerCutoff;

  private final double upperCutoff;

  @Override
  public boolean setValue(AllValues allValues)
  {
    ValueSet targetSet = allValues.getValueSetNonNull(targetSetId);
    PhysicalQuantityValue knownTargetValue = targetSet.getKnownValue(targetQuantity);
    if (knownTargetValue != null)
    {
      return false;
    }

    double targetValue1 = lowerCutoff;
    double targetValue2 = upperCutoff;
    int remainingTries = MAX_TRIES;
    double cutoffTrialInterval = getCutoffTrialInterval();
    AllValues allValuesForCalculation = new AllValues(allValues);
    allValuesForCalculation.moveCalculatedValuesToStartValues();
    CalculateDifferenceResult difference1 = null;
    CalculateDifferenceResult difference2 = null;
    while (targetValue1 <= targetValue2 && (difference1 == null || difference2 == null))
    {
      if (difference1 == null)
      {
        difference1 = calculateDifference(targetValue1, allValuesForCalculation);
      }
      if (difference1 == null && (targetValue1 + cutoffTrialInterval < targetValue2))
      {
        targetValue1 += cutoffTrialInterval;
      }
      if (difference2 == null && (targetValue1 < targetValue2 - cutoffTrialInterval))
      {
        difference2 = calculateDifference(targetValue2, allValuesForCalculation);
      }
      if (difference2 == null && remainingTries > 0)
      {
        targetValue2 -= cutoffTrialInterval;
      }
    }
    if (difference1 == null || difference2 == null)
    {
      return false;
    }
    boolean changed = applyAndRecalculateWithPoints(targetValue1, difference1, targetValue2, difference2, allValuesForCalculation, remainingTries);
    if (changed)
    {
      double targetValue = allValuesForCalculation.getValueSetNonNull(targetSetId).getKnownValue(targetQuantity).getValue();
      targetSet.setCalculatedValueNoOverwrite(targetQuantity, targetValue, getClass().getSimpleName());
    }
    return changed;
  }

  private double getCutoffTrialInterval()
  {
    return (upperCutoff - lowerCutoff) / TRIAL_INTERVALS;
  }

  private boolean applyAndRecalculateWithPoints(
      double targetValue1,
      CalculateDifferenceResult difference1,
      double targetValue2,
      CalculateDifferenceResult difference2,
      AllValues allValues,
      int maxTries)
  {
    log.info("Try values " + targetValue1 + " and " + targetValue2
        + " for target Quantity " + targetSetId + ":" + targetQuantity
        + ", maxTries = " + maxTries);
    if (maxTries <= 0)
    {
      return false;
    }
    if (difference1.getRelativeDifference() < CUTOFF)
    {
      clearComputedValuesAndSetTargetValue(targetValue1, allValues);
      return true;
    }
    if (difference2.getRelativeDifference() < CUTOFF)
    {
      clearComputedValuesAndSetTargetValue(targetValue2, allValues);
      return true;
    }
    if (difference1.getDifference() == difference2.getDifference())
    {
      targetValue2 = targetValue2 + 1; // random shot
      difference2 = calculateDifference(targetValue2, allValues);
      applyAndRecalculateWithPoints(
          targetValue1,
          difference1,
          targetValue2,
          difference2,
          allValues,
          maxTries - 1);
    }
    double estimatedTarget = targetValue1
        - (targetValue2 - targetValue1) * difference1.getDifference() / (difference2.getDifference() - difference1.getDifference());
    if (estimatedTarget < lowerCutoff)
    {
      estimatedTarget = lowerCutoff;
    }
    if (estimatedTarget > upperCutoff)
    {
      estimatedTarget = upperCutoff;
    }
    CalculateDifferenceResult estimatedTargetDifference = calculateDifference(estimatedTarget, allValues);
//    if (estimatedTargetDifference == null)
//    {
//      // perhaps a noncalculatable region was reached outside the trial region, try half the intervall.
//      double minTargetValue = Math.min(targetValue1, targetValue2);
//      while (estimatedTarget < minTargetValue && maxTries > 0 && estimatedTargetDifference == null)
//      {
//        maxTries--;
//        estimatedTarget = estimatedTarget / 2 + minTargetValue / 2;
//        estimatedTargetDifference = calculateDifference(estimatedTarget, allValues);
//      }
//      double maxTargetValue = Math.max(targetValue1, targetValue2);
//      while (estimatedTarget > maxTargetValue && maxTries > 0 && estimatedTargetDifference == null)
//      {
//        maxTries--;
//        estimatedTarget = estimatedTarget / 2 + maxTargetValue / 2;
//        estimatedTargetDifference = calculateDifference(estimatedTarget, allValues);
//      }
//    }
    if (estimatedTargetDifference == null)
    {
      log.info("Could not calculate difference between "
          + equalQuantity1 + " in " + equalQuantity1SetId
          + " and " + equalQuantity2 + " in " + equalQuantity2SetId
          + " for target Quantity " + targetQuantity + " with value " + estimatedTarget);
      return false;
    }
    if (Math.signum(difference1.getDifference()) == Math.signum(estimatedTargetDifference.getDifference()))
    {
      return applyAndRecalculateWithPoints(estimatedTarget, estimatedTargetDifference, targetValue2, difference2, allValues, maxTries - 1);
    }
    else
    {
      return applyAndRecalculateWithPoints(targetValue1, difference1, estimatedTarget, estimatedTargetDifference, allValues, maxTries - 1);
    }
  }

  private CalculateDifferenceResult calculateDifference(double targetValue, AllValues allValues)
  {
    CalculationState.set(targetSetId + ":" + targetQuantity, targetValue);
    clearComputedValuesAndSetTargetValue(targetValue, allValues);
    allValues.calculate(null);
    ValueSet equalQuantity1Set = allValues.getValueSetNonNull(equalQuantity1SetId);
    ValueSet equalQuantity2Set = allValues.getValueSetNonNull(equalQuantity2SetId);
    PhysicalQuantityValue value1 =  equalQuantity1Set.getKnownValue(equalQuantity1);
    PhysicalQuantityValue value2 =  equalQuantity2Set.getKnownValue(equalQuantity2);
    if (value1 == null)
    {
      log.info("Could not calculate " + equalQuantity1
          + " in " + equalQuantity1Set.getName()
          + " for value " + targetValue + " of " + targetSetId + ":" + targetQuantity);
    }
    if (value2 == null)
    {
      log.info("Could not calculate " + equalQuantity2
          + " in " + equalQuantity2Set.getName()
          + " for value " + targetValue + " of " + targetSetId + ":" + targetQuantity);
    }
    if (value1==null || value2 == null)
    {
      allValues.logState();
      return null;
    }
    return new CalculateDifferenceResult(value1.getValue(), value2.getValue());
  }

  private void clearComputedValuesAndSetTargetValue(double targetValue, AllValues allValues)
  {
    allValues.clearCalculatedValues();
    ValueSet targetSet = allValues.getValueSetNonNull(targetSetId);
    targetSet.setCalculatedValueNoOverwrite(targetQuantity, targetValue, getClass().getSimpleName() + " trial Value");
  }

  @ToString
  private static class CalculateDifferenceResult
  {
    @Getter
    private final double difference;

    @Getter
    private double relativeDifference;

    public CalculateDifferenceResult(double value1, double value2)
    {
      difference = value1 - value2;
      if (difference == 0d)
      {
        relativeDifference = 0d;
      }
      else
      {
        double maxAbsoluteValue = Math.max(Math.abs(value1), Math.abs(value2));
        double absoluteDifference = Math.abs(difference);
        relativeDifference = absoluteDifference / maxAbsoluteValue;
      }
    }
  }
}
