package com.github.thomasfox.boatcalculator.value;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@EqualsAndHashCode
public class SimplePhysicalQuantityValueWithSetId implements PhysicalQuantityValueWithSetId
{
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("[");
    builder.append(setId);
    builder.append(":");
    builder.append(getPhysicalQuantity());
    builder.append("=");
    builder.append(getValue());
    builder.append("]");
    return builder.toString();
  }

  @NonNull
  @Getter
  private final String setId;

  private final PhysicalQuantityValue physicalQuantityValue;

  public SimplePhysicalQuantityValueWithSetId(PhysicalQuantityValue physicalQuantityValue, String setId)
  {
    this.physicalQuantityValue = physicalQuantityValue;
    this.setId = setId;
  }

  public SimplePhysicalQuantityValueWithSetId(SimplePhysicalQuantityValueWithSetId toCopy)
  {
    this.physicalQuantityValue = toCopy.physicalQuantityValue;
    this.setId = toCopy.setId;
  }

  @Override
  public SimplePhysicalQuantityValueWithSetId clone()
  {
    return new SimplePhysicalQuantityValueWithSetId(this);
  }

  @Override
  public PhysicalQuantity getPhysicalQuantity()
  {
    return physicalQuantityValue.getPhysicalQuantity();
  }

  @Override
  public PhysicalQuantityValue getPhysicalQuantityValue()
  {
    return physicalQuantityValue;
  }

  @Override
  public double getValue()
  {
    return physicalQuantityValue.getValue();
  }

  @Override
  public boolean isTrial()
  {
    return physicalQuantityValue.isTrial();
  }
}
