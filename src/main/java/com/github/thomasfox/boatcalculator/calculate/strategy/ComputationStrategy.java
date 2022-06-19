package com.github.thomasfox.boatcalculator.calculate.strategy;

import java.util.Set;

import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;

/**
 * A computation strategy is more powerful and complex than a simple
 * com.github.thomasfox.boatcalculator.calculate.Calculator, because it can
 * - connect quantities from different value set
 * - slowly approach the correct solution by recursively approaching the correct value.
 */
public interface ComputationStrategy
{
  public Set<PhysicalQuantityInSet> getOutputs();

  public Set<PhysicalQuantityInSet> getInputs();
}
