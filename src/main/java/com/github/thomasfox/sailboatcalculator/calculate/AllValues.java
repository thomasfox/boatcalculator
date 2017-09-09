package com.github.thomasfox.sailboatcalculator.calculate;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.github.thomasfox.sailboatcalculator.calculate.strategy.ComputationStrategy;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AllValues
{
  private final Set<NamedValueSet> namedValueSets = new LinkedHashSet<>();

  private final Set<ComputationStrategy> computationStrategies = new HashSet<>();

  public AllValues(Set<NamedValueSet> namedValueSets)
  {
    this.namedValueSets.addAll(namedValueSets);
  }

  public AllValues(AllValues toCopy)
  {
    for (NamedValueSet namedValueSetToCopy : toCopy.namedValueSets)
    {
      this.namedValueSets.add(new NamedValueSet(namedValueSetToCopy));
    }
    this.computationStrategies.addAll(toCopy.getComputationStrategies());
  }

  public void add(NamedValueSet toAdd)
  {
    NamedValueSet withSameId = getNamedValueSet(toAdd.getId());
    if (withSameId != null)
    {
      throw new IllegalArgumentException("namedValueSet with id " + withSameId
          + " exists already");
    }
    this.namedValueSets.add(toAdd);
  }

  public Set<NamedValueSet> getNamedValueSets()
  {
    return Collections.unmodifiableSet(namedValueSets);
  }

  public NamedValueSet getNamedValueSet(String id)
  {
    return namedValueSets.stream()
        .filter(n -> n.getId().equals(id))
        .findFirst().orElse(null);
  }

  public NamedValueSet getNamedValueSetNonNull(String id)
  {
    return namedValueSets.stream()
        .filter(n -> n.getId().equals(id))
        .findFirst().orElseThrow(() -> new IllegalStateException("No namedValueSet with id " + id + " exists, existing namedValueSets are " + namedValueSets));
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
      for (NamedValueSet namedValueSet : namedValueSets)
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
    for (NamedValueSet set : namedValueSets)
    {
      set.clearCalculatedValues();
    }
  }

  public void moveCalculatedValuesToStartValues()
  {
    for (NamedValueSet set : namedValueSets)
    {
      set.moveCalculatedValuesToStartValues();
    }
  }
}
