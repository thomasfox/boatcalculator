package com.github.thomasfox.boatcalculator.valueset;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class CalculationTreeNode
{
  @Getter
  @NonNull
  private final PhysicalQuantityInSet calculatedQuantity;

  Set<CalculationPath> canBeCalculatedFrom = new LinkedHashSet<>();

  @Getter
  @Setter
  private Double knownValue;

  public Set<CalculationPath> getPossibleCalculationPathForCalculatedQuantity()
  {
    return Collections.unmodifiableSet(canBeCalculatedFrom);
  }

  public void addPossibleCalculationPathForCalculatedQuantity(CalculationPath calculationPath)
  {
    canBeCalculatedFrom.add(calculationPath);
  }
}
