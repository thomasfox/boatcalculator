package com.github.thomasfox.sailboatcalculator.calculate.value;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

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
    for (SimpleValueSet namedValueSetToCopy : toCopy.valueSets)
    {
      this.valueSets.add(new SimpleValueSet(namedValueSetToCopy));
    }
    this.computationStrategies.addAll(toCopy.getComputationStrategies());
  }

  public void add(SimpleValueSet toAdd)
  {
    ValueSet withSameId = getNamedValueSet(toAdd.getId());
    if (withSameId != null)
    {
      throw new IllegalArgumentException("namedValueSet with id " + withSameId
          + " exists already");
    }
    this.valueSets.add(toAdd);
  }

  public Set<ValueSet> getValueSets()
  {
    return Collections.unmodifiableSet(valueSets);
  }

  public ValueSet getNamedValueSet(String id)
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
            "No namedValueSet with id " + id + " exists, existing namedValueSets are " + valueSets));
  }

  public Double getKnownValue(PhysicalQuantityInSet toResolve)
  {
    ValueSet sourceSet = getValueSetNonNull(toResolve.getNamedValueSetId());
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

  public String getNameOfSetWithId(String setId)
  {
    return valueSets.stream()
        .filter(n -> n.getId().equals(setId))
        .map(s -> s.getName())
        .findAny().orElseThrow(() -> new IllegalStateException(
            "No namedValueSet with id " + setId + " exists, existing namedValueSets are " + valueSets));
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
      for (ValueSet namedValueSet : valueSets)
      {
        boolean partChanged = namedValueSet.calculateSinglePass(this);
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
}
