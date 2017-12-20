package com.github.thomasfox.sailboatcalculator.calculate.strategy;

import java.util.Arrays;
import java.util.Objects;

import com.github.thomasfox.sailboatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.sailboatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.value.PhysicalQuantityValueWithSetId;
import com.github.thomasfox.sailboatcalculator.valueset.AllValues;
import com.github.thomasfox.sailboatcalculator.valueset.ValueSet;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * A sum of values. One quantity is known to be the sum of other quantities.
 * Optionally, factors can be applied to the parts of the sum.
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
          new PhysicalQuantityValue(target.getPhysicalQuantity(), getSumOfSourceValues(allValues)),
          getCalculatedByDescription(allValues),
          getSourceValuesWithNames(allValues));
      return true;
    }
    return false;
  }

  public boolean allSourceValuesAreKnown(AllValues allValues)
  {
    return Arrays.stream(sources).allMatch(allValues::isValueKnown);
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

  private PhysicalQuantityValueWithSetId[] getSourceValuesWithNames(AllValues allValues)
  {
    PhysicalQuantityValueWithSetId[] result = new PhysicalQuantityValueWithSetId[sources.length];
    int i = 0;
    for (PhysicalQuantityInSet source : sources)
    {
      ValueSet sourceSet = allValues.getValueSet(source.getValueSetId());
      result[i] = new PhysicalQuantityValueWithSetId(
          sourceSet.getKnownValue(source.getPhysicalQuantity()),
          sourceSet.getId());
      ++i;
    }
    return result;
  }
}
