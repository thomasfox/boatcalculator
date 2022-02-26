package com.github.thomasfox.boatcalculator.value;

import java.util.Collections;
import java.util.List;

import lombok.Getter;

public class CalculatedPhysicalQuantityValue extends SimplePhysicalQuantityValue
{
  @Getter
  public PhysicalQuantityValuesWithSetIdPerValue calculatedFrom = new PhysicalQuantityValuesWithSetIdPerValue();

  @Getter
  public final String calculatedBy;

  public final boolean trial;

  public CalculatedPhysicalQuantityValue(
      PhysicalQuantityValue calculatedValue,
      String calculatedBy,
      boolean trial,
      PhysicalQuantityValueWithSetId... calculatedFrom)
  {
    super(calculatedValue);
    this.calculatedBy = calculatedBy;
    if (calculatedFrom != null)
    {
      this.calculatedFrom.setValues(calculatedFrom);
    }
    this.trial = trial;
  }

  public CalculatedPhysicalQuantityValue(
      PhysicalQuantityValue calculatedValue,
      String calculatedBy,
      boolean isTrial,
      PhysicalQuantityValuesWithSetIdPerValue calculatedFrom)
  {
    super(calculatedValue);
    this.calculatedBy = calculatedBy;
    if (calculatedFrom != null)
    {
      this.calculatedFrom.setValues(calculatedFrom);
    }
    this.trial = isTrial;
  }

  public CalculatedPhysicalQuantityValue(CalculatedPhysicalQuantityValue toCopy)
  {
    super(toCopy);
    this.calculatedBy = toCopy.calculatedBy;
    this.calculatedFrom = new PhysicalQuantityValuesWithSetIdPerValue(toCopy.calculatedFrom);
    this.trial = toCopy.trial;
  }

  public List<PhysicalQuantityValueWithSetId> getCalculatedFromAsList()
  {
    return Collections.unmodifiableList(calculatedFrom.getAsList());
  }

  public PhysicalQuantityValue getPhysicalQuantityValue()
  {
    return new SimplePhysicalQuantityValue(getPhysicalQuantity(), getValue());
  }

  @Override
  public CalculatedPhysicalQuantityValue clone()
  {
    return new CalculatedPhysicalQuantityValue(this);
  }

  @Override
  public boolean isTrial()
  {
    return trial;
  }
}
