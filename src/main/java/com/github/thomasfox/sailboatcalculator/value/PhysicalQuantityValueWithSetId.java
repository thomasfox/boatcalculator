package com.github.thomasfox.sailboatcalculator.value;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;

import lombok.Getter;
import lombok.NonNull;

public class PhysicalQuantityValueWithSetId extends PhysicalQuantityValue
{
  @NonNull
  @Getter
  private final String setId;

  public PhysicalQuantityValueWithSetId(PhysicalQuantity physicalQuantity, double value, String setId)
  {
    super(physicalQuantity, value);
    this.setId = setId;
  }

  public PhysicalQuantityValueWithSetId(PhysicalQuantityValue physicalQuantityValue, String setId)
  {
    super(physicalQuantityValue);
    this.setId = setId;
  }

  public PhysicalQuantityValueWithSetId(PhysicalQuantityValueWithSetId toCopy)
  {
    super(toCopy);
    this.setId = toCopy.setId;
  }

  @Override
  public PhysicalQuantityValueWithSetId clone()
  {
    return new PhysicalQuantityValueWithSetId(this);
  }
}
