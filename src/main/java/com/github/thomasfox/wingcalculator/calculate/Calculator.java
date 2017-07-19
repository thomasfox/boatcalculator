package com.github.thomasfox.wingcalculator.calculate;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Calculator
{
  private final Set<PhysicalQuantity> inputQuantities;

  private final PhysicalQuantity outputQuantity;

  protected Calculator(PhysicalQuantity outputQuantity, PhysicalQuantity... inputQuantities)
  {
    this.outputQuantity = outputQuantity;
    this.inputQuantities = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(inputQuantities)));
  }

  public Set<PhysicalQuantity> getInputQuantities()
  {
    return inputQuantities;
  }

  public PhysicalQuantity getOutputQuantity()
  {
    return outputQuantity;
  }

  public double calculate(Map<PhysicalQuantity, Double> input)
  {
    checkNeededQuantitiesArePresent(input);
    checkQuantitiesAreInValidRanges(input);
    return calculateWithoutChecks(input);
  }

  protected void checkNeededQuantitiesArePresent(Map<PhysicalQuantity, Double> input)
  {
    for (PhysicalQuantity inputQuantity : inputQuantities)
    {
      if (getValueOf(inputQuantity, input) == null)
      {
        throw new InputQuantityNotPresentException(inputQuantity);
      }
    }
  }

  protected void checkQuantitiesAreInValidRanges(Map<PhysicalQuantity, Double> input)
  {
    // do nothing per default
  }

  protected Double getValueOf(PhysicalQuantity quantity, Map<PhysicalQuantity, Double> availableQuantities)
  {
    if (quantity.getFixedValue() != null)
    {
      return quantity.getFixedValue();
    }
    return availableQuantities.get(quantity);
  }

  protected abstract double calculateWithoutChecks(Map<PhysicalQuantity, Double> input);
}
