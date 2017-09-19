package com.github.thomasfox.sailboatcalculator.calculate.value;

public class FixedPhysicalQuantityValue extends PhysicalQuantityValue
{
  public FixedPhysicalQuantityValue(PhysicalQuantity physicalQuantity, double value)
  {
    super(physicalQuantity, value);
  }

  @Override
  public void setValue(double value)
  {
    throw new UnsupportedOperationException();
  }
}
