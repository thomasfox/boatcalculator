package com.github.thomasfox.sailboatcalculator.calculate.strategy;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.AllValues;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.calculate.value.ValueSet;

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
    ValueSet limitedQuantitySet = allValues.getValueSetNonNull(limitedQuantitySetId);

    PhysicalQuantityValue limitedValue = limitedQuantitySet.getKnownValue(limitedQuantity);
    if (limitedValue != null)
    {
      return false;
    }

    ValueSet scannedQuantitySet = allValues.getValueSetNonNull(scannedQuantitySetId);
    PhysicalQuantityValue scannedQuantityValue = scannedQuantitySet.getKnownValue(scannedQuantity);
    if (scannedQuantityValue != null)
    {
      return false;
    }

    double interval = scannedQuantityUpperLimit / 2;

    AllValues allValuesForCalculation = new AllValues(allValues);
    allValuesForCalculation.moveCalculatedValuesToStartValues();
    Double targetValue = applyAndRecalculateWithInterval(scannedQuantityUpperLimit, interval, 20, allValuesForCalculation);
    if (targetValue == null)
    {
      return false;
    }
    scannedQuantitySet.setCalculatedValueNoOverwrite(
        scannedQuantity,
        targetValue,
        "Scan " + scannedQuantitySet.getName() + ": " + scannedQuantity.getDisplayName() + " with max "+ scannedQuantityUpperLimit
          + " to maximize " + limitedQuantitySet.getName() + ":" + limitedQuantity.getDisplayName() + " with max " + limitedValueLimit);
    return true;
  }

  private Double applyAndRecalculateWithInterval(double scannedValue, double scanInterval, int cutoff, AllValues allValues)
  {
    if (cutoff <= -5)
    {
      throw new IllegalStateException("Could not limit quantity " + limitedQuantity
          + " within cutoff , last value for " + scannedQuantity + " in " + scannedQuantitySetId + " was " + scannedValue);
    }
    clearComputedValuesAndSetScannedValue(scannedValue, allValues);
    allValues.calculate();
    ValueSet limitedQuantitySet = allValues.getValueSetNonNull(limitedQuantitySetId);
    PhysicalQuantityValue limitedValue = limitedQuantitySet.getKnownValue(limitedQuantity);
    if (limitedValue == null)
    {
      return null;
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
    ValueSet scannedQuantitySet = allValues.getValueSetNonNull(scannedQuantitySetId);
    scannedQuantitySet.setCalculatedValueNoOverwrite(scannedQuantity, scannedValue, getClass().getSimpleName() + " trial value");
  }
}
