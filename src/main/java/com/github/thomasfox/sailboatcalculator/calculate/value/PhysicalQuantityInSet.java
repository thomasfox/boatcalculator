package com.github.thomasfox.sailboatcalculator.calculate.value;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@AllArgsConstructor
@Data
public class PhysicalQuantityInSet
{
  @NonNull
  private final PhysicalQuantity physicalQuantity;

  @NonNull
  private final String namedValueSetId;
}
