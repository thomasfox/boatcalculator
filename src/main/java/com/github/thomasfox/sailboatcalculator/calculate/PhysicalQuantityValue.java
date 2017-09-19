package com.github.thomasfox.sailboatcalculator.calculate;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
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
