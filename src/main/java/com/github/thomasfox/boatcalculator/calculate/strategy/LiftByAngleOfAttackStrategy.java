package com.github.thomasfox.boatcalculator.calculate.strategy;

import java.util.Arrays;
import java.util.Objects;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.progress.CalculationState;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.valueset.AllValues;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;
import com.github.thomasfox.boatcalculator.valueset.impl.Hull;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Lift of a foil is calculated by limiting it by an associated weight.
 * If the lift exceeds the weight, the angle of attack is reduced.
 * If the lift is smaller than the weight, the angle of attack is increased
 * to a limit.
 * If the limit for the angle of attack is reached, the remaining weight
 * is set for the hull.
 *
 * So this strategy sets two values, hull weight and angle of attack of the
 * main lifting foil.
 *
 */
@Getter
@ToString
@Slf4j
public class LiftByAngleOfAttackStrategy implements ComputationStrategy
{
  @NonNull
  private final PhysicalQuantityInSet[] weightSources;

  @NonNull
  private final PhysicalQuantityInSet[] liftSources;

  @NonNull
  private final PhysicalQuantityInSet[] anglesOfAttack;

  @NonNull
  private final PhysicalQuantityInSet maxAngleOfAttack;

  public LiftByAngleOfAttackStrategy(
      @NonNull PhysicalQuantityInSet[] weightSources,
      @NonNull PhysicalQuantityInSet[] liftSources,
      @NonNull PhysicalQuantityInSet[] anglesOfAttack,
      @NonNull PhysicalQuantityInSet maxAngleOfAttack)
  {
    this.weightSources = weightSources;
    this.liftSources = liftSources;
    this.anglesOfAttack = anglesOfAttack;
    this.maxAngleOfAttack = maxAngleOfAttack;
    checkUnitsOfWeightSources();
    checkUnitsOfLiftSources();
  }

  private void checkUnitsOfWeightSources()
  {
    String targetUnit = PhysicalQuantity.WEIGHT.getUnit();
    for (PhysicalQuantityInSet source : weightSources)
    {
      if (!Objects.equals(source.getPhysicalQuantity().getUnit(), targetUnit))
      {
        throw new IllegalArgumentException(
            "Source " + source.getPhysicalQuantity().getDescription()
              + " has wrong unit, must be equal to " + targetUnit);
      }
    }
  }

  private void checkUnitsOfLiftSources()
  {
    String targetUnit = PhysicalQuantity.LIFT.getUnit();
    for (PhysicalQuantityInSet source : liftSources)
    {
      if (!Objects.equals(source.getPhysicalQuantity().getUnit(), targetUnit))
      {
        throw new IllegalArgumentException(
            "Source " + source.getPhysicalQuantity().getDescription()
              + " has wrong unit, must be equal to " + targetUnit);
      }
    }
  }

