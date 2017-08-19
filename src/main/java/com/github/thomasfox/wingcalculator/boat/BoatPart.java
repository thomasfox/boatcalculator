package com.github.thomasfox.wingcalculator.boat;

import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantityValues;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BoatPart
{
  @NonNull
  private PartType type;

  private PhysicalQuantityValues fixedValues;

  private PhysicalQuantityValues startValues;

  private PhysicalQuantityValues calculatedValues;

  public PhysicalQuantityValues getKnownValues()
  {
    PhysicalQuantityValues result = new PhysicalQuantityValues(calculatedValues);
    result.setValuesFailOnOverwrite(startValues);
    result.setValuesFailOnOverwrite(calculatedValues);
    return result;
  }
}
