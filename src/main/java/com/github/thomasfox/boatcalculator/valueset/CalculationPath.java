package com.github.thomasfox.boatcalculator.valueset;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CalculationPath
{
  @NonNull
  @Getter
  private final String name;

  private final Set<CalculationTreeNode> requiredInputs = new HashSet<>();

  public CalculationPath addRequiredInput(CalculationTreeNode requiredInput)
  {
    requiredInputs.add(requiredInput);
    return this;
  }

  public Set<CalculationTreeNode> getRequiredInputs()
  {
    return Collections.unmodifiableSet(requiredInputs);
  }

  @Override
  public String toString()
  {
    StringBuilder result = new StringBuilder(name);
    result.append("[");
    boolean first = true;
    for (CalculationTreeNode requiredInput : requiredInputs)
    {
      if (!first)
      {
        result.append(",");
      }
      result.append(requiredInput.getCalculatedQuantity());
      first = false;
    }
    return result.toString();
  }

  @Override
  public int hashCode()
  {
    int result = 1;
    for (CalculationTreeNode requiredInput : requiredInputs)
    {
      result += requiredInput.getCalculatedQuantity().hashCode();

    }
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    CalculationPath other = (CalculationPath) obj;
    if (requiredInputs.size() != other.requiredInputs.size())
    {
      return false;
    }
    Set<PhysicalQuantityInSet> requiredQuantities
        = requiredInputs.stream().map(CalculationTreeNode::getCalculatedQuantity).collect(Collectors.toSet());
    Set<PhysicalQuantityInSet> otherRequiredQuantities
        = other.requiredInputs.stream().map(CalculationTreeNode::getCalculatedQuantity).collect(Collectors.toSet());
    return requiredQuantities.equals(otherRequiredQuantities);
  }
}
