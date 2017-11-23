package com.github.thomasfox.sailboatcalculator.calculate.value;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CalculatedPhysicalQuantityValues extends AbstractPhysicalQuantityValues<CalculatedPhysicalQuantityValue>
{
  public CalculatedPhysicalQuantityValues(CalculatedPhysicalQuantityValues toCopy)
  {
    super(toCopy);
  }

  @Override
  protected CalculatedPhysicalQuantityValue copy(CalculatedPhysicalQuantityValue toCopy)
  {
    return new CalculatedPhysicalQuantityValue(toCopy);
  }

  @Override
  protected CalculatedPhysicalQuantityValue createEntry(PhysicalQuantity physicalQuantity, Double value)
  {
    return new CalculatedPhysicalQuantityValue(physicalQuantity, value, null, (PhysicalQuantityValueWithSetId[]) null);
  }

  public void setValueNoOverwrite(
      PhysicalQuantity physicalQuantity,
      double value,
      String calculatedBy,
      PhysicalQuantityValueWithSetId... calculatedFrom)
  {
    super.setValueNoOverwrite(new CalculatedPhysicalQuantityValue(physicalQuantity, value, calculatedBy, calculatedFrom));
  }

  public void setValueNoOverwrite(
      PhysicalQuantity physicalQuantity,
      double value,
      String calculatedBy,
      PhysicalQuantityValuesWithSetIdPerValue calculatedFrom)
  {
    super.setValueNoOverwrite(new CalculatedPhysicalQuantityValue(physicalQuantity, value, calculatedBy, calculatedFrom));
  }

  public void setValue(
      PhysicalQuantity physicalQuantity,
      double value,
      String calculatedBy,
      PhysicalQuantityValueWithSetId... calculatedFrom)
  {
    super.setValue(new CalculatedPhysicalQuantityValue(physicalQuantity, value, calculatedBy, calculatedFrom));
  }
}
