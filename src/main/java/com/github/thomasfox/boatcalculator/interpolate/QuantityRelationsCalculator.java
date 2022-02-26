package com.github.thomasfox.boatcalculator.interpolate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.thomasfox.boatcalculator.calculate.CalculationResult;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.value.CalculatedPhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.CalculatedPhysicalQuantityValues;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValues;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValuesWithSetIdPerValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuantityRelationsCalculator
{
  private final Interpolator interpolator = new Interpolator();

  private final List<QuantityRelation> quantityRelationsList = new ArrayList<>();

  public void setQuantityRelations(List<QuantityRelation> quantityRelationsList)
  {
    this.quantityRelationsList.clear();
    this.quantityRelationsList.addAll(quantityRelationsList);
  }

  public Set<String> applyQuantityRelations(ValueSet valueSet)
  {
    Map<PhysicalQuantityValues, QuantityRelation> fixedValueInterpolationCandidates
        = getFixedValueInterpolationCandidates(valueSet);
    if (fixedValueInterpolationCandidates.isEmpty())
    {
      return Set.of();
    }

    Entry<PhysicalQuantityValues, QuantityRelation> firstEntry
        = fixedValueInterpolationCandidates.entrySet().iterator().next();

    PhysicalQuantityValues nonmatchingFixedValues = firstEntry.getKey();

    if (nonmatchingFixedValues.size() == 0)
    {
      // no interpolation of fixed value necessary, direct hit
      return setValueFromQuantityRelations(
          valueSet,
          fixedValueInterpolationCandidates.values());
    }

    if (nonmatchingFixedValues.size() == 1)
    {
      return interpolateNonmatchingValueAndApply(
          nonmatchingFixedValues,
          fixedValueInterpolationCandidates,
          valueSet);
    }

    return Set.of();
  }

  /**
   * Returns the quantity relations with the minimal number of nonmatching fixed quantities.
   * Nonmatching means that either the fixed value in the quantity relations is not equal to the known value
   * or that the fixed value is not in the known values.
   *
   * @param valueSet the values to operate on.
   *
   * @return the quantity relations with the minimal number of nonmatching fixed quantities,
   *         not null, may be empty.
   *         The keys of the map contains the nonmatching fixed quantities
   *         The values of the map contains the quantity relations which has the nonmatching fixed quantities
   *         in the corresponding key.
   */
  protected Map<PhysicalQuantityValues, QuantityRelation> getFixedValueInterpolationCandidates(
      ValueSet valueSet)
  {
    int minimalNumberOfNonmatchingQuantities = Integer.MAX_VALUE;
    Map<PhysicalQuantityValues, QuantityRelation> interpolationCandidates = new HashMap<>();
    for (QuantityRelation quantityRelations : quantityRelationsList)
    {
      PhysicalQuantityValues nonmatchingFixedValues
          = quantityRelations.getNonmatchingFixedQuantities(valueSet);
      int numberOfMissingQuantities = nonmatchingFixedValues.size();
      if (numberOfMissingQuantities <= minimalNumberOfNonmatchingQuantities
          && quantityRelations.getUnknownOrTrialRelatedQuantities(valueSet).size() > 0)
      {
        if (numberOfMissingQuantities < minimalNumberOfNonmatchingQuantities)
        {
          interpolationCandidates.clear();
        }
        minimalNumberOfNonmatchingQuantities = numberOfMissingQuantities;
        interpolationCandidates.put(nonmatchingFixedValues, quantityRelations);
      }
    }

    return interpolationCandidates;
  }

  private Set<String> interpolateNonmatchingValueAndApply(
      PhysicalQuantityValues nonmatchingFixedValues,
      Map<PhysicalQuantityValues, QuantityRelation> fixedValueInterpolationCandidates,
      ValueSet valueSet)
  {
    PhysicalQuantityValue nonmatchingValue = getNonmatchingKnownValue(
        valueSet, nonmatchingFixedValues);

    if (nonmatchingValue == null)
    {
      return Set.of();
    }

    Map<PhysicalQuantityValue, QuantityRelation> fixedValueInterpolationRelations
        = getFixedValueInterpolationRelations(
            fixedValueInterpolationCandidates,
            nonmatchingValue);

    if (fixedValueInterpolationRelations.size() == 1)
    {
      // no interpolation possible, outside interval or only one point available
      PhysicalQuantityValue usedValue = fixedValueInterpolationRelations.keySet().iterator().next();
      QuantityRelation quantityRelation = fixedValueInterpolationRelations.values().iterator().next();
      log.info("Only one point to interpolate "
          + usedValue.getPhysicalQuantity().getDisplayName()
          + ", using " + usedValue.getValue() + " instead of " + nonmatchingValue.getValue());

      return setValuesFromQuantityRelation(
          valueSet,
          quantityRelation);
    }
    return interpolateNonmatchingValueAndApply(
        nonmatchingValue,
        fixedValueInterpolationRelations,
        valueSet);
  }

  protected Set<String> interpolateNonmatchingValueAndApply(
      PhysicalQuantityValue nonmatchingValue,
      Map<PhysicalQuantityValue, QuantityRelation> fixedValueInterpolationRelations,
      ValueSet valueSet)
  {
    // Interpolation
    Double x1 = null;
    Double x2 = null;
    CalculatedPhysicalQuantityValues yValues1 = null;
    CalculatedPhysicalQuantityValues yValues2 = null;
    String name1 = null;
    String name2 = null;
    boolean trial = false;
    Set<PhysicalQuantity> usedQuantities = new HashSet<>();
    for (Map.Entry<PhysicalQuantityValue, QuantityRelation> interpolationRelationEntry : fixedValueInterpolationRelations.entrySet())
    {
      QuantityRelation quantityRelation = interpolationRelationEntry.getValue();
      CalculatedPhysicalQuantityValues calculatedPhysicalQuantityValues
          = quantityRelation.getRelatedQuantityValues(valueSet);
      if (x1 == null)
      {
        x1 = interpolationRelationEntry.getKey().getValue();
        yValues1 = calculatedPhysicalQuantityValues;
        name1 = quantityRelation.getName();
      }
      else if (x2 == null)
      {
        x2 = interpolationRelationEntry.getKey().getValue();
        yValues2 = calculatedPhysicalQuantityValues;
        name2 = quantityRelation.getName();
      }
      else
      {
        throw new IllegalArgumentException("Too many entries in fixedValueInterpolationRelations : " + fixedValueInterpolationRelations);
      }
      trial |= interpolationRelationEntry.getKey().isTrial()
          || isAFixedValueTrial(valueSet, quantityRelation)
          || isACalculatedValueTrial(yValues1)
          || isACalculatedValueTrial(yValues2);
      usedQuantities = getUsedQuantities(valueSet, quantityRelation, calculatedPhysicalQuantityValues);
    }
    Set<String> changed = new HashSet<>();
    for (PhysicalQuantityValue y1 : yValues1.getAsList())
    {
      Double y2 = yValues2.getValue(y1.getPhysicalQuantity());
      if (y2 != null)
      {
        double y = interpolator.interpolateY(
            nonmatchingValue.getValue(),
            new SimpleXYPoint(x1, y1.getValue()),
            new SimpleXYPoint(x2, y2));

        Double oldResult = Optional.ofNullable(valueSet.getKnownQuantityValue(y1.getPhysicalQuantity()))
            .map(PhysicalQuantityValue::getValue)
            .orElse(null);
        CalculationResult calculationResult = new CalculationResult(
            y,
            oldResult,
            trial);

        valueSet.setCalculatedValueNoOverwrite(
            new SimplePhysicalQuantityValue(y1.getPhysicalQuantity(), y),
            name1 + " and " + name2,
            trial,
            new PhysicalQuantityValuesWithSetIdPerValue(
                valueSet.getKnownValues(usedQuantities),
                valueSet.getId()));
        if (!calculationResult.relativeDifferenceIsBelowThreshold())
        {
          changed.add(valueSet.getId() + ":" + fixedValueInterpolationRelations.values().iterator().next().getName());
        }
      }
    }
    return changed;
  }

  private Set<PhysicalQuantity> getUsedQuantities(ValueSet valueSet,
      QuantityRelation quantityRelation,
      CalculatedPhysicalQuantityValues calculatedPhysicalQuantityValues)
  {
    Set<PhysicalQuantity> usedQuantities = quantityRelation.getKnownRelatedQuantities(valueSet).stream()
        .map(PhysicalQuantityValue::getPhysicalQuantity)
        .collect(Collectors.toSet());
    for (CalculatedPhysicalQuantityValue calculatedValue : calculatedPhysicalQuantityValues.getAsList())
    {
      usedQuantities.remove(calculatedValue.getPhysicalQuantity());
    }
    usedQuantities.addAll(quantityRelation.getFixedQuantities().getContainedQuantities());
    return usedQuantities;
  }

  private PhysicalQuantityValue getNonmatchingKnownValue(
      ValueSet valueSet,
      PhysicalQuantityValues nonmatchingFixedValues)
  {
    // only one nonmatching value -> try to interpolate
    PhysicalQuantityValue nonmatchingValue = null;

    // check whether any nonmatching value is known at all
    for (PhysicalQuantityValue valueCandidate : nonmatchingFixedValues.getAsList())
    {
      nonmatchingValue = valueSet.getKnownQuantityValue(valueCandidate.getPhysicalQuantity());
      if (nonmatchingValue != null)
      {
        break;
      }
    }
    return nonmatchingValue;
  }

  private Set<String> setValueFromQuantityRelations(
      ValueSet valueSet,
      Collection<QuantityRelation> quantityRelations)
  {
    Set<String> changed = new HashSet<>();
    for (QuantityRelation quantityRelation : quantityRelations)
    {
      changed.addAll(setValuesFromQuantityRelation(
          valueSet,
          quantityRelation));
    }
    return changed;
  }

  protected Set<String> setValuesFromQuantityRelation(ValueSet valueSet, QuantityRelation quantityRelation)
  {
    boolean trial = isKeyTrial(valueSet, quantityRelation) || isAFixedValueTrial(valueSet, quantityRelation);

    Set<String> changed = new HashSet<>();
    CalculatedPhysicalQuantityValues relatedQuantities
        = quantityRelation.getRelatedQuantityValues(valueSet);
    Set<PhysicalQuantity> usedQuantities
        = getUsedQuantities(valueSet, quantityRelation, relatedQuantities);
    boolean calculatedValuesAreTrial = isACalculatedValueTrial(relatedQuantities);
    for (PhysicalQuantityValue physicalQuantityValue : relatedQuantities.getAsList())
    {

      Double oldResult = Optional.ofNullable(valueSet.getKnownQuantityValue(physicalQuantityValue.getPhysicalQuantity()))
          .map(PhysicalQuantityValue::getValue)
          .orElse(null);
      CalculationResult calculationResult = new CalculationResult(
          physicalQuantityValue.getValue(),
          oldResult,
          trial);

      valueSet.setCalculatedValueNoOverwrite(
          physicalQuantityValue,
          quantityRelation.getName(),
          trial || calculatedValuesAreTrial,
          new PhysicalQuantityValuesWithSetIdPerValue(valueSet.getKnownValues(usedQuantities), valueSet.getId()));
      if (!calculationResult.relativeDifferenceIsBelowThreshold())
      {
        changed.add(valueSet.getId() + ":" + quantityRelation.getName());
      }
    }
    return changed;
  }

  /**
   * Gets the quantity relations which can really be used for interpolation.
   *
   * @param fixedValueInterpolationCandidates the quantity relations which can be used for interpolation,
   *        not empty, keys must have size 1.
   * @return the interpolation quantity relations,
   *         of size 0 if no matching entries are found to interpolate,
   *         or of size 1 if no interpolation is possible
   *         or of size 2 if interpolation is possible.
   */
  private Map<PhysicalQuantityValue, QuantityRelation> getFixedValueInterpolationRelations(
      @NonNull Map<PhysicalQuantityValues, QuantityRelation> fixedValueInterpolationCandidates,
      @NonNull PhysicalQuantityValue knownValue)
  {
    Double minInterpolationValue = null;
    Double maxInterpolationValue = null;
    QuantityRelation minInterpolationRelations = null;
    QuantityRelation maxInterpolationRelations = null;
    for (Map.Entry<PhysicalQuantityValues, QuantityRelation> fixedValueInterpolationCandidate
        : fixedValueInterpolationCandidates.entrySet())
    {
      PhysicalQuantityValue candidateQuantityValue = fixedValueInterpolationCandidate.getKey().getAsList().get(0);
      if (candidateQuantityValue.getPhysicalQuantity() != knownValue.getPhysicalQuantity())
      {
        continue;
      }
      Double candidate = candidateQuantityValue.getValue();

      if (minInterpolationValue == null)
      {
        minInterpolationValue = candidate;
        maxInterpolationValue = candidate;
        minInterpolationRelations = fixedValueInterpolationCandidate.getValue();
        maxInterpolationRelations = minInterpolationRelations;
        continue;
      }

      if (candidate <= knownValue.getValue()
          && (candidate > minInterpolationValue
              || minInterpolationValue > knownValue.getValue()))
      {
        minInterpolationValue = candidate;
        minInterpolationRelations = fixedValueInterpolationCandidate.getValue();
      }
      if (candidate > knownValue.getValue() && candidate < minInterpolationValue)
      {
        minInterpolationValue = candidate;
        minInterpolationRelations = fixedValueInterpolationCandidate.getValue();
      }

      if (candidate >= knownValue.getValue()
          && (candidate < maxInterpolationValue
              || maxInterpolationValue < knownValue.getValue()))
      {
        maxInterpolationValue = candidate;
        maxInterpolationRelations = fixedValueInterpolationCandidate.getValue();
      }
      if (candidate < knownValue.getValue() && candidate > maxInterpolationValue)
      {
        maxInterpolationValue = candidate;
        maxInterpolationRelations = fixedValueInterpolationCandidate.getValue();
      }
    }
    Map<PhysicalQuantityValue, QuantityRelation> result = new HashMap<>();
    result.put(
        new SimplePhysicalQuantityValue(knownValue.getPhysicalQuantity(), minInterpolationValue),
        minInterpolationRelations);
    if (maxInterpolationRelations != minInterpolationRelations)
    {
      result.put(
          new SimplePhysicalQuantityValue(knownValue.getPhysicalQuantity(), maxInterpolationValue),
          maxInterpolationRelations);
    }
    return result;
  }

  private boolean isKeyTrial(ValueSet valueSet,
      QuantityRelation quantityRelations)
  {
    PhysicalQuantity keyQuantity = quantityRelations.getKeyQuantity();
    if (keyQuantity != null)
    {
      PhysicalQuantityValue knownValue = valueSet.getKnownQuantityValue(keyQuantity);
      if (knownValue != null)
      {
        return knownValue.isTrial();
      }
    }
    return false;
  }

  private boolean isAFixedValueTrial(ValueSet valueSet, QuantityRelation quantityRelations)
  {
    for (PhysicalQuantityValue fixedQuantityValue : quantityRelations.getFixedQuantities().getAsList())
    {
      PhysicalQuantityValue actualValue = valueSet.getKnownQuantityValue(fixedQuantityValue.getPhysicalQuantity());
      if (actualValue.isTrial())
      {
        return true;
      }
    }
    return false;
  }

  private boolean isACalculatedValueTrial(CalculatedPhysicalQuantityValues values)
  {
    if (values == null)
    {
      return false;
    }
    return values.getAsList().stream().anyMatch(e -> e.isTrial());
  }
}
