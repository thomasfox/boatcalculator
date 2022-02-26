package com.github.thomasfox.boatcalculator.calculate.strategy;

import java.util.Set;

import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.valueset.ValuesAndCalculationRules;

public interface ComputationStrategy
{
  /**
   * set the value calculated by this strategy one step closer to the calculated value.
   *
   * @param allValues the values to calculate on.
   * @param step the calculation step.
   *
   * @return true if further calculation is needed, false otherwise
   */
  public boolean step(ValuesAndCalculationRules allValues);

  public Set<PhysicalQuantityInSet> getOutputs();

  public Set<PhysicalQuantityInSet> getInputs();
}
