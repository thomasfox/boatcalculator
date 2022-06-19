package com.github.thomasfox.boatcalculator.calculate.strategy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.github.thomasfox.boatcalculator.calculate.CalculationResult;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;
import com.github.thomasfox.boatcalculator.valueset.ValuesAndCalculationRules;
import com.github.thomasfox.boatcalculator.valueset.impl.Hull;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * Lift of a foil is calculated by limiting it by an associated weight.
 * If the lift exceeds the weight, the angle of attack is reduced.
 * If the lift is smaller than the weight, the angle of attack is increased
 * to a limit.
 * If the limit for the angle of attack is reached, the remaining weight
 * is set for the hull.
 *
 * So this strategy sets two values, hull weight and angle of attack of the
 * main lifting foil.
 */
@Getter
@ToString
public class LiftByAngleOfAttackStrategy implements StepComputationStrategy
{
  @NonNull
  private final PhysicalQuantityInSet[] weightSources;

  @NonNull
  private final PhysicalQuantityInSet[] liftSources;

  @NonNull
  private final PhysicalQuantityInSet[] anglesOfAttack;

  @NonNull
  private final PhysicalQuantityInSet maxAngleOfAttack;

  private double factor = 2;

  private Double lastTrialValueDifference;

  public LiftByAngleOfAttackStrategy(
      @NonNull PhysicalQuantityInSet[] weightSources,
      @NonNull PhysicalQuantityInSet[] liftSources,
      @NonNull PhysicalQuantityInSet[] anglesOfAttack,
      @NonNull PhysicalQuantityInSet maxAngleOfAttack)
  {
    this.weightSources = weightSources;
    this.liftSources = liftSources;
    this.anglesOfAttack = anglesOfAttack;
    this.maxAngleOfAttack = maxAngleOfAttack;
    checkUnitsOfWeightSources();
    checkUnitsOfLiftSources();
  }

  private void checkUnitsOfWeightSources()
  {
    String targetUnit = PhysicalQuantity.WEIGHT.getUnit();
    for (PhysicalQuantityInSet source : weightSources)
    {
      if (!Objects.equals(source.getPhysicalQuantity().getUnit(), targetUnit))
      {
        throw new IllegalArgumentException(
            "Source " + source.getPhysicalQuantity().getDescription()
              + " has wrong unit, must be equal to " + targetUnit);
      }
    }
  }

  private void checkUnitsOfLiftSources()
  {
    String targetUnit = PhysicalQuantity.LIFT.getUnit();
    for (PhysicalQuantityInSet source : liftSources)
    {
      if (!Objects.equals(source.getPhysicalQuantity().getUnit(), targetUnit))
      {
        throw new IllegalArgumentException(
            "Source " + source.getPhysicalQuantity().getDescription()
              + " has wrong unit, must be equal to " + targetUnit);
      }
    }
  }

  private String getCalculatedByDescription(ValuesAndCalculationRules allValues)
  {
    StringBuilder result = new StringBuilder("LiftByAngleOfAttackStrategy: ");
    for (PhysicalQuantityInSet source : weightSources)
    {
      if (result.length() > 0 )
      {
        result.append(",");
      }
      result.append(allValues.getNameOfSetWithId(source.getSetId()))
          .append(":")
          .append(source.getPhysicalQuantity().getDisplayName());
    }
    return result.toString();
  }

  private PhysicalQuantityValueWithSetId[] getWeightSourceValuesWithSetId(ValuesAndCalculationRules allValues)
  {
    PhysicalQuantityValueWithSetId[] result = new PhysicalQuantityValueWithSetId[weightSources.length];
    int i = 0;
    for (PhysicalQuantityInSet source : weightSources)
    {
      result[i] = new SimplePhysicalQuantityValueWithSetId(
          allValues.getKnownPhysicalQuantityValue(source),
          source.getSetId());
      ++i;
    }
    return result;
  }

  private PhysicalQuantityValueWithSetId[] getAngleOfAttackSourceValuesWithSetId(ValuesAndCalculationRules allValues)
  {
    PhysicalQuantityValueWithSetId[] result = new PhysicalQuantityValueWithSetId[anglesOfAttack.length];
    int i = 0;
    for (PhysicalQuantityInSet source : anglesOfAttack)
    {
      result[i] = new SimplePhysicalQuantityValueWithSetId(
          allValues.getKnownPhysicalQuantityValue(source),
          source.getSetId());
      ++i;
    }
    return result;
  }

  private PhysicalQuantityValueWithSetId[] getAllSourceValuesWithSetId(ValuesAndCalculationRules allValues)
  {
    PhysicalQuantityValueWithSetId[] weightArray = getWeightSourceValuesWithSetId(allValues);
    PhysicalQuantityValueWithSetId[] angleOfAttackArray = getAngleOfAttackSourceValuesWithSetId(allValues);
    PhysicalQuantityValueWithSetId[] result
        = Arrays.copyOf(weightArray, weightArray.length + angleOfAttackArray.length);
    System.arraycopy(angleOfAttackArray, 0, result, weightArray.length, angleOfAttackArray.length);
    return result;
  }

