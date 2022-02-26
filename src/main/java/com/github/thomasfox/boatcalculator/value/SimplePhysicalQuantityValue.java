package com.github.thomasfox.boatcalculator.value;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class SimplePhysicalQuantityValue implements PhysicalQuantityValue
{
  @NonNull
  private final PhysicalQuantity physicalQuantity;

  // @Setter
  private final double value;

  public SimplePhysicalQuantityValue(PhysicalQuantityValue toCopy)
  {
    this.physicalQuantity = toCopy.getPhysicalQuantity();
    this.value = toCopy.getValue();
  }

  @Override
  public String toString()
  {
    return physicalQuantity + "=" + value;
  }

  @Override
  public boolean isTrial()
  {
    return false;
  }

  @Override
  public PhysicalQuantityValue clone()
  {
    return new SimplePhysicalQuantityValue(this);
  }
}
