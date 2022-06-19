package com.github.thomasfox.boatcalculator.calculate.strategy;

import com.github.thomasfox.boatcalculator.valueset.ValuesAndCalculationRules;

/**
 * A computation strategy which needs an converged result from the other calculations before doing a step.
 */
public interface ComputationFromConvergedResultStrategy extends ComputationStrategy
{

  /**
   * Resets the internal state of the computation strategy
   */
  public void reset();

  /**
   * Sets the start values for the following calculation.
   *
   * @param allValues the values here to set the start values.
   */
  public void setStartValues(ValuesAndCalculationRules allValues);

  /**
   * Performs a calculation step.
   * Before this method is called, the setStartValues method is called and then
   * all the "enclosed" Calculations were performed and the enclosed calculations have converged.
   *
   * @param allValues the values here to set the start values.
   */
  public boolean stepAfterConvergence(ValuesAndCalculationRules allValues);

}
