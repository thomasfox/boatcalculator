package com.github.thomasfox.wingcalculator.part;

import java.util.HashSet;
import java.util.Set;

import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantityValue;
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

  protected final Set<PhysicalQuantity> toInput = new HashSet<>();

  private final PhysicalQuantityValues fixedValues = new PhysicalQuantityValues();

  private final PhysicalQuantityValues startValues = new PhysicalQuantityValues();

  private final PhysicalQuantityValues calculatedValues = new PhysicalQuantityValues();

  public PhysicalQuantityValues getKnownValues()
  {
    PhysicalQuantityValues result = new PhysicalQuantityValues(calculatedValues);
    result.setValuesFailOnOverwrite(startValues);
    result.setValuesFailOnOverwrite(calculatedValues);
    return result;
  }

  public void setFixedValueNoOverwide(PhysicalQuantityValue toSet)
  {
    fixedValues.setValueNoOverwrite(toSet);
  }

  public Double getFixedValue(PhysicalQuantity physicalQuantity)
  {
    return fixedValues.getValue(physicalQuantity);
  }

  public Double getStartValue(PhysicalQuantity physicalQuantity)
  {
    return startValues.getValue(physicalQuantity);
  }
}
