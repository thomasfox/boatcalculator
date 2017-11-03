package com.github.thomasfox.sailboatcalculator.calculate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.github.thomasfox.sailboatcalculator.calculate.impl.ApparentWindDirectionCalculator;
import com.github.thomasfox.sailboatcalculator.calculate.impl.ApparentWindSpeedCalculator;
import com.github.thomasfox.sailboatcalculator.calculate.impl.AreaCalculator;
import com.github.thomasfox.sailboatcalculator.calculate.impl.BendingCalculator;
import com.github.thomasfox.sailboatcalculator.calculate.impl.DrivingForceCalculator;
import com.github.thomasfox.sailboatcalculator.calculate.impl.InducedDragCalculator;
import com.github.thomasfox.sailboatcalculator.calculate.impl.InducedDragCoefficientCalculator;
import com.github.thomasfox.sailboatcalculator.calculate.impl.LateralForceCalculator;
import com.github.thomasfox.sailboatcalculator.calculate.impl.LeverFromWeightCalculator;
import com.github.thomasfox.sailboatcalculator.calculate.impl.LiftCalculator;
import com.github.thomasfox.sailboatcalculator.calculate.impl.LiftCoefficientCalculator;
import com.github.thomasfox.sailboatcalculator.calculate.impl.ProfileDragCalculator;
import com.github.thomasfox.sailboatcalculator.calculate.impl.ReynoldsNumberCalculator;
import com.github.thomasfox.sailboatcalculator.calculate.impl.SailingAngleCalculator;
import com.github.thomasfox.sailboatcalculator.calculate.impl.SecondMomentOfAreaCalculator;
import com.github.thomasfox.sailboatcalculator.calculate.impl.ThicknessCalculator;
import com.github.thomasfox.sailboatcalculator.calculate.impl.TorqueCalculator;
import com.github.thomasfox.sailboatcalculator.calculate.impl.TotalDragCalculator;
import com.github.thomasfox.sailboatcalculator.calculate.impl.TotalDragCoefficientCalculator;
import com.github.thomasfox.sailboatcalculator.calculate.impl.VMGCalculator;
import com.github.thomasfox.sailboatcalculator.calculate.impl.WingChordFromSecondMomentOfAreaCalculator;
import com.github.thomasfox.sailboatcalculator.calculate.value.NamedValueSet;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValues;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValuesWithSetNamePerValue;
import com.github.thomasfox.sailboatcalculator.interpolate.Interpolator;
import com.github.thomasfox.sailboatcalculator.interpolate.QuantityRelations;
import com.github.thomasfox.sailboatcalculator.interpolate.SimpleXYPoint;

import lombok.NonNull;

public class CombinedCalculator
{
  private final List<Calculator> calculators = new ArrayList<>();

  private final List<QuantityRelations> quantityRelationsList = new ArrayList<>();

  private final Interpolator interpolator = new Interpolator();

  public CombinedCalculator(List<QuantityRelations> quantityRelationsList)
  {
    calculators.add(new AreaCalculator());
    calculators.add(new ReynoldsNumberCalculator());
    calculators.add(new InducedDragCoefficientCalculator());
    calculators.add(new InducedDragCalculator());
    calculators.add(new BendingCalculator());
    calculators.add(new SecondMomentOfAreaCalculator());
    calculators.add(new LiftCoefficientCalculator());
    calculators.add(new ThicknessCalculator());
    calculators.add(new WingChordFromSecondMomentOfAreaCalculator());
    calculators.add(new ProfileDragCalculator());
    calculators.add(new ApparentWindDirectionCalculator());
    calculators.add(new ApparentWindSpeedCalculator());
    calculators.add(new LiftCalculator());
    calculators.add(new TotalDragCoefficientCalculator());
    calculators.add(new TotalDragCalculator());
    calculators.add(new LateralForceCalculator());
    calculators.add(new DrivingForceCalculator());
    calculators.add(new TorqueCalculator());
    calculators.add(new LeverFromWeightCalculator());
    calculators.add(new VMGCalculator());
    calculators.add(new SailingAngleCalculator());

    this.quantityRelationsList.addAll(quantityRelationsList);
  }

