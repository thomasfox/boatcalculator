package com.github.thomasfox.wingcalculator.interpolate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantityValues;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * Describes relations between multiple physical quantity along the line of
 * "if quantity a has value x, the quantity b has value y and quantity c has value z"
 */
@Builder
@Data
public class QuantityRelations
{
  @NonNull
  private String name;

  @NonNull
  private Map<PhysicalQuantity, Double> fixedQuantities = new HashMap<>();

  @NonNull
  private Set<PhysicalQuantity> relatedQuantities = new LinkedHashSet<>();

  @NonNull
  private List<Map<PhysicalQuantity, Double>> relatedQuantityValues = new ArrayList<>();

  /**
   * The PhysicalQuantity of which the other Quantities are functions.
   * May be null.
   */
  private final PhysicalQuantity keyQuantity;

  public Double interpolateValueFrom(
      PhysicalQuantity wantedQuantity,
      PhysicalQuantity providedQuantity,
      Double providedValue)
  {
    List<XYPoint> interpolationPoints = new ArrayList<>();
    for (Map<PhysicalQuantity, Double> relatedValues : relatedQuantityValues)
    {
      Double xValue = relatedValues.get(providedQuantity);
      if (xValue == null)
      {
        throw new InterpolatorException("Quantity " + providedQuantity + " not found");
      }
      Double yValue = relatedValues.get(wantedQuantity);
      if (yValue == null)
      {
        throw new InterpolatorException("Quantity " + wantedQuantity + " not found");
      }
      interpolationPoints.add(new SimpleXYPoint(xValue, yValue));
    }
    return new Interpolator().interpolate(providedValue, interpolationPoints);
  }

  public PhysicalQuantityValues getRelatedQuantityValues(Map<PhysicalQuantity, Double> knownValues)
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
          Double interpolatedValue = interpolateValueFrom(wantedQuantity, providedQuantity, knownValues.get(providedQuantity));
          if (interpolatedValue != null)
          {
            result.setValueNoOverwrite(wantedQuantity, interpolatedValue);
          }
        }
        catch (InterpolatorException e)
        {
          System.out.println("Could not calculate " + wantedQuantity.getDisplayName()
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
    for (Map.Entry<PhysicalQuantity, Double> fixedQuantity : fixedQuantities.entrySet())
    {
      result.append(fixedQuantity.getKey().getDisplayNameIncludingUnit())
      .append(" = ")
      .append(fixedQuantity.getValue())
      .append("   ");
    }
    return result.toString();
  }

  public boolean fixedQuantitiesMatch(Map<PhysicalQuantity, Double> knownValues)
  {
    for (Map.Entry<PhysicalQuantity, Double> fixedQuantity : fixedQuantities.entrySet())
    {
      Double knownValue = knownValues.get(fixedQuantity.getKey());
      if (knownValue == null || !knownValue.equals(fixedQuantity.getValue()))
      {
        return false;
      }
    }
    return true;
  }

  public Set<PhysicalQuantity> getAvailableQuantities(Map<PhysicalQuantity, Double> knownValues)
  {
    Set<PhysicalQuantity> result = new HashSet<>();
    for (PhysicalQuantity relatedQuantity : relatedQuantities)
    {
      if (!knownValues.keySet().contains(relatedQuantity))
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

  public Set<PhysicalQuantity> getKnownRelatedQuantities(Map<PhysicalQuantity, Double> knownValues)
  {
    Set<PhysicalQuantity> result = new HashSet<>();
    for (PhysicalQuantity relatedQuantity : relatedQuantities)
    {
      if (knownValues.keySet().contains(relatedQuantity))
      {
        result.add(relatedQuantity);
      }
    }
    return result;
  }
}
