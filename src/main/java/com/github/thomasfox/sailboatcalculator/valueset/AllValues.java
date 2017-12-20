package com.github.thomasfox.sailboatcalculator.valueset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.strategy.ComputationStrategy;
import com.github.thomasfox.sailboatcalculator.value.CalculatedPhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.sailboatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.value.PhysicalQuantityValueWithSetId;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Contains all valueSets for a boat, and the strategies
 * which relate quantities in different valueSets.
 */
@NoArgsConstructor
@Slf4j
public class AllValues
{
  private final List<ValueSet> valueSets = new ArrayList<>();

  private final List<ComputationStrategy> computationStrategies = new ArrayList<>();

  public AllValues(Set<ValueSet> valueSets)
  {
    this.valueSets.addAll(valueSets);
  }

  public AllValues(AllValues toCopy)
  {
    for (ValueSet valueSetToCopy : toCopy.valueSets)
    {
      this.valueSets.add(valueSetToCopy.clone());
    }
    this.computationStrategies.addAll(toCopy.getComputationStrategies());
  }

  public void add(ValueSet toAdd)
  {
    ValueSet withSameId = getValueSet(toAdd.getId());
    if (withSameId != null)
    {
      throw new IllegalArgumentException("valueSet with id " + withSameId
          + " exists already");
    }
    this.valueSets.add(toAdd);
  }

  public List<ValueSet> getValueSets()
  {
    return Collections.unmodifiableList(valueSets);
  }

  public boolean remove(ValueSet toRemove)
  {
    return valueSets.remove(toRemove);
  }

  public ValueSet getValueSet(String id)
  {
    return valueSets.stream()
        .filter(n -> n.getId().equals(id))
        .findAny().orElse(null);
  }

  public ValueSet getValueSetNonNull(String id)
  {
    return valueSets.stream()
        .filter(n -> n.getId().equals(id))
        .findAny().orElseThrow(() -> new IllegalStateException(
            "No valueSet with id " + id + " exists, existing valueSets are "
                + valueSets.stream().map(ValueSet::getId).collect(Collectors.toList())));
  }

  public Double getKnownValue(PhysicalQuantityInSet toResolve)
  {
    if (toResolve == null)
    {
      return null;
    }
    ValueSet sourceSet = getValueSetNonNull(toResolve.getValueSetId());
    PhysicalQuantityValue knownValue = sourceSet.getKnownValue(toResolve.getPhysicalQuantity());
    if (knownValue == null)
    {
      return null;
    }
    return knownValue.getValue();
  }

  public String getName(PhysicalQuantityInSet toBeNamed)
  {
    return getSetName(toBeNamed) + ":" + toBeNamed.getPhysicalQuantity().getDisplayName();
  }

  public String getSetName(PhysicalQuantityInSet toBeNamed)
  {
    return getValueSetNonNull(toBeNamed.getValueSetId()).getDisplayName();
  }

  public boolean isValueKnown(PhysicalQuantityInSet toCheck)
  {
    Double value = getKnownValue(toCheck);
    return value != null;
  }

  public void setCalculatedValueNoOverwrite(
      PhysicalQuantityInSet target,
      double value,
      String calculatedBy,
      PhysicalQuantityValueWithSetId... calculatedFrom)
  {
    ValueSet targetSet = getValueSetNonNull(target.getValueSetId());
    targetSet.setCalculatedValueNoOverwrite(
        new PhysicalQuantityValue(target.getPhysicalQuantity(), value),
        calculatedBy,
        calculatedFrom);
  }

  public String getNameOfSetWithId(String setId)
  {
    return valueSets.stream()
        .filter(n -> n.getId().equals(setId))
        .map(s -> s.getDisplayName())
        .findAny().orElseThrow(() -> new IllegalStateException(
            "No valueSet with id " + setId + " exists, existing valueSets are "
                + valueSets.stream().map(ValueSet::getId).collect(Collectors.toList())));
  }

  public void add(ComputationStrategy computationStrategy)
  {
    computationStrategies.add(computationStrategy);
  }

  public List<ComputationStrategy> getComputationStrategies()
  {
    return Collections.unmodifiableList(computationStrategies);
  }

