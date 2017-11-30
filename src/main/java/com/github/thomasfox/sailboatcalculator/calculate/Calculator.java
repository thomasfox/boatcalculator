package com.github.thomasfox.sailboatcalculator.calculate;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValues;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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

  public double calculate(PhysicalQuantityValues input)
  {
    checkNeededQuantitiesArePresent(input);
    checkQuantitiesAreInValidRanges(input);
    return calculateWithoutChecks(input);
  }

  public boolean areNeededQuantitiesPresent(PhysicalQuantityValues input)
  {
    for (PhysicalQuantity inputQuantity : inputQuantities)
    {
      if (input.getPhysicalQuantityValue(inputQuantity) == null)
      {
        log.debug("Calculator " + getClass().getName() + " misses input quantity " + inputQuantity);
        return false;
      }
    }
    return true;
  }

  public boolean isOutputPresent(PhysicalQuantityValues input)
  {
    return (input.getPhysicalQuantityValue(outputQuantity) != null);
  }

  protected void checkNeededQuantitiesArePresent(PhysicalQuantityValues input)
  {
    for (PhysicalQuantity inputQuantity : inputQuantities)
    {
      if (input.getPhysicalQuantityValue(inputQuantity) == null)
      {
        throw new QuantityNotPresentException(inputQuantity);
      }
    }
  }

  protected void checkQuantitiesAreInValidRanges(PhysicalQuantityValues input)
  {
    // do nothing per default
  }

  protected abstract double calculateWithoutChecks(PhysicalQuantityValues input);
}
