package com.github.thomasfox.wingcalculator.calculate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.github.thomasfox.wingcalculator.calculate.impl.ApparentWindDirectionCalculator;
import com.github.thomasfox.wingcalculator.calculate.impl.ApparentWindSpeedCalculator;
import com.github.thomasfox.wingcalculator.calculate.impl.BendingCalculator;
import com.github.thomasfox.wingcalculator.calculate.impl.DrivingForceCalculator;
import com.github.thomasfox.wingcalculator.calculate.impl.InducedDragCalculator;
import com.github.thomasfox.wingcalculator.calculate.impl.InducedDragCoefficientCalculator;
import com.github.thomasfox.wingcalculator.calculate.impl.LateralForceCalculator;
import com.github.thomasfox.wingcalculator.calculate.impl.LeverFromWeightCalculator;
import com.github.thomasfox.wingcalculator.calculate.impl.LiftCalculator;
import com.github.thomasfox.wingcalculator.calculate.impl.LiftCoefficientCalculator;
import com.github.thomasfox.wingcalculator.calculate.impl.ProfileDragCalculator;
import com.github.thomasfox.wingcalculator.calculate.impl.ReynoldsNumberCalculator;
import com.github.thomasfox.wingcalculator.calculate.impl.SecondMomentOfAreaCalculator;
import com.github.thomasfox.wingcalculator.calculate.impl.ThicknessCalculator;
import com.github.thomasfox.wingcalculator.calculate.impl.TorqueCalculator;
import com.github.thomasfox.wingcalculator.calculate.impl.TotalDragCalculator;
import com.github.thomasfox.wingcalculator.calculate.impl.WingDepthFromSecondMomentOfAreaCalculator;
import com.github.thomasfox.wingcalculator.interpolate.Interpolator;
import com.github.thomasfox.wingcalculator.interpolate.InterpolatorException;
import com.github.thomasfox.wingcalculator.interpolate.QuantityRelations;
import com.github.thomasfox.wingcalculator.interpolate.SimpleXYPoint;
import com.github.thomasfox.wingcalculator.interpolate.XYPoint;

public class CombinedCalculator
{
  private final List<Calculator> calculators = new ArrayList<>();

  private final List<QuantityRelations> quantityRelationsList = new ArrayList<>();

  private final Interpolator interpolator = new Interpolator();

  public CombinedCalculator(List<QuantityRelations> quantityRelationsList)
  {
    calculators.add(new ReynoldsNumberCalculator());
    calculators.add(new InducedDragCoefficientCalculator());
    calculators.add(new InducedDragCalculator());
    calculators.add(new BendingCalculator());
    calculators.add(new SecondMomentOfAreaCalculator());
    calculators.add(new LiftCoefficientCalculator());
    calculators.add(new ThicknessCalculator());
    calculators.add(new WingDepthFromSecondMomentOfAreaCalculator());
    calculators.add(new ProfileDragCalculator());
    calculators.add(new ApparentWindDirectionCalculator());
    calculators.add(new ApparentWindSpeedCalculator());
    calculators.add(new LiftCalculator());
    calculators.add(new TotalDragCalculator());
    calculators.add(new LateralForceCalculator());
    calculators.add(new DrivingForceCalculator());
    calculators.add(new TorqueCalculator());
    calculators.add(new LeverFromWeightCalculator());

    this.quantityRelationsList.addAll(quantityRelationsList);
  }

