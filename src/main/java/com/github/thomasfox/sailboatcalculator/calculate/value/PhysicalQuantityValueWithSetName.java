package com.github.thomasfox.sailboatcalculator.calculate.value;

import lombok.Getter;
import lombok.NonNull;

public class PhysicalQuantityValueWithSetName extends PhysicalQuantityValue
{
  @NonNull
  @Getter
  private final String setName;

  public PhysicalQuantityValueWithSetName(PhysicalQuantity physicalQuantity, double value, String setName)
  {
    super(physicalQuantity, value);
    this.setName = setName;
  }

  public PhysicalQuantityValueWithSetName(PhysicalQuantityValue physicalQuantityValue, String setName)
  {
    super(physicalQuantityValue);
    this.setName = setName;
  }

  public PhysicalQuantityValueWithSetName(PhysicalQuantityValueWithSetName toCopy)
  {
    super(toCopy);
    this.setName = toCopy.setName;
  }

}
