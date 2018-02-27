package com.github.thomasfox.boatcalculator.calculate;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.github.thomasfox.boatcalculator.valueset.ValueSet;

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

  public double calculate(ValueSet input)
  {
    checkNeededQuantitiesArePresent(input);
    checkQuantitiesAreInValidRanges(input);
    return calculateWithoutChecks(input);
  }

  public boolean areNeededQuantitiesPresent(ValueSet valueSet)
  {
    for (PhysicalQuantity inputQuantity : inputQuantities)
    {
      if (!valueSet.isValueKnown(inputQuantity))
      {
        log.debug("Calculator " + getClass().getName() + " misses input quantity " + inputQuantity);
        return false;
      }
    }
    return true;
  }

  public boolean isOutputPresent(ValueSet valueSet)
  {
    return (valueSet.isValueKnown(outputQuantity));
  }

  protected void checkNeededQuantitiesArePresent(ValueSet valueSet)
  {
    for (PhysicalQuantity inputQuantity : inputQuantities)
    {
      if (!valueSet.isValueKnown(inputQuantity))
      {
        throw new QuantityNotPresentException(inputQuantity);
      }
    }
  }

  protected void checkQuantitiesAreInValidRanges(ValueSet valueSet)
  {
    // do nothing per default
  }

  protected abstract double calculateWithoutChecks(ValueSet valueSet);
}