  public boolean calculate(NamedValueSet namedValueSet)
  {
    boolean changedOverall = false;
    boolean changedInCurrentIteration;
    int cutoff = 100;
    do
    {
      changedInCurrentIteration = false;
      for (Calculator calculator: calculators)
      {
        if (!calculator.areNeededQuantitiesPresent(namedValueSet.getKnownValues()))
        {
          continue;
        }
        if (calculator.isOutputPresent(namedValueSet.getKnownValues()))
        {
          continue;
        }
        double calculationResult = calculator.calculate(namedValueSet.getKnownValues());
        namedValueSet.setCalculatedValueNoOverwrite(
            calculator.getOutputQuantity(),
            calculationResult,
            calculator.getClass().getSimpleName(),
            namedValueSet.getKnownValuesAsArray(calculator.getInputQuantities()));
        changedInCurrentIteration = true;
        changedOverall = true;
      }

      // from here use quantityRelationsList to calculate unknown values

      Map<PhysicalQuantityValues, QuantityRelations> fixedValueInterpolationCandidates
          = getFixedValueInterpolationCandidates(namedValueSet.getKnownValues());
      if (fixedValueInterpolationCandidates.isEmpty())
      {
        cutoff--;
        continue;
      }

      Entry<PhysicalQuantityValues, QuantityRelations> firstEntry
          = fixedValueInterpolationCandidates.entrySet().iterator().next();

      PhysicalQuantityValues nonmatchingFixedValues = firstEntry.getKey();

      if (nonmatchingFixedValues.size() == 0)
      {
        // no interpolation necessary, direct hit
        for (QuantityRelations quantityRelations : fixedValueInterpolationCandidates.values())
        {
          boolean changed = setValuesFromQuantityRelations(
              namedValueSet,
              quantityRelations);
          changedInCurrentIteration = changedInCurrentIteration || changed;
          changedOverall = changedOverall || changed;
        }
      }
      else if (nonmatchingFixedValues.size() == 1)
      {
        // only one nonmatching value -> try to interpolate
        PhysicalQuantityValue nonmatchingValue = null;

        // check whether any nonmatching value is known at all
        for (PhysicalQuantityValue valueCandidate : nonmatchingFixedValues.getAsList())
        {
          nonmatchingValue = namedValueSet.getKnownValue(valueCandidate.getPhysicalQuantity());
          if (nonmatchingValue != null)
          {
            break;
          }
        }
        if (nonmatchingValue == null)
        {
          // no nonmatching value is known now
          continue;
        }

        Map<PhysicalQuantityValue, QuantityRelations> fixedValueInterpolationRelations
            = getFixedValueInterpolationRelations(
                fixedValueInterpolationCandidates,
                nonmatchingValue);

        if (fixedValueInterpolationRelations.size() == 1)
        {
          // no interpolation possible, outside interval or only one point available
          PhysicalQuantityValue usedValue = fixedValueInterpolationRelations.keySet().iterator().next();
          QuantityRelations quantityRelations = fixedValueInterpolationRelations.values().iterator().next();
          System.out.println("Only one point to interpolate "
              + usedValue.getPhysicalQuantity().getDisplayName()
              + ", using " + usedValue.getValue() + " instead of " + nonmatchingValue.getValue());

          boolean changed = setValuesFromQuantityRelations(
              namedValueSet,
              quantityRelations);
          changedInCurrentIteration = changedInCurrentIteration || changed;
          changedOverall = changedOverall || changed;
        }
        else
        {
          // Interpolation
          Double x1 = null;
          Double x2 = null;
          PhysicalQuantityValues yValues1 = null;
          PhysicalQuantityValues yValues2 = null;
          String name1 = null;
          String name2 = null;
          for (Map.Entry<PhysicalQuantityValue, QuantityRelations> interpolationRelationEntry : fixedValueInterpolationRelations.entrySet())
          {
            if (x1 == null)
            {
              x1 = interpolationRelationEntry.getKey().getValue();
              yValues1 = interpolationRelationEntry.getValue().getRelatedQuantityValues(namedValueSet.getKnownValues());
              name1 = interpolationRelationEntry.getValue().getName();
            }
            else if (x2 == null)
            {
              x2 = interpolationRelationEntry.getKey().getValue();
              yValues2 = interpolationRelationEntry.getValue().getRelatedQuantityValues(namedValueSet.getKnownValues());
              name2 = interpolationRelationEntry.getValue().getName();
            }
            else
            {
              throw new IllegalArgumentException("Too many entries in fixedValueInterpolationRelations : " + fixedValueInterpolationRelations);
            }
          }
          for (PhysicalQuantityValue y1 : yValues1.getAsList())
          {
            Double y2 = yValues2.getValue(y1.getPhysicalQuantity());
            if (y2 != null)
            {
              double y = interpolator.interpolateY(
                  nonmatchingValue.getValue(),
                  new SimpleXYPoint(x1, y1.getValue()),
                  new SimpleXYPoint(x2, y2));
              namedValueSet.setCalculatedValue(
                  y1.getPhysicalQuantity(),
                  y,
                  name1 + " and " + name2); // todo set origin
            }
          }
        }
      }
      cutoff--;
    }
    while(changedInCurrentIteration && cutoff > 0);
    return changedOverall;
  }

  private boolean setValuesFromQuantityRelations(NamedValueSet namedValueSet, QuantityRelations quantityRelations)
  {
    boolean changed = false;
    Set<PhysicalQuantity> usedQuantities
        = quantityRelations.getKnownRelatedQuantities(namedValueSet.getKnownValues());
    usedQuantities.addAll(quantityRelations.getFixedQuantities().getContainedQuantities());
    PhysicalQuantityValues relatedQuantities
        = quantityRelations.getRelatedQuantityValues(namedValueSet.getKnownValues());
    for (PhysicalQuantityValue physicalQuantityValue : relatedQuantities.getAsList())
    {
      namedValueSet.setCalculatedValueNoOverwrite(
          physicalQuantityValue.getPhysicalQuantity(),
          physicalQuantityValue.getValue(),
          quantityRelations.getName(),
          new PhysicalQuantityValuesWithSetNamePerValue(namedValueSet.getKnownValues(usedQuantities), namedValueSet.getName()));
      changed = true;
    }
    return changed;
  }

