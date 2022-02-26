package com.github.thomasfox.boatcalculator.value;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PhysicalQuantityValues extends AbstractPhysicalQuantityValues<PhysicalQuantityValue>
{
  public PhysicalQuantityValues(PhysicalQuantityValues toCopy)
  {
    super(toCopy);
  }

  @Override
  protected PhysicalQuantityValue createEntry(PhysicalQuantity physicalQuantity, Double value)
  {
    return new SimplePhysicalQuantityValue(physicalQuantity, value);
  }

  @Override
  public String toString()
  {
    StringBuilder result = new StringBuilder();
    for (PhysicalQuantityValue value : getAsList())
    {
      if (result.length() > 0)
      {
        result.append(", ");
      }
      result.append(value.getPhysicalQuantity()).append("=").append(value.getValue());
    }
    return result.toString();
  }
}
