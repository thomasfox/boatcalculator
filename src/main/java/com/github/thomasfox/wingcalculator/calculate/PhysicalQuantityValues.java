package com.github.thomasfox.wingcalculator.calculate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class PhysicalQuantityValues
{
  /**
   * Contains the values.
   * Contains at most one value for a given PhysicalQuantity.
   */
  private final List<PhysicalQuantityValue> values = new ArrayList<>();

  public PhysicalQuantityValues(PhysicalQuantityValues toCopy)
  {
    for (PhysicalQuantityValue valueToCopy : toCopy.values)
    {
      values.add(new PhysicalQuantityValue(valueToCopy));
    }
  }

  public void setValuesFailOnOverwrite(PhysicalQuantityValues toSet)
  {
    for (PhysicalQuantityValue valueToCopy : toSet.values)
    {
      setValueNoOverwrite(valueToCopy);
    }
  }

  public void setValue(@NonNull PhysicalQuantityValue toSet)
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

  public void setValueNoOverwrite(@NonNull PhysicalQuantityValue toSet)
  {
    setValueNoOverwrite(toSet.getPhysicalQuantity(), toSet.getValue());
  }

  public void setValueNoOverwrite(@NonNull PhysicalQuantity physicalQuantity, double value)
  {
    PhysicalQuantityValue physicalQuantityValue = getPhysicalQuantityValue(physicalQuantity);
    if (physicalQuantityValue != null)
    {
      throw new IllegalArgumentException("Tried to overwite value for quantity " + physicalQuantity);
    }
    addValue(physicalQuantity, value);
  }

  private void addValue(PhysicalQuantity physicalQuantity, Double value)
  {
    PhysicalQuantityValue physicalQuantityValue;
    physicalQuantityValue = new PhysicalQuantityValue(physicalQuantity);
    physicalQuantityValue.setValue(value);
    values.add(physicalQuantityValue);
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

  public List<PhysicalQuantityValue> getAsList()
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

  public void clear()
  {
    values.clear();
  }

  public boolean isEmpty()
  {
    return values.isEmpty();
  }
}
