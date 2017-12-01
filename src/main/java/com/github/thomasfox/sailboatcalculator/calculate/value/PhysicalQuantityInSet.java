package com.github.thomasfox.sailboatcalculator.calculate.value;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

/**
 * The key for a certain physical quantity in a certain value set,
 * e.g. the weight of the crew.
 */
@AllArgsConstructor
@Data
public class PhysicalQuantityInSet
{
  @NonNull
  private final PhysicalQuantity physicalQuantity;

  @NonNull
  private final String valueSetId;

  @Override
  public String toString()
  {
    return valueSetId + ":" + physicalQuantity;
  }
}
