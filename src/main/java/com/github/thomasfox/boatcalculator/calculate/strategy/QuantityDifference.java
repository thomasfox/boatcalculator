package com.github.thomasfox.boatcalculator.calculate.strategy;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.github.thomasfox.boatcalculator.calculate.CalculationResult;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;
import com.github.thomasfox.boatcalculator.valueset.ValuesAndCalculationRules;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * A difference of two values.
 */
@Getter
@ToString
public class QuantityDifference implements ComputationStrategy
{
  @NonNull
  private final PhysicalQuantityInSet target;

  @NonNull
  private final PhysicalQuantityInSet toSubtractFrom;

  @NonNull
  private final PhysicalQuantityInSet toSubtract;

  public QuantityDifference(
      @NonNull PhysicalQuantityInSet target,
      @NonNull PhysicalQuantityInSet toSubtractFrom,
      @NonNull PhysicalQuantityInSet toSubtract)
  {
    this.target = target;
    this.toSubtractFrom = toSubtractFrom;
    this.toSubtract = toSubtract;
    String targetUnit = target.getPhysicalQuantity().getUnit();
    if (!Objects.equals(toSubtractFrom.getPhysicalQuantity().getUnit(), targetUnit))
    {
      throw new IllegalArgumentException("toSubtractFrom " + toSubtractFrom.getPhysicalQuantity().getDescription()
          + " has wrong unit, must be equal to unit of target " + target.getPhysicalQuantity().getDescription());
    }
    if (!Objects.equals(toSubtract.getPhysicalQuantity().getUnit(), targetUnit))
    {
      throw new IllegalArgumentException("toSubtract " + toSubtract.getPhysicalQuantity().getDescription()
          + " has wrong unit, must be equal to unit of target " + target.getPhysicalQuantity().getDescription());
    }
  }

  public boolean allSourceValuesAreKnown(ValuesAndCalculationRules allValues)
  {
    return allValues.isValueKnown(toSubtractFrom) && allValues.isValueKnown(toSubtract);
  }

  public CalculationResult getDifferenceOfSourceValues(ValuesAndCalculationRules allValues)
  {
    double newValue = 0d;
    boolean trialValue = false;

    ValueSet sourceSet = allValues.getValueSetNonNull(toSubtractFrom.getSetId());
    PhysicalQuantityValue knownValue = sourceSet.getKnownQuantityValue(toSubtractFrom.getPhysicalQuantity());
    newValue += knownValue.getValue();
    trialValue |= knownValue.isTrial();

    sourceSet = allValues.getValueSetNonNull(toSubtract.getSetId());
    knownValue = sourceSet.getKnownQuantityValue(toSubtract.getPhysicalQuantity());
    newValue -= knownValue.getValue();
    trialValue |= knownValue.isTrial();

    Double lastValue = allValues.getKnownValue(target);

    CalculationResult result = new CalculationResult(newValue, lastValue, trialValue);
    lastValue = newValue;
    return result;
  }

  public String getCalculatedByDescription(ValuesAndCalculationRules allValues)
  {
    StringBuilder result = new StringBuilder();
    result.append(allValues.getNameOfSetWithId(toSubtractFrom.getSetId()))
        .append(":")
        .append(toSubtractFrom.getPhysicalQuantity().getDisplayName())
        .append(" - ")
        .append(allValues.getNameOfSetWithId(toSubtract.getSetId()))
        .append(":")
        .append(toSubtract.getPhysicalQuantity().getDisplayName());
    return result.toString();
  }

  private PhysicalQuantityValueWithSetId[] getSourceValuesWithNames(ValuesAndCalculationRules allValues)
  {
  PhysicalQuantityValueWithSetId[] result = new PhysicalQuantityValueWithSetId[2];
    ValueSet sourceSet = allValues.getValueSet(toSubtractFrom.getSetId());
    result[0] = new SimplePhysicalQuantityValueWithSetId(
        allValues.getKnownPhysicalQuantityValue(toSubtractFrom),
        sourceSet.getId());
    sourceSet = allValues.getValueSet(toSubtract.getSetId());
    result[1] = new SimplePhysicalQuantityValueWithSetId(
        allValues.getKnownPhysicalQuantityValue(toSubtract),
        sourceSet.getId());
    return result;
  }

  @Override
  public boolean step(ValuesAndCalculationRules allValues)
  {
    ValueSet targetSet = allValues.getValueSetNonNull(target.getSetId());
    PhysicalQuantityValue knownValue = targetSet.getKnownQuantityValue(target.getPhysicalQuantity());
    if (allSourceValuesAreKnown(allValues)  && (knownValue == null || knownValue.isTrial()))
    {
      CalculationResult calculationResult = getDifferenceOfSourceValues(allValues);
      targetSet.setCalculatedValueNoOverwrite(
          new SimplePhysicalQuantityValue(target.getPhysicalQuantity(), calculationResult.getValue()),
          getCalculatedByDescription(allValues),
          calculationResult.isTrial(),
          getSourceValuesWithNames(allValues));
      return !calculationResult.relativeDifferenceIsBelowThreshold();
    }
    return false;
  }

  @Override
  public Set<PhysicalQuantityInSet> getOutputs()
  {
    Set<PhysicalQuantityInSet> result = new HashSet<>();
    result.add(target);
    return result;
  }

  @Override
  public Set<PhysicalQuantityInSet> getInputs()
  {
    Set<PhysicalQuantityInSet> result = new HashSet<>();
    result.add(toSubtractFrom);
    result.add(toSubtract);
    return result;
  }
}