  @Override
  public boolean setValue(AllValues allValues)
  {
    if (!allWeightSourceValuesAreKnown(allValues))
    {
      return false;
    }
    if (!allLiftSourceValuesAreUnknown(allValues))
    {
      return false;
    }
    if (!allAngleOfAttackSourceValuesAreUnknown(allValues))
    {
      return false;
    }

    Double maxLift = calculateTotalWingLift(allValues.getKnownValue(maxAngleOfAttack), allValues);
    if (maxLift == null)
    {
      return false;
    }
    double weight = getSumOfWeightSourcesValues(allValues);
    if (weight > maxLift)
    {
      // wing lift cannot compensate weight, use maximum angle of attack
      for (PhysicalQuantityInSet angleOfAttackValue : anglesOfAttack)
      {
        allValues.setCalculatedValueNoOverwrite(
            angleOfAttackValue,
            allValues.getKnownValue(maxAngleOfAttack),
            getCalculatedByDescription(allValues),
            getSourceValuesWithNames(allValues));
      }
      allValues.setCalculatedValueNoOverwrite(
          new PhysicalQuantityInSet(PhysicalQuantity.LIFT, Hull.ID),
          weight - maxLift,
          getCalculatedByDescription(allValues),
          getSourceValuesWithNames(allValues));
      return true;
    }
    Double wingLift = maxLift;
    double angleOfAttack = allValues.getKnownValue(maxAngleOfAttack);
    double stepUnsigned = angleOfAttack;
    double lastStepSigned = stepUnsigned;
    int tries = 20;

    while (Math.abs(weight - wingLift) > weight/1000 && tries > 0)
    {
      if (wingLift > weight)
      {
        angleOfAttack -= stepUnsigned;
        lastStepSigned = -stepUnsigned;
      }
      else
      {
        angleOfAttack += stepUnsigned;
        lastStepSigned = -stepUnsigned;
      }
      stepUnsigned = stepUnsigned/2;
      wingLift = calculateTotalWingLift(angleOfAttack, allValues);
      while (wingLift == null && tries > 0)
      {
        angleOfAttack -= lastStepSigned / 2;
        lastStepSigned = -lastStepSigned / 2;
        tries--;
        wingLift = calculateTotalWingLift(angleOfAttack, allValues);
      }
      tries--;
    }
    if (Math.abs(weight - wingLift) < weight/1000)
    {
      for (PhysicalQuantityInSet angleOfAttackValue : anglesOfAttack)
      {
        allValues.setCalculatedValueNoOverwrite(
            angleOfAttackValue,
            angleOfAttack,
            getCalculatedByDescription(allValues),
            getSourceValuesWithNames(allValues));
      }
      allValues.setCalculatedValueNoOverwrite(
          new PhysicalQuantityInSet(PhysicalQuantity.LIFT, Hull.ID),
          0,
          getCalculatedByDescription(allValues),
          getSourceValuesWithNames(allValues));
      return true;
    }
    log.info("Could not calculate lifting foil angle of attack");
    return false;
  }

  private Double calculateTotalWingLift(
      double angleOfAttack, AllValues allValues)
  {
    CalculationState.set(PhysicalQuantity.ANGLE_OF_ATTACK.toString(), angleOfAttack);
    AllValues allValuesForCalculation = new AllValues(allValues);
    allValuesForCalculation.moveCalculatedValuesToStartValues();
    for (PhysicalQuantityInSet angleOfAttackQuantity : anglesOfAttack)
    {
      allValuesForCalculation.setStartValueNoOverwrite(angleOfAttackQuantity, angleOfAttack);
    }
    double result = 0d;
    for (PhysicalQuantityInSet liftQuantity : liftSources)
    {
      allValuesForCalculation.calculate(liftQuantity);
      Double calculatedLift = allValuesForCalculation.getKnownValue(liftQuantity);
      if (calculatedLift == null)
      {
        return null;
      }
      result += calculatedLift;
    }
    return result;
  }

  public boolean allWeightSourceValuesAreKnown(AllValues allValues)
  {
    return Arrays.stream(weightSources).allMatch(allValues::isValueKnown);
  }

  public boolean allLiftSourceValuesAreUnknown(AllValues allValues)
  {
    return Arrays.stream(liftSources).allMatch(v -> !allValues.isValueKnown(v));
  }

  public boolean allAngleOfAttackSourceValuesAreUnknown(AllValues allValues)
  {
    return Arrays.stream(anglesOfAttack).allMatch(v -> !allValues.isValueKnown(v));
  }

  public double getSumOfWeightSourcesValues(AllValues allValues)
  {
    double result = 0d;
    for (PhysicalQuantityInSet source : weightSources)
    {
      result += allValues.getKnownValue(source);
    }
    return result;
  }

  public String getCalculatedByDescription(AllValues allValues)
  {
    StringBuilder result = new StringBuilder("LiftByAngleOfAttackStrategy: ");
    for (PhysicalQuantityInSet source : weightSources)
    {
      if (result.length() > 0 )
      {
        result.append(",");
      }
      result.append(allValues.getNameOfSetWithId(source.getValueSetId()))
          .append(":")
          .append(source.getPhysicalQuantity().getDisplayName());
    }
    return result.toString();
  }

  private PhysicalQuantityValueWithSetId[] getSourceValuesWithNames(AllValues allValues)
  {
    PhysicalQuantityValueWithSetId[] result = new PhysicalQuantityValueWithSetId[weightSources.length];
    int i = 0;
    for (PhysicalQuantityInSet source : weightSources)
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
