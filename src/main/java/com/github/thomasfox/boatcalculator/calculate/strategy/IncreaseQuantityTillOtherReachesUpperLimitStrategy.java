package com.github.thomasfox.boatcalculator.calculate.strategy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.thomasfox.boatcalculator.calculate.CalculationResult;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.value.CalculatedPhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.CalculatedPhysicalQuantityWithSetId;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;
import com.github.thomasfox.boatcalculator.valueset.ValuesAndCalculationRules;

import lombok.ToString;

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
public class IncreaseQuantityTillOtherReachesUpperLimitStrategy implements ComputationStrategy
{
  private final PhysicalQuantityInSet limitedQuantity;

  private final double limitedValueLimit;

  private final PhysicalQuantityInSet scannedQuantity;

  private final double scannedQuantityUpperLimit;

  private double factor = 1;

  private Double lastTrialValueDifference;

  private int stepsToWait = 0;

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
  public boolean step(ValuesAndCalculationRules allValues)
  {
    ValueSet targetSet = allValues.getValueSetNonNull(scannedQuantity.getSetId());
    PhysicalQuantityValue knownTargetValue = targetSet.getKnownQuantityValue(scannedQuantity.getPhysicalQuantity());
    if (knownTargetValue == null)
    {
      PhysicalQuantityValue newTargetValue = new SimplePhysicalQuantityValue(
          scannedQuantity.getPhysicalQuantity(),
          scannedQuantityUpperLimit);
      targetSet.setCalculatedValueNoOverwrite(
          newTargetValue,
          getClass().getSimpleName() + " trial Value",
          true,
          new SimplePhysicalQuantityValueWithSetId(newTargetValue, scannedQuantity.getSetId()));
      factor = 1;
      stepsToWait = 0;
      lastTrialValueDifference = null;
      return true;
    }

    if (!knownTargetValue.isTrial())
    {
      // targetValue is no trial value and thus already calculated
      return false;
    }

    PhysicalQuantityValue limitedQuantityValue = allValues.getKnownPhysicalQuantityValue(limitedQuantity);
    if (limitedQuantityValue == null)
    {
      return false;
    }

//    if (stepsToWait > 0)
//    {
//      stepsToWait--;
//      return true;
//    }
//    stepsToWait = 7;
//
    double limitedValue = limitedQuantityValue.getValue();

    double trialValueDifference = factor * (limitedValueLimit - limitedValue);

    if (lastTrialValueDifference != null)
    {
      if (trialValueDifference*lastTrialValueDifference < 0d // they have different sign
        && Math.abs(trialValueDifference) > Math.abs(lastTrialValueDifference) * 0.9)
      {
        trialValueDifference = trialValueDifference * 0.5;
        factor *= 0.5;
      }
    }
    lastTrialValueDifference = trialValueDifference;

//    System.out.println("-------------------------------------------");
//    CalculationTreeEntry calculationTree = findInCalculationTree2(allValues, limitedQuantity);
//    calculationTree.removeLeavesExcept(limitedQuantity);
////    calculationTree.removeDuplicatePhysicalQuantitiesInSetExcept(limitedQuantity);
//    calculationTree.print(System.out);

//    Set<PhysicalQuantityValueWithSetId> limitedQuantityValuesInCalculationTree = calculationTree.getAllValuesOf(limitedQuantity);
    boolean shouldReturnTrue = false;
//    Double relativeDifference = getMaxRelativeDifference(limitedQuantityValuesInCalculationTree);
//    if (relativeDifference != null
//        && relativeDifference.doubleValue() != 0d
//        && (relativeDifference < 0
//            || knownTargetValue.getValue() == 0
//            || Math.abs(trialValueDifference / knownTargetValue.getValue()) < relativeDifference))
//    {
//      trialValueDifference = 0;
//      shouldReturnTrue = true;
//    }

//    if (limitedQuantityValue instanceof CalculatedPhysicalQuantityValue)
//    {
//      new CalculatedPhysicalQuantityWithSetId(
//              (CalculatedPhysicalQuantityValue) limitedQuantityValue,
//              limitedQuantity.getSetId())
//          .printCalculationTree();
//    }

    double newScanValue = knownTargetValue.getValue() + trialValueDifference;
    if (newScanValue < 0)
    {
      newScanValue = 0;
      factor *= 0.5;
    }

    if (newScanValue > scannedQuantityUpperLimit)
    {
      newScanValue = scannedQuantityUpperLimit;
    }
    targetSet.setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(scannedQuantity.getPhysicalQuantity(), newScanValue),
        getClass().getSimpleName() + " trial Value",
        true,
        new SimplePhysicalQuantityValueWithSetId(
            limitedQuantityValue,
            limitedQuantity.getSetId()));

