package com.github.thomasfox.sailboatcalculator.interpolate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValues;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Describes relations between multiple physical quantity along the line of
 * "if quantity a has value x, the quantity b has value y and quantity c has value z"
 */
public class QuantityRelations
{
  @NonNull
  @Getter
  @Setter
  private String name;

  @NonNull
  @Getter
  private final PhysicalQuantityValues fixedQuantities;

  @NonNull
  private final List<PhysicalQuantityValues> relatedQuantityValues = new ArrayList<>();

  /**
   * The PhysicalQuantity of which the other Quantities are functions.
   * May be null.
   */
  @Getter
  private final PhysicalQuantity keyQuantity;

  @Builder
  public QuantityRelations(
      String name,
      PhysicalQuantityValues fixedQuantities,
      List<PhysicalQuantityValues> relatedQuantityValues,
      PhysicalQuantity keyQuantity)
  {
    this.name = name;
    this.fixedQuantities = new PhysicalQuantityValues(fixedQuantities);
    for (PhysicalQuantityValues relatedQuantityValuesEntry : relatedQuantityValues)
    {
      addRelatedQuantityValuesEntry(relatedQuantityValuesEntry);
    }
    this.keyQuantity = keyQuantity;
  }

  public void addRelatedQuantityValuesEntry(PhysicalQuantityValues relatedQuantityValuesEntry)
  {
    if (!relatedQuantityValues.isEmpty() && !getRelatedQuantities().equals(relatedQuantityValuesEntry.getContainedQuantities()))
    {
      throw new IllegalArgumentException("The passed object contains non-mathcing Physical Quantities");
    }
    relatedQuantityValues.add(new PhysicalQuantityValues(relatedQuantityValuesEntry));
  }

  public List<PhysicalQuantityValues> getRelatedQuantityValues()
  {
    List<PhysicalQuantityValues> result = new ArrayList<>();
    for (PhysicalQuantityValues entry : relatedQuantityValues)
    {
      result.add(new PhysicalQuantityValues(entry));
    }
    return result;
  }

  public Set<PhysicalQuantity> getRelatedQuantities()
  {
    if (relatedQuantityValues.isEmpty())
    {
      return Collections.unmodifiableSet(new HashSet<>());
    }
    return Collections.unmodifiableSet(relatedQuantityValues.get(0).getContainedQuantities());
  }

  public double interpolateValueFrom(
      PhysicalQuantity wantedQuantity,
      PhysicalQuantity providedQuantity,
      Double providedValue)
  {
    List<XYPoint> interpolationPoints = new ArrayList<>();
    for (PhysicalQuantityValues relatedValues : relatedQuantityValues)
    {
      Double xValue = relatedValues.getValue(providedQuantity);
      if (xValue == null)
      {
        throw new InterpolatorException("Quantity " + providedQuantity + " not found");
      }
      Double yValue = relatedValues.getValue(wantedQuantity);
      if (yValue == null)
      {
        throw new InterpolatorException("Quantity " + wantedQuantity + " not found");
      }
      interpolationPoints.add(new SimpleXYPoint(xValue, yValue));
    }
    return new Interpolator().interpolate(providedValue, interpolationPoints);
  }

  public PhysicalQuantityValues getRelatedQuantityValues(PhysicalQuantityValues knownValues)
  {
    PhysicalQuantityValues result = new PhysicalQuantityValues();
    Set<PhysicalQuantity> availableQuantities = getAvailableQuantities(knownValues);
    Set<PhysicalQuantity> providedQuantities = getKnownRelatedQuantities(knownValues);
    if (!providedQuantities.isEmpty())
    {
      PhysicalQuantity providedQuantity = providedQuantities.iterator().next();
      // TODO check that all other provided quantities match
      for (PhysicalQuantity wantedQuantity : availableQuantities)
      {
        try
        {
          double interpolatedValue = interpolateValueFrom(wantedQuantity, providedQuantity, knownValues.getValue(providedQuantity));
          result.setValueNoOverwrite(wantedQuantity, interpolatedValue);
        }
        catch (InterpolatorException e)
        {
          System.out.println("Could not calculate " + wantedQuantity.getDisplayName()
          + " for value " + knownValues.getValue(providedQuantity) + " of " + providedQuantity.getDisplayName()
          + " from quantityRelations " + name
          + " with fixed quantities " + printFixedQuantities());
        }
      }
    }
    return result;
  }


  public String printFixedQuantities()
  {
    StringBuilder result = new StringBuilder();
    for (PhysicalQuantityValue fixedQuantity : fixedQuantities.getAsList())
    {
      result.append(fixedQuantity.getPhysicalQuantity().getDisplayNameIncludingUnit())
      .append(" = ")
      .append(fixedQuantity.getValue())
      .append("   ");
    }
    return result.toString();
  }

  public boolean fixedQuantitiesMatch(PhysicalQuantityValues knownValues)
  {
    for (PhysicalQuantityValue fixedQuantity : fixedQuantities.getAsList())
    {
      Double knownValue = knownValues.getValue(fixedQuantity.getPhysicalQuantity());
      if (knownValue == null || !knownValue.equals(fixedQuantity.getValue()))
      {
        return false;
      }
    }
    return true;
  }

  public Set<PhysicalQuantity> getAvailableQuantities(PhysicalQuantityValues knownValues)
  {
    Set<PhysicalQuantity> result = new HashSet<>();
    Set<PhysicalQuantity> relatedQuantities = getRelatedQuantities();
    for (PhysicalQuantity relatedQuantity : relatedQuantities)
    {
      if (!knownValues.containsQuantity(relatedQuantity))
      {
        result.add(relatedQuantity);
      }
    }
    if (result.equals(relatedQuantities))
    {
      // no related value is known
      return new HashSet<>();
    }
    return result;
  }

  public Set<PhysicalQuantity> getKnownRelatedQuantities(PhysicalQuantityValues knownValues)
  {
    Set<PhysicalQuantity> result = new HashSet<>();
    for (PhysicalQuantity relatedQuantity : getRelatedQuantities())
    {
      if (knownValues.containsQuantity(relatedQuantity))
      {
        result.add(relatedQuantity);
      }
    }
    return result;
  }

  @Override
  public String toString()
  {
    return name + ": fixedQuantities=" + fixedQuantities + ", relatedQuantities= " + getRelatedQuantities();
  }
}
