package com.github.thomasfox.boatcalculator.foil;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.calculate.impl.ReynoldsNumberCalculator;
import com.github.thomasfox.boatcalculator.interpolate.QuantityRelation;
import com.github.thomasfox.boatcalculator.interpolate.QuantityRelationLoader;
import com.github.thomasfox.boatcalculator.interpolate.QuantityRelationsCalculator;
import com.github.thomasfox.boatcalculator.value.CalculatedPhysicalQuantityValues;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValues;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.valueset.SimpleValueSet;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Slf4j
public class AveragedProfilePolar
{
  public static final String QUANTITY_RELATON_NAME = "averaged Polar";
  private final List<QuantityRelation> profilePolarQuantityRelations;

  private final QuantityRelation kinematicViscosityWaterRelation;

  private final double velocity;

  public AveragedProfilePolar(List<QuantityRelation> profilePolars, double velocity)
  {
    profilePolarQuantityRelations= new ArrayList<>(profilePolars);
    this.velocity = velocity;

    Reader kinematicViscosityWaterReader
        = new InputStreamReader(FoilOptimizer.class.getResourceAsStream("/kinematicViscosity_water.txt"));
    kinematicViscosityWaterRelation =
        new QuantityRelationLoader().load(kinematicViscosityWaterReader, "kinematicViscosityWater");
  }