  public boolean calculate(NamedValueSet store)
  {
    boolean changedOverall = false;
    boolean changedInCurrentIteration;
    int cutoff = 100;
    Map<PhysicalQuantity, Double> allKnownValues = new HashMap<>();
    for (PhysicalQuantityValue physicalQuantityValue : store.getKnownValues().getAsList())
    {
      allKnownValues.put(physicalQuantityValue.getPhysicalQuantity(), physicalQuantityValue.getValue());
    }
    do
    {
      changedInCurrentIteration = false;
      for (Calculator calculator: calculators)
      {
        if (!calculator.areNeededQuantitiesPresent(allKnownValues))
        {
          continue;
        }
        if (calculator.isOutputPresent(allKnownValues))
        {
          continue;
        }
        double calculationResult = calculator.calculate(allKnownValues);
        allKnownValues.put(calculator.getOutputQuantity(), calculationResult);
        store.setCalculatedValueNoOverwrite(calculator.getOutputQuantity(), calculationResult);
        changedInCurrentIteration = true;
        changedOverall = true;
      }

      // from here use quantityRelationsList to calculate unknown values

      Entry<PhysicalQuantity, Set<Double>> singleNonmatchingFixedQuantityWithValues
          = getSingleNonmatchingQuantityWithValues(allKnownValues);
      if (singleNonmatchingFixedQuantityWithValues == null)
      {
        cutoff--;
        continue;
      }

      PhysicalQuantity nonMatchingQuantity = singleNonmatchingFixedQuantityWithValues.getKey();
      Double knownValueForNonMatchingQuantity = nonMatchingQuantity.getValueFromAvailableQuantities(allKnownValues);
      if (knownValueForNonMatchingQuantity == null)
      {
        // a fixed quantity is unknown
        cutoff--;
        continue;
      }

      Map<PhysicalQuantity, List<XYPoint>> interpolationValues
          = getInterpolationValuesForQuantityFromQuantityRelations(
              nonMatchingQuantity,
              allKnownValues);

      for (Map.Entry<PhysicalQuantity, List<XYPoint>> entry : interpolationValues.entrySet())
      {
        Double interpolatedValue = getInterpolatedValueFor(
            entry.getKey(),
            knownValueForNonMatchingQuantity,
            entry.getValue());
        if (interpolatedValue != null)
        {
          allKnownValues.put(entry.getKey(), interpolatedValue);
          store.setCalculatedValueNoOverwrite(entry.getKey(), interpolatedValue);
          changedInCurrentIteration = true;
          changedOverall = true;
        }
      }
      cutoff--;
    }
    while(changedInCurrentIteration && cutoff > 0);
    return changedOverall;
  }

  private Double getInterpolatedValueFor(PhysicalQuantity quantityToInterpolate, Double xValue, List<XYPoint> from)
  {
    if (from.size() == 0)
    {
      System.out.println("No points to interpolate " + quantityToInterpolate.getDisplayName());
      return null;
    }
    else if (from.size() == 1)
    {
      System.out.println("Only one point to interpolate " + quantityToInterpolate.getDisplayName()
          + ", using " + from.get(0).getX() + " instead of " + xValue);
      return from.get(0).getY();
    }
    else
    {
      double interpolated = Double.NaN;
      try
      {
        interpolated = interpolator.interpolate(xValue, from);
      }
      catch (Exception e)
      {
        // probably outside interpolation interval, try find best-matching value
        double distance = Double.POSITIVE_INFINITY;
        double usedValue = Double.NaN;
        for (XYPoint point : from)
        {
          double currentDistance = Math.abs(xValue - point.getX());
          if (currentDistance < distance)
          {
            distance = currentDistance;
            interpolated = point.getY();
            usedValue = point.getX();
          }
        }
        System.out.println("No matching interval to interpolate " + quantityToInterpolate.getDisplayName()
            + ", using " + usedValue + " instead of " + xValue);
      }
      return interpolated;
    }
  }

  private Map<PhysicalQuantity, List<XYPoint>> getInterpolationValuesForQuantityFromQuantityRelations(
      PhysicalQuantity quantityToInterpolate,
      Map<PhysicalQuantity, Double> allKnownValues)
  {
    Map<PhysicalQuantity, List<XYPoint>> interpolationValues = new HashMap<>();
    for (QuantityRelations quantityRelations : quantityRelationsList)
    {
      if (!quantityRelationsOkForInterpolation(
          quantityToInterpolate, allKnownValues, quantityRelations))
      {
        continue;
      }

      for (PhysicalQuantity keyQuantity : quantityRelations.getRelatedQuantities())
      {
        Double knownKeyValue = keyQuantity.getValueFromAvailableQuantities(allKnownValues);
        if (knownKeyValue != null)
        {
          for (PhysicalQuantity relatedQuantity : quantityRelations.getRelatedQuantities())
          {
            Double knownRelatedValue = relatedQuantity.getValueFromAvailableQuantities(allKnownValues);
            if (knownRelatedValue == null)
            {
              try
              {
                Double relatedValue = quantityRelations.interpolateValueFrom(relatedQuantity, keyQuantity, knownKeyValue);
                List<XYPoint> relatedQuantityInterpolationList = interpolationValues.get(relatedQuantity);
                if (relatedQuantityInterpolationList == null)
                {
                  relatedQuantityInterpolationList = new ArrayList<>();
                  interpolationValues.put(relatedQuantity, relatedQuantityInterpolationList);
                }
                relatedQuantityInterpolationList.add(new SimpleXYPoint(
                    quantityRelations.getFixedQuantities().get(quantityToInterpolate),
                    relatedValue));
              }
              catch (InterpolatorException e)
              {
                System.out.println("Could not calculate " + relatedQuantity.getDisplayName()
                + " from quantityRelations " + quantityRelations.getName()
                + " with fixed quantities " + quantityRelations.printFixedQuantities());
              }
            }
          }
        }
      }
    }
    return interpolationValues;
  }

