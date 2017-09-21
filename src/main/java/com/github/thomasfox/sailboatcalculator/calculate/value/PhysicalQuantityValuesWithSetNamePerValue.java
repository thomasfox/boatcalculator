package com.github.thomasfox.sailboatcalculator.calculate.value;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PhysicalQuantityValuesWithSetNamePerValue extends AbstractPhysicalQuantityValues<PhysicalQuantityValueWithSetName>
{
  public PhysicalQuantityValuesWithSetNamePerValue(PhysicalQuantityValuesWithSetNamePerValue toCopy)
  {
    super(toCopy);
  }

  public PhysicalQuantityValuesWithSetNamePerValue(
      AbstractPhysicalQuantityValues<? extends PhysicalQuantityValue> physicalQuantityValues,
      String valueSetName)
  {
    for (PhysicalQuantityValue physicalQuantityValue : physicalQuantityValues.getAsList())
    {
      setValue(new PhysicalQuantityValueWithSetName(physicalQuantityValue.getPhysicalQuantity(), physicalQuantityValue.getValue(), valueSetName));
    }
  }

  @Override
  protected PhysicalQuantityValueWithSetName copy(PhysicalQuantityValueWithSetName toCopy)
  {
    return new PhysicalQuantityValueWithSetName(toCopy);
  }

  @Override
  protected PhysicalQuantityValueWithSetName createEntry(PhysicalQuantity physicalQuantity, Double value)
  {
    return new PhysicalQuantityValueWithSetName(physicalQuantity, value, null);
  }
}
