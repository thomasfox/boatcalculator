package com.github.thomasfox.sailboatcalculator.calculate.strategy;

import java.util.Objects;

import com.github.thomasfox.sailboatcalculator.calculate.value.AllValues;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityInSet;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValueWithSetName;
import com.github.thomasfox.sailboatcalculator.calculate.value.ValueSet;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * A directed equality. A quantity is known to be equal to another.
 * So if the one quantity is known, the other quantities value can be set
 * equal to the value of the first quantity.
 */
@Getter
@ToString
public class QuantitySum implements ComputationStrategy
{
  @NonNull
  private final PhysicalQuantityInSet target;

  @NonNull
  private final PhysicalQuantityInSet[] sources;

  public QuantitySum(@NonNull PhysicalQuantityInSet target, @NonNull PhysicalQuantityInSet... sources)
  {
    this.target = target;
    this.sources = sources;
    String targetUnit = target.getPhysicalQuantity().getUnit();
    for (PhysicalQuantityInSet source : sources)
    {
      if (!Objects.equals(source.getPhysicalQuantity().getUnit(), targetUnit))
      {
        throw new IllegalArgumentException("Source " + source.getPhysicalQuantity().getDescription()
            + " has wrong unit, must be equal to unit of target " + target.getPhysicalQuantity().getDescription());
      }
    }
  }

  @Override
  public boolean setValue(AllValues allValues)
  {
    ValueSet targetSet = allValues.getValueSetNonNull(target.getValueSetId());
    if (allSourceValuesAreKnown(allValues) && !targetSet.isValueKnown(target.getPhysicalQuantity()))
    {
      targetSet.setCalculatedValueNoOverwrite(
          target.getPhysicalQuantity(),
          getSumOfSourceValues(allValues),
          getCalculatedByDescription(allValues),
          getSourceValuesWithNames(allValues));
      return true;
    }
    return false;
  }

  public boolean allSourceValuesAreKnown(AllValues allValues)
  {
    for (PhysicalQuantityInSet source : sources)
    {
      Double sourceValue = allValues.getKnownValue(source);
      if (sourceValue == null)
      {
        return false;
      }
    }
    return true;
  }

  public double getSumOfSourceValues(AllValues allValues)
  {
    double result = 0d;
    for (PhysicalQuantityInSet source : sources)
    {
      result += allValues.getKnownValue(source);
    }
    return result;
  }

  public String getCalculatedByDescription(AllValues allValues)
  {
    StringBuilder result = new StringBuilder();
    for (PhysicalQuantityInSet source : sources)
    {
      if (result.length() > 0 )
      {
        result.append(" + ");
      }
      result.append(allValues.getNameOfSetWithId(source.getValueSetId()))
          .append(":")
          .append(source.getPhysicalQuantity().getDisplayName());
    }
    return result.toString();
  }

  private PhysicalQuantityValueWithSetName[] getSourceValuesWithNames(AllValues allValues)
  {
    PhysicalQuantityValueWithSetName[] result = new PhysicalQuantityValueWithSetName[sources.length];
    int i = 0;
    for (PhysicalQuantityInSet source : sources)
    {
      ValueSet sourceSet = allValues.getValueSet(source.getValueSetId());
      result[i] = new PhysicalQuantityValueWithSetName(
          sourceSet.getKnownValue(source.getPhysicalQuantity()),
          sourceSet.getName());
      ++i;
    }
    return result;
  }
}
