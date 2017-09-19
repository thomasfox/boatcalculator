package com.github.thomasfox.sailboatcalculator.calculate;

public class CalculatedPhysicalQuantityValue extends PhysicalQuantityValue
{
  public CalculatedPhysicalQuantityValue(PhysicalQuantity physicalQuantity, double value)
  {
    super(physicalQuantity, value);
  }

  public CalculatedPhysicalQuantityValue(PhysicalQuantityValue toCopy)
  {
    super(toCopy);
  }
}
