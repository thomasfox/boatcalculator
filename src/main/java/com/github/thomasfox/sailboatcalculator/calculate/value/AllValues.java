package com.github.thomasfox.sailboatcalculator.calculate.value;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.thomasfox.sailboatcalculator.calculate.strategy.ComputationStrategy;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AllValues
{
  private final Set<SimpleValueSet> valueSets = new LinkedHashSet<>();

  private final Set<ComputationStrategy> computationStrategies = new LinkedHashSet<>();

  public AllValues(Set<SimpleValueSet> valueSets)
  {
    this.valueSets.addAll(valueSets);
  }

  public AllValues(AllValues toCopy)
  {
    for (SimpleValueSet valueSetToCopy : toCopy.valueSets)
    {
      this.valueSets.add(new SimpleValueSet(valueSetToCopy));
    }
    this.computationStrategies.addAll(toCopy.getComputationStrategies());
  }

  public void add(SimpleValueSet toAdd)
  {
    ValueSet withSameId = getValueSet(toAdd.getId());
    if (withSameId != null)
    {
      throw new IllegalArgumentException("valueSet with id " + withSameId
          + " exists already");
    }
    this.valueSets.add(toAdd);
  }

  public Set<ValueSet> getValueSets()
  {
    return Collections.unmodifiableSet(valueSets);
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
    ValueSet sourceSet = getValueSetNonNull(toResolve.getValueSetId());
    if (sourceSet == null)
    {
      return null;
    }
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
    return getValueSetNonNull(toBeNamed.getValueSetId()).getName();
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
        target.getPhysicalQuantity(),
        value,
        calculatedBy,
        calculatedFrom);
  }

  public String getNameOfSetWithId(String setId)
  {
    return valueSets.stream()
        .filter(n -> n.getId().equals(setId))
        .map(s -> s.getName())
        .findAny().orElseThrow(() -> new IllegalStateException(
            "No valueSet with id " + setId + " exists, existing valueSets are "
                + valueSets.stream().map(ValueSet::getId).collect(Collectors.toList())));
  }

  public void add(ComputationStrategy computationStrategy)
  {
    computationStrategies.add(computationStrategy);
  }

  public Set<ComputationStrategy> getComputationStrategies()
  {
    return Collections.unmodifiableSet(computationStrategies);
  }

  public void calculate()
  {
    boolean changed;
    int cutoff = 100;
    do
    {
      changed = false;
      for (ValueSet valueSet : valueSets)
      {
        boolean partChanged = valueSet.calculateSinglePass(this);
        changed = changed || partChanged;
      }
      boolean changedByComputationStrategies = applyComputationStrategies();
      changed = changed || changedByComputationStrategies;
      cutoff--;
    }
    while (changed && cutoff > 0);
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
        System.out.println("Calculation path for " + set.getId() + ":" + calculatedValue.getPhysicalQuantity().getDisplayName());
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
          = (CalculatedPhysicalQuantityValue) set.getCalculatedValues().getPhysicalQuantityValue(sourceQuantity.getPhysicalQuantity());
      if (calculatedFrom != null)
      {
        System.out.println(indent + sourceQuantity.getSetId() + ":" + sourceQuantity.getPhysicalQuantity().getDisplayName());
        printComputationPath(calculatedFrom, indent + "  ");
      }
    }
  }
}