  /**
   * Returns true only if the quantity to interpolate is the only
   * quantity in the fixed values which is not equal to a fixed value.
   */
  private boolean quantityRelationsOkForInterpolation(
      PhysicalQuantity quantityToInterpolate,
      Map<PhysicalQuantity, Double> allKnownValues,
      QuantityRelations quantityRelations)
  {
    Map<PhysicalQuantity, Double> fixedQuantities
        = quantityRelations.getFixedQuantities();
    boolean okForInterpolation = true;
    for (Map.Entry<PhysicalQuantity, Double> fixedQuantity : fixedQuantities.entrySet())
    {
      if (!fixedQuantity.getKey().equals(quantityToInterpolate)
          && !fixedQuantity.getKey().getValueFromAvailableQuantities(allKnownValues).equals(fixedQuantity.getValue()))
      {
        okForInterpolation = false;
        break;
      }
    }
    return okForInterpolation;
  }

  /**
   * Checks the fixed values in the known quantityRelationList.
   * The fixed values are checked against the known values.
   * If a fixed value is equal to a known value, it is marked.
   * When all fixed values are passed, the list of fixed values without match
   * is determined. If it contains more than one quantity, null is returned,
   * otherwise the matching quantity .
   *
   * NOTE: This method is ok for the profile calculations,
   * it does not work if known quantities change between quantityRelations
   * or if all quantities match exactly in a quantityRelations.
   *
   * @return the single nonmatching quantity, along with the occurances of fixed values
   *         in the quantityRelationsList, or null if no match is found.
   */
  private Map.Entry<PhysicalQuantity, Set<Double>> getSingleNonmatchingQuantityWithValues(
      Map<PhysicalQuantity, Double> allKnownValues)
  {
    Map<PhysicalQuantity, Set<Double>> fixedQuantitiesOccurances = new HashMap<>();
    Set<PhysicalQuantity> fixedQuantitiesWithMatches = new HashSet<>();
    for (QuantityRelations quantityRelations : quantityRelationsList)
    {
      Map<PhysicalQuantity, Double> fixedQuantities
          = quantityRelations.getFixedQuantities();
      for (Map.Entry<PhysicalQuantity, Double> fixedQuantity : fixedQuantities.entrySet())
      {
        Set<Double> quantityValues = fixedQuantitiesOccurances.get(fixedQuantity.getKey());
        if (quantityValues == null)
        {
          quantityValues = new HashSet<>();
          fixedQuantitiesOccurances.put(fixedQuantity.getKey(), quantityValues);
        }
        quantityValues.add(fixedQuantity.getValue());
        Double knownValue = fixedQuantity.getKey().getValueFromAvailableQuantities(allKnownValues);
        if (knownValue != null && knownValue.equals(fixedQuantity.getValue()))
        {
          fixedQuantitiesWithMatches.add(fixedQuantity.getKey());
        }
      }
    }

    if (fixedQuantitiesOccurances.size() - fixedQuantitiesWithMatches.size() > 1)
    {
      System.out.println("not enough matching quantities for " + quantityRelationsList);
      return null;
    }

    for (PhysicalQuantity matchingQuantity : fixedQuantitiesWithMatches)
    {
      fixedQuantitiesOccurances.remove(matchingQuantity);
    }

    if (fixedQuantitiesOccurances.size() != 1)
    {
      if (fixedQuantitiesOccurances.size() != 0)
      {
        System.out.println("Matching quantities for " + quantityRelationsList + " are " + fixedQuantitiesOccurances.size());
      }
      return null;
    }
    return fixedQuantitiesOccurances.entrySet().iterator().next();
  }
}