  public QuantityRelation average(HalfFoilGeometry halfFoilGeometry)
  {
    List<WeightedReynoldsNumber> weightedReynoldsNumbersForChords
        = getWeightedReynoldsNumbersForChords(halfFoilGeometry);
    List<WeightedReynoldsNumberAndPolar> availableProfileReynoldsNumbers = profilePolarQuantityRelations.stream()
        .map(PhysicalQuantityValues -> new WeightedReynoldsNumberAndPolar(
            0,
            PhysicalQuantityValues.getFixedQuantities().getValue(PhysicalQuantity.REYNOLDS_NUMBER),
            PhysicalQuantityValues))
        .collect(Collectors.toList());
    for (WeightedReynoldsNumber weightedReynoldsNumberForChord : weightedReynoldsNumbersForChords)
    {
      WeightedReynoldsNumberAndPolar lowerAvailableReynoldsNumber
          = getLargestReynoldsNumberLessThanOrEqualTo(weightedReynoldsNumberForChord, availableProfileReynoldsNumbers);
      WeightedReynoldsNumberAndPolar upperAvailableReynoldsNumber = getSmallestReynoldsNumberGreaterThanOrEqualTo(
          weightedReynoldsNumberForChord, availableProfileReynoldsNumbers);
      if (lowerAvailableReynoldsNumber == null)
      {
        log.info("no Reynolds number available below " + upperAvailableReynoldsNumber.getReynoldsNumber());
        lowerAvailableReynoldsNumber = upperAvailableReynoldsNumber;
      }
      if (upperAvailableReynoldsNumber == null && lowerAvailableReynoldsNumber != null)
      {
        log.info("no Reynolds number available above " + lowerAvailableReynoldsNumber.getReynoldsNumber());
        upperAvailableReynoldsNumber = lowerAvailableReynoldsNumber;
      }
      if (lowerAvailableReynoldsNumber.getReynoldsNumber() == upperAvailableReynoldsNumber.getReynoldsNumber())
      {
        lowerAvailableReynoldsNumber.addWeight(weightedReynoldsNumberForChord.getWeight());
      }
      else
      {
        double lowerWeight
            = (upperAvailableReynoldsNumber.getReynoldsNumber() - weightedReynoldsNumberForChord.getReynoldsNumber())
            / (upperAvailableReynoldsNumber.getReynoldsNumber() - lowerAvailableReynoldsNumber.getReynoldsNumber())
            * weightedReynoldsNumberForChord.getWeight();
        double upperWeight
            = (weightedReynoldsNumberForChord.getReynoldsNumber() - lowerAvailableReynoldsNumber.getReynoldsNumber())
            / (upperAvailableReynoldsNumber.getReynoldsNumber() - lowerAvailableReynoldsNumber.getReynoldsNumber())
            * weightedReynoldsNumberForChord.getWeight();
        upperAvailableReynoldsNumber.addWeight(upperWeight);
        lowerAvailableReynoldsNumber.addWeight(lowerWeight);
      }
    }
    List<WeightedReynoldsNumberAndPolar> weightedAvailableReynoldsNumbers = availableProfileReynoldsNumbers.stream()
        .filter(weightedReynoldsNumber -> weightedReynoldsNumber.getWeight() > 0)
        .collect(Collectors.toList());
    SortedSet<Double> anglesOfAttack = getAnglesOfAttack(weightedAvailableReynoldsNumbers);

    QuantityRelation firstAvailableQuantityRelation = availableProfileReynoldsNumbers.iterator().next().getPolar();
    PhysicalQuantityValues averagedFixedQuantities
        = new PhysicalQuantityValues(firstAvailableQuantityRelation.getFixedQuantities());
    averagedFixedQuantities.remove(PhysicalQuantity.REYNOLDS_NUMBER);
    QuantityRelation averagedQuantityRelation = new QuantityRelation(
        QUANTITY_RELATON_NAME,
        averagedFixedQuantities,
        new ArrayList<>(),
        firstAvailableQuantityRelation.getKeyQuantity());

    for (Double angleOfAttack : anglesOfAttack)
    {
      PhysicalQuantityValues physicalQuantityValues = new PhysicalQuantityValues();
      physicalQuantityValues.setValue(PhysicalQuantity.ANGLE_OF_ATTACK, angleOfAttack);
      double cl = 0;
      double cd = 0;
      for (WeightedReynoldsNumberAndPolar weightedReynoldsNumberAndPolar : weightedAvailableReynoldsNumbers)
      {
        CalculatedPhysicalQuantityValues dataPoint = weightedReynoldsNumberAndPolar.getPolar().getRelatedQuantityValues(
            new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, angleOfAttack));
        cl += weightedReynoldsNumberAndPolar.getWeight() * dataPoint.getValue(PhysicalQuantity.LIFT_COEFFICIENT);
        cd += weightedReynoldsNumberAndPolar.getWeight() * dataPoint.getValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
      }
      physicalQuantityValues.setValue(PhysicalQuantity.LIFT_COEFFICIENT, cl);
      physicalQuantityValues.setValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT, cd);
      averagedQuantityRelation.addRelatedQuantityValuesEntry(physicalQuantityValues);
    }
    return averagedQuantityRelation;
  }

  private SortedSet<Double> getAnglesOfAttack(List<WeightedReynoldsNumberAndPolar> weightedAvailableReynoldsNumbers)
  {
    Double smallestAngleOfAttackContainedInAllReynoldsNumbers = null;
    Double greatestAngleOfAttackContainedInAllReynoldsNumbers = null;
    for (WeightedReynoldsNumberAndPolar availableReynoldsNumber : weightedAvailableReynoldsNumbers)
    {
      double smallestAngleOfAttackInPolar
          = availableReynoldsNumber.getPolar().getSmallestRelatedValue(PhysicalQuantity.ANGLE_OF_ATTACK);
      if (smallestAngleOfAttackContainedInAllReynoldsNumbers == null
          || smallestAngleOfAttackInPolar > smallestAngleOfAttackContainedInAllReynoldsNumbers)
      {
        smallestAngleOfAttackContainedInAllReynoldsNumbers = smallestAngleOfAttackInPolar;
      }
      double greatestAngleOfAttackInPolar
          = availableReynoldsNumber.getPolar().getGreatestRelatedValue(PhysicalQuantity.ANGLE_OF_ATTACK);
      if (greatestAngleOfAttackContainedInAllReynoldsNumbers == null
          || greatestAngleOfAttackInPolar < greatestAngleOfAttackContainedInAllReynoldsNumbers)
      {
        greatestAngleOfAttackContainedInAllReynoldsNumbers = greatestAngleOfAttackInPolar;
      }
    }

    SortedSet<Double> anglesOfAttack = new TreeSet<>();
    for (WeightedReynoldsNumberAndPolar availableReynoldsNumber : weightedAvailableReynoldsNumbers)
    {
      for (PhysicalQuantityValues dataPoint : availableReynoldsNumber.getPolar().getRelatedQuantityValues())
      {
        Double angleOfAttack = dataPoint.getValue(PhysicalQuantity.ANGLE_OF_ATTACK);
        if (angleOfAttack != null
            && angleOfAttack >= smallestAngleOfAttackContainedInAllReynoldsNumbers
            && angleOfAttack <= greatestAngleOfAttackContainedInAllReynoldsNumbers)
        {
          anglesOfAttack.add(angleOfAttack);
        }
      }
    }

    return anglesOfAttack;
  }

  private WeightedReynoldsNumberAndPolar getSmallestReynoldsNumberGreaterThanOrEqualTo(
      WeightedReynoldsNumber toCompareWith,
      List<WeightedReynoldsNumberAndPolar> availableProfileReynoldsNumbers)
  {
    WeightedReynoldsNumberAndPolar result = null;
    for (WeightedReynoldsNumberAndPolar trialValue : availableProfileReynoldsNumbers)
    {
      if (trialValue.getReynoldsNumber() < toCompareWith.getReynoldsNumber())
      {
        continue;
      }
      if (result == null
          || trialValue.getReynoldsNumber() < result.getReynoldsNumber())
      {
        result = trialValue;
      }
    }
    return result;
  }

  private WeightedReynoldsNumberAndPolar getLargestReynoldsNumberLessThanOrEqualTo(
      WeightedReynoldsNumber toCompareWith,
      List<WeightedReynoldsNumberAndPolar> availableProfileReynoldsNumbers)
  {
    WeightedReynoldsNumberAndPolar result = null;
    for (WeightedReynoldsNumberAndPolar trialValue : availableProfileReynoldsNumbers)
    {
      if (trialValue.getReynoldsNumber() > toCompareWith.getReynoldsNumber())
      {
        continue;
      }
      if (result == null
          || trialValue.getReynoldsNumber() > result.getReynoldsNumber())
      {
        result = trialValue;
      }
    }
    return result;
  }

  private List<WeightedReynoldsNumber> getWeightedReynoldsNumbersForChords(
      HalfFoilGeometry halfFoilGeometry)
  {
    PartialWingState partialWingState = new PartialWingState(halfFoilGeometry);
    ReynoldsNumberCalculator reynoldsNumberCalculator = new ReynoldsNumberCalculator();
    QuantityRelationsCalculator quantityRelationsCalculator = new QuantityRelationsCalculator();
    do
    {
      ValueSet valueSet = getValueSetWithStartValuesForPartialWing(partialWingState);
      quantityRelationsCalculator.applyQuantityRelations(valueSet);
      reynoldsNumberCalculator.apply(valueSet);
      double weight = partialWingState.getAreaForStep()/ halfFoilGeometry.getArea();
      double reynoldsNumber = valueSet.getCalculatedValue(PhysicalQuantity.REYNOLDS_NUMBER).getValue();
      partialWingState.addReynoldsNumber(
          WeightedReynoldsNumber.builder().reynoldsNumber(reynoldsNumber).weight(weight).build());
    }
    while (partialWingState.nextStep());
    return partialWingState.getReynoldsNumbers();
  }

  private ValueSet getValueSetWithStartValuesForPartialWing(
      PartialWingState partialWingState)
  {
    ValueSet valueSet = new SimpleValueSet("foil", "foil");
    valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(PhysicalQuantity.TEMPERATURE, 20));
    valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(PhysicalQuantity.VELOCITY, velocity));
    valueSet.getQuantityRelations().add(kinematicViscosityWaterRelation);
    valueSet.setStartValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.WING_CHORD, partialWingState.getChordForStep()));
    return valueSet;
  }
}