    CalculationResult calculationResult = new CalculationResult(newScanValue, knownTargetValue.getValue(), true);
    return !calculationResult.relativeDifferenceIsBelowThreshold() || shouldReturnTrue;
  }

  private Double getMaxRelativeDifference(
      Set<PhysicalQuantityValueWithSetId> values)
  {
    double min = Double.MAX_VALUE;
    double max = -Double.MAX_VALUE;
    for (PhysicalQuantityValueWithSetId physicalQuantityValue : values)
    {
      double value = physicalQuantityValue.getValue();
      if (value < min)
      {
        min = value;
      }
      if (value > max)
      {
        max =  value;
      }
    }
    if (min == 0 || min == Double.MAX_VALUE || max == -Double.MAX_VALUE)
    {
      return null;
    }
    return 2 * (max - min)/(max + min);

  }

  @Override
  public Set<PhysicalQuantityInSet> getOutputs()
  {
    Set<PhysicalQuantityInSet> result = new HashSet<>();
    result.add(scannedQuantity);
    result.add(limitedQuantity);
    return result;
  }

  @Override
  public Set<PhysicalQuantityInSet> getInputs()
  {
    Set<PhysicalQuantityInSet> result = new HashSet<>();
    return result;
  }

  private CalculationTreeEntry findInCalculationTree2(
      ValuesAndCalculationRules allValues,
      PhysicalQuantityInSet stopQuantity)
  {
    PhysicalQuantityValue quantityValue = allValues.getKnownPhysicalQuantityValue(stopQuantity);
    PhysicalQuantityValueWithSetId startValue = new SimplePhysicalQuantityValueWithSetId(
        quantityValue,
        stopQuantity.getSetId());

    CalculationTreeEntry root = CalculationTreeEntry.fromRoot(startValue);
    findInCalculationTree(root, stopQuantity, 0);
    return root;
  }

  private void findInCalculationTree(
      CalculationTreeEntry searchRoot,
      PhysicalQuantityInSet stopQuantity,
      int currentDepth)
  {
    if (currentDepth >= 20)
    {
      return;
    }
    PhysicalQuantityValueWithSetId searchRootValue = searchRoot.getValue();

    List<PhysicalQuantityValueWithSetId> calculatedFromList = List.of();
    if (searchRootValue instanceof CalculatedPhysicalQuantityWithSetId)
    {
      CalculatedPhysicalQuantityWithSetId calculatedPhysicalQuantityWithSetId
          = (CalculatedPhysicalQuantityWithSetId) searchRootValue;
      calculatedFromList = calculatedPhysicalQuantityWithSetId.getCalculatedFrom().getAsList();
    }
    else
    {
      PhysicalQuantityValue physicalQuantityValue = searchRootValue.getPhysicalQuantityValue();
      if (physicalQuantityValue instanceof CalculatedPhysicalQuantityValue)
      {
        calculatedFromList = ((CalculatedPhysicalQuantityValue) physicalQuantityValue).getCalculatedFromAsList();
      }
    }
    for (PhysicalQuantityValueWithSetId calculatedFrom : calculatedFromList)
    {
      CalculationTreeEntry nextValue = searchRoot.addChild(calculatedFrom);
      if (calculatedFrom.getPhysicalQuantity().equals(stopQuantity.getPhysicalQuantity())
          && calculatedFrom.getSetId().equals(stopQuantity.getSetId()))
      {
//        System.out.println("Used " + calculatedFrom + " with depth " + currentDepth + " for " + stopQuantity);
        continue;
      }
      else
      {
        findInCalculationTree(nextValue, stopQuantity, currentDepth + 1);
      }
    }
  }

}
