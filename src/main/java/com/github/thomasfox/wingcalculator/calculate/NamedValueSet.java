package com.github.thomasfox.wingcalculator.calculate;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
public abstract class NamedValueSet
{
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

  public void setFixedValueNoOverwrite(PhysicalQuantityValue toSet)
  {
    fixedValues.setValueNoOverwrite(toSet);
  }

  public void setStartValueNoOverwrite(PhysicalQuantity physicalQuantity, double value)
  {
    startValues.setValueNoOverwrite(physicalQuantity, value);
  }

  public Double getFixedValue(PhysicalQuantity physicalQuantity)
  {
    return fixedValues.getValue(physicalQuantity);
  }

  public Double getStartValue(PhysicalQuantity physicalQuantity)
  {
    return startValues.getValue(physicalQuantity);
  }

  public abstract String getName();
}
