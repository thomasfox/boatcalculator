package com.github.thomasfox.wingcalculator.calculate.strategy;

import com.github.thomasfox.wingcalculator.calculate.NamedValueSet;

public interface ComputationStrategy
{
  /**
   * Sets the value(s) calculated by this strategy in the target set.
   *
   * @param targetSet the target to set the calculated value(s) in, not null.
   *
   * @return true if the target set was changed, false otherwise.
   */
  public boolean setValue(NamedValueSet targetSet);
}
