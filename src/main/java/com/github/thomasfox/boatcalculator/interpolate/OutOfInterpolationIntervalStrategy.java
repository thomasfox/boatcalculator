package com.github.thomasfox.boatcalculator.interpolate;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.value.CalculatedPhysicalQuantityValues;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * How to calculate values from a quantity relation if a provided quantity is out of the range of values
 * in the quantity relation
 */
@RequiredArgsConstructor
public class OutOfInterpolationIntervalStrategy
{
  @NonNull
  @Getter
  private final PhysicalQuantity knownQuantity;

  @Getter
  private final boolean aboveKnownInterval;

  @Getter
  private final CalculatedPhysicalQuantityValues providedQuantities = new CalculatedPhysicalQuantityValues();

  public void addProvidedQuantities(PhysicalQuantityValue... providedQuantities)
  {
    for (PhysicalQuantityValue providedQuantity : providedQuantities)
    {
      this.providedQuantities.setValueNoOverwrite(providedQuantity, "OutOfInterpolationIntervalStrategy for " + knownQuantity, true);
    }
  }
}
