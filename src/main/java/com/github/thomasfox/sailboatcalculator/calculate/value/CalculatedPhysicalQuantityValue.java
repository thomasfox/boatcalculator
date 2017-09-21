package com.github.thomasfox.sailboatcalculator.calculate.value;

import java.util.Collections;
import java.util.List;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;

import lombok.Getter;

public class CalculatedPhysicalQuantityValue extends PhysicalQuantityValue
{
  public PhysicalQuantityValuesWithSetNamePerValue calculatedFrom = new PhysicalQuantityValuesWithSetNamePerValue();

  @Getter
  public String calculatedBy;

  public CalculatedPhysicalQuantityValue(
      PhysicalQuantity physicalQuantity,
      double value,
      String calculatedBy,
      PhysicalQuantityValueWithSetName... calculatedFrom)
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
      PhysicalQuantityValuesWithSetNamePerValue calculatedFrom)
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
    this.calculatedFrom = new PhysicalQuantityValuesWithSetNamePerValue(toCopy.calculatedFrom);
  }

  public List<PhysicalQuantityValueWithSetName> getCalculatedFromAsList()
  {
    return Collections.unmodifiableList(calculatedFrom.getAsList());
  }
}
