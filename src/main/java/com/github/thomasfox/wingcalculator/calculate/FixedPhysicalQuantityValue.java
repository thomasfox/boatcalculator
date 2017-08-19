package com.github.thomasfox.wingcalculator.calculate;

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