  @Override
  public boolean step(ValuesAndCalculationRules allValues)
  {
    if (!allWeightSourceValuesAreKnown(allValues))
    {
      factor = 2;
      lastTrialValueDifference = null;
      return false;
    }

    Double maxAngleOfAttackValue = allValues.getKnownValue(maxAngleOfAttack);
    if (maxAngleOfAttackValue == null)
    {
      return false;
    }
    Double oldAngleOfAttackValue = maxAngleOfAttackValue;

    for (PhysicalQuantityInSet angleOfAttackQuantity : anglesOfAttack)
    {
      ValueSet targetSet = allValues.getValueSetNonNull(angleOfAttackQuantity.getSetId());
      PhysicalQuantityValue knownTargetValue = targetSet.getKnownQuantityValue(angleOfAttackQuantity.getPhysicalQuantity());
      if (knownTargetValue == null)
      {
        targetSet.setCalculatedValueNoOverwrite(
            new SimplePhysicalQuantityValue(angleOfAttackQuantity.getPhysicalQuantity(), oldAngleOfAttackValue),
            getClass().getSimpleName() + " trial Value",
            true,
            getWeightSourceValuesWithSetId(allValues));
        knownTargetValue = targetSet.getKnownQuantityValue(angleOfAttackQuantity.getPhysicalQuantity());
      }
      else
      {
        oldAngleOfAttackValue = knownTargetValue.getValue();
      }

      if (!knownTargetValue.isTrial())
      {
        // targetValue is no trial value and thus already calculated
        return false;
      }
    }

    if (!allLiftSourceValuesAreKnown(allValues))
    {
      for (PhysicalQuantityInSet angleOfAttackQuantity : anglesOfAttack)
      {

        allValues.setCalculatedValueNoOverwrite(
            angleOfAttackQuantity,
            oldAngleOfAttackValue,
            getClass().getSimpleName() + " trial Value",
            true,
            getAllSourceValuesWithSetId(allValues));
      }
      factor = 2;
      lastTrialValueDifference = null;
      return true;
    }

    double calculatedLiftSum = getSumOfLiftSourcesValues(allValues);

    double weight = getSumOfWeightSourcesValues(allValues);

    CalculationResult liftWeightDifference = new CalculationResult(calculatedLiftSum, weight , true);
    boolean converged = !liftWeightDifference.relativeDifferenceIsBelowThreshold();

    double hullLift = weight - calculatedLiftSum;
    if (hullLift < 0)
    {
      hullLift = 0;
    }
    allValues.setCalculatedValueNoOverwrite(
        new PhysicalQuantityInSet(PhysicalQuantity.LIFT, Hull.ID),
        hullLift,
        getCalculatedByDescription(allValues),
        true,
        getWeightSourceValuesWithSetId(allValues));

    double trialValueDifference = -factor * (calculatedLiftSum - weight) / weight;
    if (lastTrialValueDifference != null)
    {
      if (trialValueDifference*lastTrialValueDifference < 0d // they have different sign
        && Math.abs(trialValueDifference) > Math.abs(lastTrialValueDifference) * 0.9)
      {
        trialValueDifference = trialValueDifference * 0.5;
        factor *= 0.5;
      }
    }
    lastTrialValueDifference = trialValueDifference;
    double newAngleOfAttackValue = oldAngleOfAttackValue + trialValueDifference;

    if (newAngleOfAttackValue > maxAngleOfAttackValue)
    {
      newAngleOfAttackValue = maxAngleOfAttackValue;
      converged = oldAngleOfAttackValue == maxAngleOfAttackValue;
    }

    for (PhysicalQuantityInSet angleOfAttackQuantity : anglesOfAttack)
    {
      allValues.setCalculatedValueNoOverwrite(
          angleOfAttackQuantity,
          newAngleOfAttackValue,
          getClass().getSimpleName() + " trial Value",
          true,
          getWeightSourceValuesWithSetId(allValues));
    }

    return converged;
  }

  private boolean allWeightSourceValuesAreKnown(ValuesAndCalculationRules allValues)
  {
    return Arrays.stream(weightSources).allMatch(allValues::isValueKnown);
  }

  private boolean allLiftSourceValuesAreKnown(ValuesAndCalculationRules allValues)
  {
    return Arrays.stream(liftSources).allMatch(allValues::isValueKnown);
  }

  private double getSumOfWeightSourcesValues(ValuesAndCalculationRules allValues)
  {
    double result = 0d;
    for (PhysicalQuantityInSet source : weightSources)
    {
      result += allValues.getKnownValue(source);
    }
    return result;
  }

  private double getSumOfLiftSourcesValues(ValuesAndCalculationRules allValues)
  {
    double result = 0d;
    for (PhysicalQuantityInSet source : liftSources)
    {
      result += allValues.getKnownValue(source);
    }
    return result;
  }

  @Override
  public Set<PhysicalQuantityInSet> getOutputs()
  {
    Set<PhysicalQuantityInSet> result = new HashSet<>();
    for (PhysicalQuantityInSet angleOfAttack : anglesOfAttack)
    {
      result.add(angleOfAttack);
    }
    for (PhysicalQuantityInSet liftSource : liftSources)
    {
      result.add(liftSource);
    }
    return result;
  }

  @Override
  public Set<PhysicalQuantityInSet> getInputs()
  {
    Set<PhysicalQuantityInSet> result = new HashSet<>();
    for (PhysicalQuantityInSet weightSource : weightSources)
    {
      result.add(weightSource);
    }
    return result;
  }
}
