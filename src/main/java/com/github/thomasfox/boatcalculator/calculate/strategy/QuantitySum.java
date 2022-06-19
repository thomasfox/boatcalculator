package com.github.thomasfox.boatcalculator.calculate.strategy;

import java.util.Arrays;
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
 * A sum of values. One quantity is known to be the sum of other quantities.
 * Optionally, factors can be applied to the parts of the sum.
 */
@Getter
@ToString
public class QuantitySum implements StepComputationStrategy
{
  @NonNull
  private final PhysicalQuantityInSet target;

  @NonNull
  private final PhysicalQuantityInSet[] sources;

  public QuantitySum(@NonNull PhysicalQuantityInSet target, @NonNull PhysicalQuantityInSet... sources)
  {
    this.target = target;
    this.sources = sources;
    String targetUnit = target.getPhysicalQuantity().getUnit();
    for (PhysicalQuantityInSet source : sources)
    {
      if (!Objects.equals(source.getPhysicalQuantity().getUnit(), targetUnit))
      {
        throw new IllegalArgumentException("Source " + source.getPhysicalQuantity().getDescription()
            + " has wrong unit, must be equal to unit of target " + target.getPhysicalQuantity().getDescription());
      }
    }
  }

  private boolean allSourceValuesAreKnown(ValuesAndCalculationRules allValues)
  {
    return Arrays.stream(sources).allMatch(allValues::isValueKnown);
  }

  private CalculationResult getSumOfSourceValues(ValuesAndCalculationRules allValues)
  {
    double newValue = 0d;
    boolean trialValue = false;
    for (PhysicalQuantityInSet source : sources)
    {
      ValueSet sourceSet = allValues.getValueSetNonNull(source.getSetId());
      PhysicalQuantityValue knownValue = sourceSet.getKnownQuantityValue(source.getPhysicalQuantity());
      newValue += knownValue.getValue();
      trialValue |= knownValue.isTrial();
    }
    Double lastValue = allValues.getKnownValue(target);

    CalculationResult result = new CalculationResult(newValue, lastValue, trialValue);
    lastValue = newValue;
    return result;
  }

  private String getCalculatedByDescription(ValuesAndCalculationRules allValues)
  {
    StringBuilder result = new StringBuilder();
    for (PhysicalQuantityInSet source : sources)
    {
      if (result.length() > 0 )
      {
        result.append(" + ");
      }
      result.append(allValues.getNameOfSetWithId(source.getSetId()))
          .append(":")
          .append(source.getPhysicalQuantity().getDisplayName());
    }
    return result.toString();
  }

  private PhysicalQuantityValueWithSetId[] getSourceValuesWithNames(ValuesAndCalculationRules allValues)
  {
    PhysicalQuantityValueWithSetId[] result = new PhysicalQuantityValueWithSetId[sources.length];
    int i = 0;
    for (PhysicalQuantityInSet source : sources)
    {
      ValueSet sourceSet = allValues.getValueSet(source.getSetId());
      result[i] = new SimplePhysicalQuantityValueWithSetId(
          allValues.getKnownPhysicalQuantityValue(source),
          sourceSet.getId());
      ++i;
    }
    return result;
  }

  @Override
  public boolean step(ValuesAndCalculationRules allValues)
  {
    ValueSet targetSet = allValues.getValueSetNonNull(target.getSetId());
    PhysicalQuantityValue knownValue = targetSet.getKnownQuantityValue(target.getPhysicalQuantity());
    if (allSourceValuesAreKnown(allValues)  && (knownValue == null || knownValue.isTrial()))
    {
      CalculationResult calculationResult = getSumOfSourceValues(allValues);
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
    for (PhysicalQuantityInSet source : sources)
    {
      result.add(source);
    }
    return result;
  }
}
