package com.github.thomasfox.boatcalculator.value;

import java.util.HashSet;
import java.util.Set;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;

import lombok.Getter;

public class CalculatedPhysicalQuantityWithSetId implements PhysicalQuantityValueWithSetId
{
  private final CalculatedPhysicalQuantityValue calculatedPhysicalQuantityValue;

  @Getter
  private final String setId;

  public CalculatedPhysicalQuantityWithSetId(
      CalculatedPhysicalQuantityValue calculatedPhysicalQuantityValue,
      String setId)
  {
    this.calculatedPhysicalQuantityValue = calculatedPhysicalQuantityValue;
    this.setId = setId;
  }

  @Override
  public PhysicalQuantity getPhysicalQuantity()
  {
    return calculatedPhysicalQuantityValue.getPhysicalQuantity();
  }

  @Override
  public double getValue()
  {
    return calculatedPhysicalQuantityValue.getValue();
  }

  @Override
  public PhysicalQuantityValue getPhysicalQuantityValue()
  {
    return calculatedPhysicalQuantityValue;
  }

  @Override
  public boolean isTrial()
  {
    return calculatedPhysicalQuantityValue.isTrial();
  }

  public PhysicalQuantityValuesWithSetIdPerValue getCalculatedFrom()
  {
    return calculatedPhysicalQuantityValue.getCalculatedFrom();
  }

  public String getCalculatedBy()
  {
    return calculatedPhysicalQuantityValue.getCalculatedBy();
  }

  @Override
  public Object clone()
  {
    return new CalculatedPhysicalQuantityWithSetId(this.calculatedPhysicalQuantityValue.clone(), this.setId);
  }

  public void printCalculationTree()
  {
    PhysicalQuantityValueWithSetId ownValue = new SimplePhysicalQuantityValueWithSetId(
        calculatedPhysicalQuantityValue.getPhysicalQuantityValue(),
        setId);
    System.out.println(ownValue);
    for (PhysicalQuantityValueWithSetId calculatedFromEntry : calculatedPhysicalQuantityValue.getCalculatedFrom().getAsList())
    {
      printCalculationTree(calculatedFromEntry, ownValue, new HashSet<>(), 1);
    }
  }

  private void printCalculationTree(PhysicalQuantityValueWithSetId item, PhysicalQuantityValueWithSetId ignoredItem, Set<PhysicalQuantityInSet> alreadyPrinted, int depth)
  {
    if (depth > 20)
    {
      return;
    }

    for (int i = 0; i < depth - 1; i++)
    {
      System.out.print("  ");
    }
    System.out.print("- ");
    System.out.println(item);

    PhysicalQuantityInSet alreadyPrintedItem = new PhysicalQuantityInSet(
        item.getPhysicalQuantity(),
        item.getSetId());
    if (!item.equals(ignoredItem) && alreadyPrinted.contains(alreadyPrintedItem))
    {
      return;
    }
    alreadyPrinted.add(alreadyPrintedItem);

    PhysicalQuantityValuesWithSetIdPerValue calculatedFrom = null;
    if (item instanceof CalculatedPhysicalQuantityWithSetId)
    {
      calculatedFrom = ((CalculatedPhysicalQuantityWithSetId) item).getCalculatedFrom();
    }
    else if (item.getPhysicalQuantityValue() instanceof CalculatedPhysicalQuantityValue)
    {
      calculatedFrom = ((CalculatedPhysicalQuantityValue) item.getPhysicalQuantityValue()).getCalculatedFrom();
    }
    if (calculatedFrom == null)
    {
      return;
    }

    depth = depth + 1;
    for (PhysicalQuantityValueWithSetId calculatedFromEntry : calculatedFrom.getAsList())
    {
      printCalculationTree(calculatedFromEntry, ignoredItem, alreadyPrinted, depth);
    }
  }

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
}
