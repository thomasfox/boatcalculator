package com.github.thomasfox.boatcalculator.calculate.strategy;

import com.github.thomasfox.boatcalculator.valueset.ValuesAndCalculationRules;

/**
 * A computation strategy which can do a step in the right direction without an overall converged result.
 */
public interface StepComputationStrategy extends ComputationStrategy
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
}