  public boolean remove(ComputationStrategy computationStrategy)
  {
    return computationStrategies.remove(computationStrategy);
  }

  public void calculate(PhysicalQuantityInSet wanted)
  {
    if (wanted != null)
    {
      ValueSet valueSet = getValueSet(wanted.getValueSetId());
      {
        valueSet.calculateSinglePass(this, wanted.getPhysicalQuantity());
      }
      if (valueSet.isValueKnown(wanted.getPhysicalQuantity()))
      {
        return;
      }
    }
    boolean changed;
    int cutoff = 100;
    do
    {
      changed = false;
      for (ValueSet valueSet : valueSets)
      {
        PhysicalQuantity wantedPhysicalQuantity = null;
        if (wanted != null && wanted.getValueSetId().equals(valueSet.getId()))
        {
          wantedPhysicalQuantity = wanted.getPhysicalQuantity();
        }
        boolean partChanged = valueSet.calculateSinglePass(this, wantedPhysicalQuantity);
        changed = changed || partChanged;
      }
      boolean changedByComputationStrategies = applyComputationStrategies();
      changed = changed || changedByComputationStrategies;
      cutoff--;
    }
    while (changed && cutoff > 0 && !isValueKnown(wanted));
  }

  private boolean applyComputationStrategies()
  {
    boolean changed = false;
    for (ComputationStrategy computationStrategy : computationStrategies)
    {
      changed = changed || computationStrategy.setValue(this);
    }
    return changed;
  }

  public void clearCalculatedValues()
  {
    for (ValueSet set : valueSets)
    {
      set.clearCalculatedValues();
    }
  }

  public void moveCalculatedValuesToStartValues()
  {
    for (ValueSet set : valueSets)
    {
      set.moveCalculatedValuesToStartValues();
    }
  }

  public void printLongestComputationPaths()
  {
    Set<PhysicalQuantityValueWithSetId> excluded = new HashSet<>();
    for (ValueSet set : valueSets)
    {
      for (CalculatedPhysicalQuantityValue calculatedValue : set.getCalculatedValues().getAsList())
      {
        for (PhysicalQuantityValueWithSetId sourceQuantity : calculatedValue.getCalculatedFromAsList())
        {
          excluded.add(sourceQuantity);
        }
      }
    }

    for (ValueSet set : valueSets)
    {
      for (CalculatedPhysicalQuantityValue calculatedValue : set.getCalculatedValues().getAsList())
      {
        if (excluded.contains(calculatedValue))
        {
          continue;
        }
        log.info("Calculation path for " + set.getId() + ":" + calculatedValue.getPhysicalQuantity().getDisplayName());
        printComputationPath(calculatedValue, "  ");
      }
    }
  }

  public void printComputationPath(CalculatedPhysicalQuantityValue value, String indent)
  {
    for (PhysicalQuantityValueWithSetId sourceQuantity : value.getCalculatedFromAsList())
    {
      ValueSet set = getValueSetNonNull(sourceQuantity.getSetId());
      CalculatedPhysicalQuantityValue calculatedFrom
          = set.getCalculatedValues().getPhysicalQuantityValue(sourceQuantity.getPhysicalQuantity());
      if (calculatedFrom != null)
      {
        log.info(indent + sourceQuantity.getSetId() + ":" + sourceQuantity.getPhysicalQuantity().getDisplayName());
        printComputationPath(calculatedFrom, indent + "  ");
      }
    }
  }

  public void logState()
  {
    for (ValueSet valueSet : getValueSets())
    {
      for (PhysicalQuantityValue value : valueSet.getStartValues().getAsList())
      {
        log.info("  Start value: "+ valueSet.getId() + ":" + value.getPhysicalQuantity().getDisplayName() + "=" + value.getValue());
      }
    }
    for (ValueSet valueSet : getValueSets())
    {
      for (PhysicalQuantityValue value : valueSet.getCalculatedValues().getAsList())
      {
        log.info("  Calculated value: "+ valueSet.getId() + ":" + value.getPhysicalQuantity().getDisplayName() + "=" + value.getValue());
      }
    }
  }
}
