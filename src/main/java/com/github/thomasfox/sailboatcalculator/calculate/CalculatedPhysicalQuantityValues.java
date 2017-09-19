package com.github.thomasfox.sailboatcalculator.calculate;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CalculatedPhysicalQuantityValues extends AbstractPhysicalQuantityValues<CalculatedPhysicalQuantityValue>
{
  public CalculatedPhysicalQuantityValues(CalculatedPhysicalQuantityValues toCopy)
  {
    super(toCopy);
  }

  @Override
  protected CalculatedPhysicalQuantityValue copy(CalculatedPhysicalQuantityValue toCopy)
  {
    return new CalculatedPhysicalQuantityValue(toCopy);
  }

  @Override
  protected CalculatedPhysicalQuantityValue createEntry(PhysicalQuantity physicalQuantity, Double value)
  {
    return new CalculatedPhysicalQuantityValue(physicalQuantity, value);
  }
}
