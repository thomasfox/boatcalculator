package com.github.thomasfox.boatcalculator.value;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PhysicalQuantityValuesWithSetIdPerValue extends AbstractPhysicalQuantityValues<PhysicalQuantityValueWithSetId>
{
  public PhysicalQuantityValuesWithSetIdPerValue(PhysicalQuantityValuesWithSetIdPerValue toCopy)
  {
    super(toCopy);
  }

  public PhysicalQuantityValuesWithSetIdPerValue(
      AbstractPhysicalQuantityValues<? extends PhysicalQuantityValue> physicalQuantityValues,
      String valueSetId)
  {
    for (PhysicalQuantityValue physicalQuantityValue : physicalQuantityValues.getAsList())
    {
      setValue(new PhysicalQuantityValueWithSetId(physicalQuantityValue.getPhysicalQuantity(), physicalQuantityValue.getValue(), valueSetId));
    }
  }

  @Override
  protected PhysicalQuantityValueWithSetId createEntry(PhysicalQuantity physicalQuantity, Double value)
  {
    return new PhysicalQuantityValueWithSetId(physicalQuantity, value, null);
  }
}
