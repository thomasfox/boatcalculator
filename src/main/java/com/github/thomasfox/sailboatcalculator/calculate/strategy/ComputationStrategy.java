package com.github.thomasfox.sailboatcalculator.calculate.strategy;

import com.github.thomasfox.sailboatcalculator.valueset.AllValues;

public interface ComputationStrategy
{
  /**
   * Sets the value(s) calculated by this strategy in the target set.
   *
   * @param allValues the values to calculate on.
   *
   * @return true if the values were changed, false otherwise.
   */
  public boolean setValue(AllValues allValues);
}
