package com.github.thomasfox.sailboatcalculator.value;

import java.util.Collections;
import java.util.List;

import lombok.Getter;

public class CalculatedPhysicalQuantityValue extends PhysicalQuantityValue
{
  @Getter
  public PhysicalQuantityValuesWithSetIdPerValue calculatedFrom = new PhysicalQuantityValuesWithSetIdPerValue();

  @Getter
  public String calculatedBy;

  public CalculatedPhysicalQuantityValue(
      PhysicalQuantityValue calculatedValue,
      String calculatedBy,
      PhysicalQuantityValueWithSetId... calculatedFrom)
  {
    super(calculatedValue);
    this.calculatedBy = calculatedBy;
    if (calculatedFrom != null)
    {
      this.calculatedFrom.setValues(calculatedFrom);
    }
  }

  public CalculatedPhysicalQuantityValue(
      PhysicalQuantityValue calculatedValue,
      String calculatedBy,
      PhysicalQuantityValuesWithSetIdPerValue calculatedFrom)
  {
    super(calculatedValue);
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


  @Override
  public CalculatedPhysicalQuantityValue clone()
  {
    return new CalculatedPhysicalQuantityValue(this);
  }
}
