package com.github.thomasfox.wingcalculator.calculate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class PhysicalQuantityValue
{
  @NonNull
  private final PhysicalQuantity physicalQuantity;

  @Setter
  private double value;

  public PhysicalQuantityValue(PhysicalQuantityValue toCopy)
  {
    this.physicalQuantity = toCopy.physicalQuantity;
    this.value = toCopy.value;
  }
}
