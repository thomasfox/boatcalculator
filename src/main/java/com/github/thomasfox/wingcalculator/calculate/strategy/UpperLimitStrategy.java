package com.github.thomasfox.wingcalculator.calculate.strategy;

import com.github.thomasfox.wingcalculator.boat.Boat;
import com.github.thomasfox.wingcalculator.calculate.NamedValueSet;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantityValue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class UpperLimitStrategy implements ComputationStrategy
{
  private final PhysicalQuantity limitedQuantity;

  private final NamedValueSet limitedQuantitySet;

  private final double upperLimit;

  private final PhysicalQuantity targetQuantity;

  private final Boat boat;

  @Override
  public boolean setValue(NamedValueSet targetSet)
  {
    PhysicalQuantityValue limitedValue = limitedQuantitySet.getKnownValue(limitedQuantity);
    if (limitedValue == null || limitedValue.getValue() < upperLimit)
    {
      return false;
    }
    PhysicalQuantityValue originalTargetValue = targetSet.getKnownValue(targetQuantity);
    if (originalTargetValue == null)
    {
      throw new IllegalStateException("Tried to limit quantity " + limitedQuantity
          + " in " + limitedQuantitySet.getName()
          + " but target quantity " + targetQuantity + " is not set in " + targetSet);
    }
    double interval = originalTargetValue.getValue() / 2;

    applyAndRecalculateWithInterval(interval, 15, targetSet);
    return true;
  }

  private void applyAndRecalculateWithInterval(double interval, int cutoff, NamedValueSet targetSet)
  {
    PhysicalQuantityValue limitedValue = limitedQuantitySet.getKnownValue(limitedQuantity);
    if (limitedValue == null)
    {
      throw new IllegalStateException("Tried to limit quantity " + limitedQuantity
          + " in " + limitedQuantitySet.getName()
          + " but limited quantity has disappearded within calculation");
    }
    if (cutoff <= 0 && limitedValue.getValue() <= upperLimit)
    {
      return;
    }
    double newTargetValue;
    if (limitedValue.getValue() > upperLimit)
    {
      newTargetValue = targetSet.getKnownValue(targetQuantity).getValue() - interval;
    }
    else if (limitedValue.getValue() < upperLimit)
    {
      newTargetValue = targetSet.getKnownValue(targetQuantity).getValue() + interval;
    }
    else
    {
      // exact match
      return;
    }
    for (NamedValueSet set : boat.getNamedValueSets())
    {
      set.clearCalculatedValues();
    }
    if (limitedQuantitySet.getKnownValue(limitedQuantity) != null)
    {
      throw new IllegalStateException("Tried to limit quantity " + limitedQuantity
          + " in " + limitedQuantitySet.getName()
          + " but clearing calculated value for limited quantity had no effect");
    }
    targetSet.setCalculatedValue(targetQuantity, newTargetValue);
    boat.calculate(this);
    applyAndRecalculateWithInterval(cutoff <= 0 ? interval : interval / 2, cutoff - 1, targetSet);
  }
}
