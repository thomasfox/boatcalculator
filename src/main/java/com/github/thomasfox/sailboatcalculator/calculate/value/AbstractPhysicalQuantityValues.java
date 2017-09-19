package com.github.thomasfox.sailboatcalculator.calculate.value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@NoArgsConstructor
@ToString
@EqualsAndHashCode
public abstract class AbstractPhysicalQuantityValues<T extends PhysicalQuantityValue>
{
  /**
   * Contains the values.
   * Contains at most one value for a given PhysicalQuantity.
   */
  private final List<T> values = new ArrayList<>();

  protected abstract T copy(T toCopy);

  protected abstract T createEntry(PhysicalQuantity physicalQuantity, Double value);

  public AbstractPhysicalQuantityValues(AbstractPhysicalQuantityValues<? extends T> toCopy)
  {
    for (T valueToCopy : toCopy.values)
    {
      values.add(copy(valueToCopy));
    }
  }

  public void setValuesFailOnOverwrite(@NonNull AbstractPhysicalQuantityValues<? extends T> toSet)
  {
    for (T valueToCopy : toSet.values)
    {
      setValueNoOverwrite(valueToCopy);
    }
  }

  public void setValues(@NonNull AbstractPhysicalQuantityValues<? extends T> toSet)
  {
    for (T valueToCopy : toSet.values)
    {
      setValue(valueToCopy);
    }
  }

  public void setValue(@NonNull T toSet)
  {
    setValue(toSet.getPhysicalQuantity(), toSet.getValue());
  }

  public void setValue(@NonNull PhysicalQuantity physicalQuantity, double value)
  {
    PhysicalQuantityValue physicalQuantityValue = getPhysicalQuantityValue(physicalQuantity);
    if (physicalQuantityValue == null)
    {
      addValue(physicalQuantity, value);
    }
    else
    {
      physicalQuantityValue.setValue(value);
    }
  }

  public void setValueNoOverwrite(@NonNull T toSet)
  {
    setValueNoOverwrite(toSet.getPhysicalQuantity(), toSet.getValue());
  }

  public void setValueNoOverwrite(@NonNull PhysicalQuantity physicalQuantity, double value)
  {
    checkQuantityNotSetForWrite(physicalQuantity);
    addValue(physicalQuantity, value);
  }

  public void checkQuantityNotSetForWrite(PhysicalQuantity physicalQuantity)
  {
    PhysicalQuantityValue physicalQuantityValue = getPhysicalQuantityValue(physicalQuantity);
    if (physicalQuantityValue != null)
    {
      throw new IllegalArgumentException("Tried to overwite value for quantity " + physicalQuantity);
    }
  }

  private void addValue(PhysicalQuantity physicalQuantity, Double value)
  {
    values.add(createEntry(physicalQuantity, value));
  }

  public PhysicalQuantityValue getPhysicalQuantityValue(PhysicalQuantity physicalQuantity)
  {
    return values.stream()
        .filter(v -> v.getPhysicalQuantity().equals(physicalQuantity))
        .findFirst().orElse(null);
  }

  public Double getValue(PhysicalQuantity physicalQuantity)
  {
    PhysicalQuantityValue physicalQuantityValue = getPhysicalQuantityValue(physicalQuantity);
    if (physicalQuantityValue == null)
    {
      return null;
    }
    return physicalQuantityValue.getValue();
  }

  public List<T> getAsList()
  {
    return Collections.unmodifiableList(values);
  }

  public Set<PhysicalQuantity> getContainedQuantities()
  {
    Set<PhysicalQuantity> result = new LinkedHashSet<>();
    for (PhysicalQuantityValue value : values)
    {
      result.add(value.getPhysicalQuantity());
    }
    return result;
  }

  public boolean containsQuantity(PhysicalQuantity toCheck)
  {
    for (PhysicalQuantityValue value : values)
    {
      if (value.getPhysicalQuantity().equals(toCheck))
      {
        return true;
      }
    }
    return false;
  }

  public void clear()
  {
    values.clear();
  }

  public boolean isEmpty()
  {
    return values.isEmpty();
  }

  public Double remove(PhysicalQuantity toRemove)
  {
    Iterator<T> valueIt = values.iterator();
    while (valueIt.hasNext())
    {
      T value = valueIt.next();
      if (value.getPhysicalQuantity().equals(toRemove))
      {
        valueIt.remove();
        return value.getValue();
      }
    }
    return null;
  }
}
