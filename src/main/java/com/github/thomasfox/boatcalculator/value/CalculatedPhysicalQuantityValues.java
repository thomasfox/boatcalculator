package com.github.thomasfox.boatcalculator.value;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CalculatedPhysicalQuantityValues extends AbstractPhysicalQuantityValues<CalculatedPhysicalQuantityValue>
{
  public CalculatedPhysicalQuantityValues(CalculatedPhysicalQuantityValues toCopy)
  {
    super(toCopy);
  }

  @Override
  protected CalculatedPhysicalQuantityValue createEntry(PhysicalQuantity physicalQuantity, Double value)
  {
    return new CalculatedPhysicalQuantityValue(new PhysicalQuantityValue(physicalQuantity, value), null, (PhysicalQuantityValueWithSetId[]) null);
  }

  public void setValueNoOverwrite(
      PhysicalQuantityValue calculatedValue,
      String calculatedBy,
      PhysicalQuantityValueWithSetId... calculatedFrom)
  {
    super.setValueNoOverwrite(new CalculatedPhysicalQuantityValue(calculatedValue, calculatedBy, calculatedFrom));
  }

  public void setValueNoOverwrite(
      PhysicalQuantityValue calculatedValue,
      String calculatedBy,
      PhysicalQuantityValuesWithSetIdPerValue calculatedFrom)
  {
    super.setValueNoOverwrite(new CalculatedPhysicalQuantityValue(calculatedValue, calculatedBy, calculatedFrom));
  }

  public void setValue(
      PhysicalQuantityValue calculatedValue,
      String calculatedBy,
      PhysicalQuantityValueWithSetId... calculatedFrom)
  {
    super.setValue(new CalculatedPhysicalQuantityValue(calculatedValue, calculatedBy, calculatedFrom));
  }
}