  /**
   * Returns the quantity relations with the minimal number of nonmatching fixed quantities.
   * Nonmatching means that either the fixed value in the qunatity relations is not equal to the known value
   * or that the fixed value is not in the known values.
   *
   * @param knownValues the known values.
   *
   * @return the quantity relations with the minimal number of nonmatching fixed quantities, not null, may be empty.
   */
  private Map<PhysicalQuantityValues, QuantityRelations> getFixedValueInterpolationCandidates(
      PhysicalQuantityValues knownValues)
  {
    int minimalNumberOfNonmatchingQuantities = Integer.MAX_VALUE;
    Map<PhysicalQuantityValues, QuantityRelations> interpolationCandidates = new HashMap<>();
    for (QuantityRelations quantityRelations : quantityRelationsList)
    {
      PhysicalQuantityValues nonmatchingFixedValues
          = quantityRelations.getNonmatchingFixedQuantities(knownValues);
      int numberOfMissingQuantities = nonmatchingFixedValues.size();
      if (numberOfMissingQuantities <= minimalNumberOfNonmatchingQuantities
          && quantityRelations.getUnknownRelatedQuantities(knownValues).size() > 0)
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

  /**
   * Gets the quantity relations which can really be used for interpolation
   *
   * @param fixedValueInterpolationCandidates the quantity relations which can be used for interpolation,
   *        not empty, keys must have size 1.
   * @return the interpolation quantity relations,
   *         of size 0 if no matching entries are found to interpolate,
   *         or of size 1 if no interpolation is possible
   *         or of size 2 if interpolation is possible.
   */
  private Map<PhysicalQuantityValue, QuantityRelations> getFixedValueInterpolationRelations(
      @NonNull Map<PhysicalQuantityValues, QuantityRelations> fixedValueInterpolationCandidates,
      @NonNull PhysicalQuantityValue knownValueForInterpolation)
  {
    Double minInterpolationQuantity = null;
    Double maxInterpolationQuantity = null;
    QuantityRelations minInterpolationRelations = null;
    QuantityRelations maxInterpolationRelations = null;
    for (Map.Entry<PhysicalQuantityValues, QuantityRelations> fixedValueInterpolationCandidate
        : fixedValueInterpolationCandidates.entrySet())
    {
      PhysicalQuantityValue nonmatchingValue = fixedValueInterpolationCandidate.getKey().getAsList().get(0);
      if (nonmatchingValue.getPhysicalQuantity() != knownValueForInterpolation.getPhysicalQuantity())
      {
        continue;
      }
      if (minInterpolationQuantity == null)
      {
        minInterpolationQuantity = nonmatchingValue.getValue();
        maxInterpolationQuantity = nonmatchingValue.getValue();
        minInterpolationRelations = fixedValueInterpolationCandidate.getValue();
        maxInterpolationRelations = minInterpolationRelations;
        continue;
      }

      if (nonmatchingValue.getValue() > minInterpolationQuantity
          && nonmatchingValue.getValue() <= knownValueForInterpolation.getValue())
      {
        minInterpolationQuantity = nonmatchingValue.getValue();
        minInterpolationRelations = fixedValueInterpolationCandidate.getValue();
        if (minInterpolationQuantity > maxInterpolationQuantity)
        {
          maxInterpolationQuantity = nonmatchingValue.getValue();
          maxInterpolationRelations = fixedValueInterpolationCandidate.getValue();
        }

      }

      if (nonmatchingValue.getValue() < maxInterpolationQuantity
          && nonmatchingValue.getValue() >= knownValueForInterpolation.getValue())
      {
        maxInterpolationQuantity = nonmatchingValue.getValue();
        maxInterpolationRelations = fixedValueInterpolationCandidate.getValue();
        if (minInterpolationQuantity > maxInterpolationQuantity)
        {
          minInterpolationQuantity = nonmatchingValue.getValue();
          minInterpolationRelations = fixedValueInterpolationCandidate.getValue();
        }
      }
    }
    Map<PhysicalQuantityValue, QuantityRelations> result = new HashMap<>();
    result.put(
        new PhysicalQuantityValue(knownValueForInterpolation.getPhysicalQuantity(), minInterpolationQuantity),
        minInterpolationRelations);
    if (maxInterpolationRelations != minInterpolationRelations)
    {
      result.put(
          new PhysicalQuantityValue(knownValueForInterpolation.getPhysicalQuantity(), maxInterpolationQuantity),
          maxInterpolationRelations);
    }
    return result;
  }
}
