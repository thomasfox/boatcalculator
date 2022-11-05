package com.github.thomasfox.boatcalculator.calculate;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.github.thomasfox.boatcalculator.value.CalculatedPhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValuesWithSetIdPerValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;
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

  /**
   * Calculates and sets the calculated value in the passed value set,
   * if calculation is possible.
   *
   * @param valueSet the value set to apply the calculator to, not null.
   *
   * @return whether the value was changed by a non-negligible amount.
   */
  public boolean apply(ValueSet valueSet)
  {
    if (isOutputFixed(valueSet))
    {
      return false;
    }
    if (!areNeededQuantitiesPresent(valueSet))
    {
      return false;
    }
    CalculationResult calculationResult = calculate(valueSet);
    boolean changed = !calculationResult.relativeDifferenceIsBelowThreshold();
    if (changed)
    {
      log.debug("Calculated new value " + calculationResult.getValue() + " for " + outputQuantity + " in " + valueSet.getId());
      valueSet.setCalculatedValueNoOverwrite(
          new SimplePhysicalQuantityValue(getOutputQuantity(), calculationResult.getValue()),
          getClass().getSimpleName(),
          calculationResult.isTrial(),
          valueSet.getKnownValuesAsArray(getInputQuantities()));
      return true;
    }
    else
    {
      log.debug("Relative difference is below Threshold for " + getOutputQuantity() + " in " + valueSet.getId());
      return false;
    }
  }

  public CalculationResult calculate(ValueSet input)
  {
    boolean trialValuesAreInInput = checkNeededQuantitiesArePresentAndAreTrialValuesPresent(input);
    checkQuantitiesAreInValidRanges(input);
    Double oldResult = Optional.ofNullable(input.getKnownQuantityValue(outputQuantity))
        .map(PhysicalQuantityValue::getValue)
        .orElse(null);
    double newResult = calculateWithoutChecks(input);
    CalculationResult result = new CalculationResult(newResult, oldResult, trialValuesAreInInput);
    return result;
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

  public boolean isOutputFixed(ValueSet valueSet)
  {
    PhysicalQuantityValue outputValue = valueSet.getKnownQuantityValue(outputQuantity);

    if (outputValue == null)
    {
      return false;
    }
    if (!(outputValue instanceof CalculatedPhysicalQuantityValue))
    {
      return true;
    }
    PhysicalQuantityValuesWithSetIdPerValue calculatedFrom
        = ((CalculatedPhysicalQuantityValue) outputValue).getCalculatedFrom();
    for (PhysicalQuantityValueWithSetId calculatedFromValue : calculatedFrom.getAsList())
    {
      if (!calculatedFromValue.getSetId().equals(valueSet.getId())
          || !inputQuantities.contains(calculatedFromValue.getPhysicalQuantity()))
      {
        log.debug("Ignoring " + getClass() + " because input quantity " + calculatedFromValue
            + " from old calculated value does not match source quantities");
        return true;
      }
    }
    return false;
  }

  protected boolean checkNeededQuantitiesArePresentAndAreTrialValuesPresent(ValueSet valueSet)
  {
    boolean trialValuesArePresent = false;
    for (PhysicalQuantity inputQuantity : inputQuantities)
    {
      PhysicalQuantityValue inputValue = valueSet.getKnownQuantityValue(inputQuantity);
      if (inputValue == null)
      {
        throw new QuantityNotPresentException(inputQuantity);
      }
      trialValuesArePresent |= inputValue.isTrial();
    }
    return trialValuesArePresent;
  }

  protected void checkQuantitiesAreInValidRanges(ValueSet valueSet)
  {
    // no invalid ranges are defined here
  }

  protected abstract double calculateWithoutChecks(ValueSet valueSet);
}
