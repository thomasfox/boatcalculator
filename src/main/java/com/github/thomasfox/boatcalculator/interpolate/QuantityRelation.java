package com.github.thomasfox.boatcalculator.interpolate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.interpolate.Interpolator.TwoValues;
import com.github.thomasfox.boatcalculator.value.CalculatedPhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.CalculatedPhysicalQuantityValues;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValues;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Describes a relation between multiple physical quantity along the line of
 * "if quantity a has value x, the quantity b has value y and quantity c has value z"
 */
@Slf4j
public class QuantityRelation
{
  @NonNull
  @Getter
  @Setter
  private String name;

  @NonNull
  @Getter
  private final PhysicalQuantityValues fixedQuantities;

  private final List<PhysicalQuantityValues> relatedQuantityValues = new ArrayList<>();

  private final Set<OutOfInterpolationIntervalStrategy> outOfInterpolationStrategies = new HashSet<>();

  private final Interpolator interpolator = new Interpolator();

  /**
   * The PhysicalQuantity of which the other Quantities are functions.
   * May be null.
   */
  @Getter
  private final PhysicalQuantity keyQuantity;

  @Builder
  public QuantityRelation(
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

  public void add(OutOfInterpolationIntervalStrategy strategy)
  {
    outOfInterpolationStrategies.add(strategy);
  }

  public CalculatedPhysicalQuantityValues getRelatedQuantityValues(ValueSet valueSet)
  {
    CalculatedPhysicalQuantityValues result = new CalculatedPhysicalQuantityValues();
    PhysicalQuantity providedQuantity = getProvidedQuantity(valueSet);
    if (providedQuantity == null)
    {
      return result;
    }
    Set<PhysicalQuantity> availableQuantities = getAvailableQuantities(valueSet, providedQuantity);

    PhysicalQuantityValue xQuantityValue = valueSet.getKnownQuantityValue(providedQuantity);
    double xValue = xQuantityValue.getValue();
    TwoValues<PhysicalQuantityValues> enclosingPoints;
    try
    {
      enclosingPoints = interpolator.getEnclosing(xValue, relatedQuantityValues, x -> x.getValue(providedQuantity));
    }
    catch (OutOfInterpolationIntervalException e)
    {
      for (OutOfInterpolationIntervalStrategy strategy : outOfInterpolationStrategies)
      {
        if (strategy.getKnownQuantity() == providedQuantity
            && strategy.isAboveKnownInterval() == e.isAboveInterval())
        {
          return strategy.getProvidedQuantities();
        }
      }
      log.info("Could not calculate " + availableQuantities
      + " for value " + valueSet.getKnownQuantityValue(providedQuantity).getValue()
      + " of " + providedQuantity.getDisplayName()
      + " from quantityRelations " + name
      + " with fixed quantities " + printFixedQuantities()
      + " reason is " + e.getMessage());
      return result;
    }
    catch (InterpolatorException e)
    {
      log.info("Could not calculate " + availableQuantities
      + " for value " + valueSet.getKnownQuantityValue(providedQuantity).getValue()
      + " of " + providedQuantity.getDisplayName()
      + " from quantityRelations " + name
      + " with fixed quantities " + printFixedQuantities()
      + " reason is " + e.getMessage());
      return result;
    }

    for (PhysicalQuantity wantedQuantity : availableQuantities)
    {
      double interpolatedValue = interpolator.interpolateY(
          xValue,
          new SimpleXYPoint(enclosingPoints.value1.getValue(providedQuantity), enclosingPoints.value1.getValue(wantedQuantity)),
          new SimpleXYPoint(enclosingPoints.value2.getValue(providedQuantity), enclosingPoints.value2.getValue(wantedQuantity)));
        result.setValueNoOverwrite(new SimplePhysicalQuantityValue(wantedQuantity, interpolatedValue), name, xQuantityValue.isTrial());
    }
    return result;
  }

  private PhysicalQuantity getProvidedQuantity(ValueSet valueSet)
  {
    Set<PhysicalQuantityValue> providedQuantityValueSet = getKnownRelatedQuantities(valueSet);
    if (providedQuantityValueSet.isEmpty())
    {
      return null;
    }

    if (providedQuantityValueSet.size() == 1)
    {
      return providedQuantityValueSet.iterator().next().getPhysicalQuantity();
    }

    Set<PhysicalQuantity> providedPhysicalQuantitySet = providedQuantityValueSet.stream()
        .map(PhysicalQuantityValue::getPhysicalQuantity)
        .collect(Collectors.toSet());
    for (PhysicalQuantityValue providedQuantityValue : providedQuantityValueSet)
    {
      if (!providedQuantityValue.isTrial())
      {
        return providedQuantityValue.getPhysicalQuantity();
      }
    }
    for (PhysicalQuantityValue providedQuantityValue : providedQuantityValueSet)
    {
      if (providedQuantityValue instanceof CalculatedPhysicalQuantityValue)
      {
        CalculatedPhysicalQuantityValue calculatedValue
            = (CalculatedPhysicalQuantityValue) providedQuantityValue;
        boolean calculatedFromProvidedValue = false;
        for (PhysicalQuantityValueWithSetId calculatedFrom : calculatedValue.getCalculatedFrom().getAsList())
        {
          if (calculatedFrom.getSetId().equals(valueSet.getId())
              && providedPhysicalQuantitySet.contains(calculatedFrom.getPhysicalQuantity()))
          {
            calculatedFromProvidedValue = true;
            break;
          }
        }
        if (!calculatedFromProvidedValue)
        {
          return providedQuantityValue.getPhysicalQuantity();
        }
      }
    }

    return null;
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

  public Set<PhysicalQuantity> getAvailableQuantities(ValueSet valueSet, PhysicalQuantity providedQuantity)
  {
    Set<PhysicalQuantity> result = new HashSet<>();
    Set<PhysicalQuantity> relatedQuantities = getRelatedQuantities();
    for (PhysicalQuantity relatedQuantity : relatedQuantities)
    {
      PhysicalQuantityValue knownValue = valueSet.getKnownQuantityValue(relatedQuantity);
      if (knownValue == null || knownValue.isTrial())
      {
        result.add(relatedQuantity);
      }
    }
    result.remove(providedQuantity);
    return result;
  }

  public Set<PhysicalQuantityValue> getKnownRelatedQuantities(ValueSet valueSet)
  {
    Set<PhysicalQuantityValue> result = new HashSet<>();
    for (PhysicalQuantity relatedQuantity : getRelatedQuantities())
    {
      PhysicalQuantityValue knownValue = valueSet.getKnownQuantityValue(relatedQuantity);
      if (knownValue != null)
      {
        result.add(knownValue);
      }
    }
    return result;
  }

  public Set<PhysicalQuantity> getUnknownOrTrialRelatedQuantities(ValueSet valueSet)
  {
    Set<PhysicalQuantity> result = new HashSet<>();
    for (PhysicalQuantity relatedQuantity : getRelatedQuantities())
    {
      PhysicalQuantityValue knownValue = valueSet.getKnownQuantityValue(relatedQuantity);
      if (knownValue == null || knownValue.isTrial())
      {
        result.add(relatedQuantity);
      }
    }
    return result;
  }

  public PhysicalQuantityValues getNonmatchingFixedQuantities(ValueSet valueSet)
  {
    PhysicalQuantityValues result = new PhysicalQuantityValues();
    for (PhysicalQuantityValue fixedQuantityValue : fixedQuantities.getAsList())
    {
      if (!fixedQuantityValue.equals(valueSet.getKnownQuantityValue(fixedQuantityValue.getPhysicalQuantity())))
      {
        result.setValue(fixedQuantityValue);
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
