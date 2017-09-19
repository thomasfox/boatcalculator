package com.github.thomasfox.sailboatcalculator.calculate.value;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PhysicalQuantityValues extends AbstractPhysicalQuantityValues<PhysicalQuantityValue>
{
  public PhysicalQuantityValues(PhysicalQuantityValues toCopy)
  {
    super(toCopy);
  }

  @Override
  protected PhysicalQuantityValue copy(PhysicalQuantityValue toCopy)
  {
    return new PhysicalQuantityValue(toCopy);
  }

  @Override
  protected PhysicalQuantityValue createEntry(PhysicalQuantity physicalQuantity, Double value)
  {
    return new PhysicalQuantityValue(physicalQuantity, value);
  }
}
