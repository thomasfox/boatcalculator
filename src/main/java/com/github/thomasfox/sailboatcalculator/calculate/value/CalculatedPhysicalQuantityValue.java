package com.github.thomasfox.sailboatcalculator.calculate.value;

import java.util.Collections;
import java.util.List;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;

import lombok.Getter;

public class CalculatedPhysicalQuantityValue extends PhysicalQuantityValue
{
  public PhysicalQuantityValuesWithSetIdPerValue calculatedFrom = new PhysicalQuantityValuesWithSetIdPerValue();

  @Getter
  public String calculatedBy;

  public CalculatedPhysicalQuantityValue(
      PhysicalQuantity physicalQuantity,
      double value,
      String calculatedBy,
      PhysicalQuantityValueWithSetId... calculatedFrom)
  {
    super(physicalQuantity, value);
    this.calculatedBy = calculatedBy;
    if (calculatedFrom != null)
    {
      this.calculatedFrom.setValues(calculatedFrom);
    }
  }

  public CalculatedPhysicalQuantityValue(
      PhysicalQuantity physicalQuantity,
      double value,
      String calculatedBy,
      PhysicalQuantityValuesWithSetIdPerValue calculatedFrom)
  {
    super(physicalQuantity, value);
    this.calculatedBy = calculatedBy;
    if (calculatedFrom != null)
    {
      this.calculatedFrom.setValues(calculatedFrom);
    }
  }

  public CalculatedPhysicalQuantityValue(CalculatedPhysicalQuantityValue toCopy)
  {
    super(toCopy);
    this.calculatedBy = toCopy.calculatedBy;
    this.calculatedFrom = new PhysicalQuantityValuesWithSetIdPerValue(toCopy.calculatedFrom);
  }

  public List<PhysicalQuantityValueWithSetId> getCalculatedFromAsList()
  {
    return Collections.unmodifiableList(calculatedFrom.getAsList());
  }
}
