package com.github.thomasfox.boatcalculator.calculate.strategy;

import java.util.Set;

import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.valueset.ValuesAndCalculationRules;

public interface ComputationStrategy
{
  /**
   * If the quantities calculated by this strategy are calculated in an iterative manner,
   * do one step in the iteration and set the new calculated quantities in allValues.
   * If the quantities calculated by this strategy can be calculated in a single step,
   * do the calculation and set the new calculated quantities in allValues.
   *
   * @param allValues the values to calculate on.
   *
   * @return true if further calculation is needed, false otherwise
   */
  public boolean step(ValuesAndCalculationRules allValues);

  public Set<PhysicalQuantityInSet> getOutputs();

  public Set<PhysicalQuantityInSet> getInputs();
}
