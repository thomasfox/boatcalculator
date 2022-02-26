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
  protected CalculatedPhysicalQuantityValue createEntry(
      PhysicalQuantity physicalQuantity, Double value)
  {
    throw new IllegalStateException("call CalculatedPhysicalQuantityValue createEntry(PhysicalQuantity, Double, boolean) instead");
  }

//  protected CalculatedPhysicalQuantityValue createEntry(PhysicalQuantity physicalQuantity, Double value, boolean trialValue)
//  {
//    return new CalculatedPhysicalQuantityValue(new PhysicalQuantityValue(physicalQuantity, value), null, trialValue, (PhysicalQuantityValueWithSetId[]) null);
//  }

  public void setValueNoOverwrite(
      PhysicalQuantityValue calculatedValue,
      String calculatedBy,
      boolean trialValue,
      PhysicalQuantityValueWithSetId... calculatedFrom)
  {
    super.setValueNoOverwrite(new CalculatedPhysicalQuantityValue(calculatedValue, calculatedBy, trialValue, calculatedFrom));
  }

  public void setValueNoOverwrite(
      PhysicalQuantityValue calculatedValue,
      String calculatedBy,
      boolean trialValue,
      PhysicalQuantityValuesWithSetIdPerValue calculatedFrom)
  {
    super.setValueNoOverwrite(new CalculatedPhysicalQuantityValue(calculatedValue, calculatedBy, trialValue, calculatedFrom));
  }

  public void setValue(
      PhysicalQuantityValue calculatedValue,
      String calculatedBy,
      boolean trialValue,
      PhysicalQuantityValueWithSetId... calculatedFrom)
  {
    super.setValue(new CalculatedPhysicalQuantityValue(calculatedValue, calculatedBy, trialValue, calculatedFrom));
  }
}
