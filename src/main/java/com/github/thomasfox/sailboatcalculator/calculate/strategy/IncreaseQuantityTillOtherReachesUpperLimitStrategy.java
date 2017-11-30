package com.github.thomasfox.sailboatcalculator.calculate.strategy;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.AllValues;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityInSet;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Scans the unknown scanned quantity between zero and an upper limit
 * so that the limited quantity reaches its maximum value, but at most
 * its limit value.
 *
 * This strategy requires that both scanned and limited value are unknown
 * when calculating, that the limited quantity can be calculated
 * when the scanned quantity is known, and that the limited quantity increases
 * when the scanned quantity increases.
 */
@ToString
@Slf4j
public class IncreaseQuantityTillOtherReachesUpperLimitStrategy implements ComputationStrategy
{
  private final PhysicalQuantityInSet limitedQuantity;

  private final double limitedValueLimit;

  private final PhysicalQuantityInSet scannedQuantity;

  private final double scannedQuantityUpperLimit;

  public IncreaseQuantityTillOtherReachesUpperLimitStrategy(
      PhysicalQuantity limitedQuantity,
      String limitedQuantitySetId,
      double limitedValueLimit,
      PhysicalQuantity scannedQuantity,
      String scannedQuantitySetId,
      double scannedQuantityUpperLimit)
  {
    this.limitedQuantity = new PhysicalQuantityInSet(limitedQuantity, limitedQuantitySetId);
    this.limitedValueLimit = limitedValueLimit;
    this.scannedQuantity = new PhysicalQuantityInSet(scannedQuantity, scannedQuantitySetId);
    this.scannedQuantityUpperLimit = scannedQuantityUpperLimit;
  }

  @Override
  public boolean setValue(AllValues allValues)
  {
    if (allValues.isValueKnown(limitedQuantity))
    {
      return false;
    }

    if (allValues.isValueKnown(scannedQuantity))
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
    allValues.setCalculatedValueNoOverwrite(
        scannedQuantity,
        targetValue,
        "Scan " + allValues.getName(scannedQuantity) + " with max "+ scannedQuantityUpperLimit
          + " to maximize " + allValues.getName(limitedQuantity) + " with max " + limitedValueLimit);
    return true;
  }

  private Double applyAndRecalculateWithInterval(double scannedValue, double scanInterval, int cutoff, AllValues allValues)
  {
    if (cutoff <= -5)
    {
      throw new IllegalStateException("Could not limit quantity " + limitedQuantity
          + " within cutoff , last value for " + allValues.getName(scannedQuantity) + " was " + scannedValue);
    }
    clearComputedValuesAndSetScannedValue(scannedValue, allValues);
    log.info("Try value " + scannedValue + " for scannedQuantity " + scannedQuantity);
    allValues.calculate();
    Double limitedValue = allValues.getKnownValue(limitedQuantity);
    if (limitedValue == null)
    {
      return null;
    }

    if ((cutoff <= 0 || scannedValue == scannedQuantityUpperLimit) && limitedValue <= limitedValueLimit)
    {
      return scannedValue;
    }
    double newScanValue;
    if (limitedValue > limitedValueLimit)
    {
      newScanValue = scannedValue - scanInterval;
    }
    else if (limitedValue < limitedValueLimit)
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
    allValues.setCalculatedValueNoOverwrite(scannedQuantity, scannedValue, getClass().getSimpleName() + " trial value");
  }
}
