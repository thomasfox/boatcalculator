package com.github.thomasfox.boatcalculator.value;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;

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

  @Override
  public FixedPhysicalQuantityValue clone()
  {
    return new FixedPhysicalQuantityValue(getPhysicalQuantity(), getValue());
  }
}
