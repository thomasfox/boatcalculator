package com.github.thomasfox.wingcalculator.calculate.strategy;

import com.github.thomasfox.wingcalculator.calculate.AllValues;
import com.github.thomasfox.wingcalculator.calculate.NamedValueSet;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantityValue;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class IncreaseQuantityTillOtherReachesUpperLimitStrategy implements ComputationStrategy
{
  private final PhysicalQuantity limitedQuantity;

  private final String limitedQuantitySetId;

  private final double limitedValueLimit;

  private final PhysicalQuantity scannedQuantity;

  private final String scannedQuantitySetId;

  private final double scannedQuantityUpperLimit;

  @Override
  public boolean setValue(AllValues allValues)
  {
    NamedValueSet limitedQuantitySet = allValues.getNamedValueSetNonNull(limitedQuantitySetId);

    PhysicalQuantityValue limitedValue = limitedQuantitySet.getKnownValue(limitedQuantity);
    if (limitedValue != null)
    {
      return false;
    }

    NamedValueSet scannedQuantitySet = allValues.getNamedValueSetNonNull(scannedQuantitySetId);
    PhysicalQuantityValue scannedQuantityValue = scannedQuantitySet.getKnownValue(scannedQuantity);
    if (scannedQuantityValue != null)
    {
      return false;
    }

    double interval = scannedQuantityUpperLimit / 2;

    AllValues allValuesForCalculation = new AllValues(allValues);
    allValuesForCalculation.moveCalculatedValuesToStartValues();
    double targetValue = applyAndRecalculateWithInterval(scannedQuantityUpperLimit, interval, 20, allValuesForCalculation);
    scannedQuantitySet.setCalculatedValue(scannedQuantity, targetValue);
    return true;
  }

  private double applyAndRecalculateWithInterval(double scannedValue, double scanInterval, int cutoff, AllValues allValues)
  {
    if (cutoff <= -5)
    {
      throw new IllegalStateException("Could not limit quantity " + limitedQuantity
          + " within cutoff , last value for " + scannedQuantity + " in " + scannedQuantitySetId + " was " + scannedValue);
    }
    clearComputedValuesAndSetScannedValue(scannedValue, allValues);
    allValues.calculate();
    NamedValueSet limitedQuantitySet = allValues.getNamedValueSetNonNull(limitedQuantitySetId);
    PhysicalQuantityValue limitedValue = limitedQuantitySet.getKnownValue(limitedQuantity);
    if (limitedValue == null)
    {
      throw new IllegalStateException("Tried to limit quantity " + limitedQuantity
          + " in " + limitedQuantitySetId
          + " but limited quantity was not calculated");
    }

    if ((cutoff <= 0 || scannedValue == scannedQuantityUpperLimit) && limitedValue.getValue() <= limitedValueLimit)
    {
      return scannedValue;
    }
    double newScanValue;
    if (limitedValue.getValue() > limitedValueLimit)
    {
      newScanValue = scannedValue - scanInterval;
    }
    else if (limitedValue.getValue() < limitedValueLimit)
    {
      newScanValue = scannedValue + scanInterval;
    }
    else
    {
      // exact match
      return scannedValue;
    }
    return applyAndRecalculateWithInterval(newScanValue, cutoff <=0 ? scanInterval : scanInterval / 2, cutoff - 1, allValues);
  }

  private void clearComputedValuesAndSetScannedValue(double scannedValue, AllValues allValues)
  {
    allValues.clearCalculatedValues();
    NamedValueSet scannedQuantitySet = allValues.getNamedValueSetNonNull(scannedQuantitySetId);
    scannedQuantitySet.setCalculatedValue(scannedQuantity, scannedValue);
  }
}