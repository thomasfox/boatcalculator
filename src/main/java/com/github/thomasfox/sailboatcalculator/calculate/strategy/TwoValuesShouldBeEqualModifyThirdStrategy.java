package com.github.thomasfox.sailboatcalculator.calculate.strategy;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.AllValues;
import com.github.thomasfox.sailboatcalculator.calculate.value.NamedValueSet;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class TwoValuesShouldBeEqualModifyThirdStrategy implements ComputationStrategy
{
  private static double CUTOFF = 1E-4;

  private final PhysicalQuantity equalQuantity1;

  private final String equalQuantity1SetId;

  private final PhysicalQuantity equalQuantity2;

  private final String equalQuantity2SetId;

  private final PhysicalQuantity targetQuantity;

  private final String targetSetId;

  double lowerCutoff;

  double upperCutoff;

  @Override
  public boolean setValue(AllValues allValues)
  {
    NamedValueSet targetSet = allValues.getNamedValueSetNonNull(targetSetId);
    PhysicalQuantityValue knownTargetValue = targetSet.getKnownValue(targetQuantity);
    if (knownTargetValue != null)
    {
      return false;
    }

    double targetValue1 = lowerCutoff;
    double targetValue2 = upperCutoff;
    AllValues allValuesForCalculation = new AllValues(allValues);
    allValuesForCalculation.moveCalculatedValuesToStartValues();
    CalculateDifferenceResult difference1 = calculateDifference(targetValue1, allValuesForCalculation);
    CalculateDifferenceResult difference2 = calculateDifference(targetValue2, allValuesForCalculation);
    if (difference1 == null || difference2 == null)
    {
      return false;
    }
    boolean changed = applyAndRecalculateWithPoints(targetValue1, difference1, targetValue2, difference2, allValuesForCalculation, 100);
    if (changed)
    {
      double targetValue = allValuesForCalculation.getNamedValueSetNonNull(targetSetId).getKnownValue(targetQuantity).getValue();
      targetSet.setCalculatedValueNoOverwrite(targetQuantity, targetValue, getClass().getSimpleName());
    }
    return changed;
  }

  private boolean applyAndRecalculateWithPoints(
      double targetValue1,
      CalculateDifferenceResult difference1,
      double targetValue2,
      CalculateDifferenceResult difference2,
      AllValues allValues,
      int maxTries)
  {
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
    System.out.println("Try value " + estimatedTarget + " for target Quantity " + targetQuantity + ", maxTries = " + maxTries);
    if (estimatedTargetDifference == null)
    {
      throw new RuntimeException("Could not calculate Difference between "
          + equalQuantity1 + " in " + equalQuantity1SetId
          + " and " + equalQuantity2 + " in " + equalQuantity2SetId
          + " for target Quantity " + targetQuantity + " with value " + estimatedTarget);
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
    clearComputedValuesAndSetTargetValue(targetValue, allValues);
    allValues.calculate();
    NamedValueSet equalQuantity1Set = allValues.getNamedValueSetNonNull(equalQuantity1SetId);
    NamedValueSet equalQuantity2Set = allValues.getNamedValueSetNonNull(equalQuantity2SetId);
    PhysicalQuantityValue value1 =  equalQuantity1Set.getKnownValue(equalQuantity1);
    PhysicalQuantityValue value2 =  equalQuantity2Set.getKnownValue(equalQuantity2);
    if (value1 == null || value2 == null)
    {
      return null;
    }
    return new CalculateDifferenceResult(value1.getValue(), value2.getValue());
  }

  private void clearComputedValuesAndSetTargetValue(double targetValue, AllValues allValues)
  {
    allValues.clearCalculatedValues();
    NamedValueSet targetSet = allValues.getNamedValueSetNonNull(targetSetId);
    targetSet.setCalculatedValue(targetQuantity, targetValue, getClass().getSimpleName() + " trial Value");
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
