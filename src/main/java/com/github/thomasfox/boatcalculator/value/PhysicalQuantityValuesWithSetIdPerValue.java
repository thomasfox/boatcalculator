package com.github.thomasfox.boatcalculator.value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PhysicalQuantityValuesWithSetIdPerValue
{
  private final List<PhysicalQuantityValueWithSetId> values = new ArrayList<>();

  public PhysicalQuantityValuesWithSetIdPerValue(PhysicalQuantityValuesWithSetIdPerValue toCopy)
  {
    for (PhysicalQuantityValueWithSetId valueToCopy : toCopy.values)
    {
      setValue(valueToCopy);
    }
  }

  private boolean setValue(PhysicalQuantityValueWithSetId toAdd)
  {
    remove(toAdd);
    return values.add((PhysicalQuantityValueWithSetId) toAdd.clone());
  }


  public Double remove(PhysicalQuantityValueWithSetId toRemove)
  {
    Iterator<PhysicalQuantityValueWithSetId> valueIt = values.iterator();
    while (valueIt.hasNext())
    {
      PhysicalQuantityValueWithSetId value = valueIt.next();
      if (value.getPhysicalQuantity().equals(toRemove.getPhysicalQuantity())
          && value.getSetId().equals(toRemove.getSetId()))
      {
        valueIt.remove();
        return value.getValue();
      }
    }
    return null;
  }


  public PhysicalQuantityValuesWithSetIdPerValue(
      AbstractPhysicalQuantityValues<? extends PhysicalQuantityValue> physicalQuantityValues,
      String valueSetId)
  {
    for (PhysicalQuantityValue physicalQuantityValue : physicalQuantityValues.getAsList())
    {
      setValue(new SimplePhysicalQuantityValueWithSetId(physicalQuantityValue, valueSetId));
    }
  }

  public void setValues(PhysicalQuantityValuesWithSetIdPerValue source)
  {
    for (PhysicalQuantityValueWithSetId valueToCopy : source.values)
    {
      setValue(valueToCopy);
    }
  }

  public void setValues(PhysicalQuantityValueWithSetId... source)
  {
    for (PhysicalQuantityValueWithSetId valueToCopy : source)
    {
      setValue(valueToCopy);
    }
  }

  public List<PhysicalQuantityValueWithSetId> getAsList()
  {
    return Collections.unmodifiableList(values);
  }
}
