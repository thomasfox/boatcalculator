package com.github.thomasfox.boatcalculator.calculate.strategy;

import java.util.Arrays;
import java.util.Objects;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.progress.CalculationState;
import com.github.thomasfox.boatcalculator.value.CalculatedPhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.valueset.AllValues;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;
import com.github.thomasfox.boatcalculator.valueset.impl.Hull;
import com.github.thomasfox.boatcalculator.valueset.impl.MainLiftingFoil;

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
  static final PhysicalQuantityInSet MAIN_FOIL_LIFT
      = new PhysicalQuantityInSet(PhysicalQuantity.LIFT, MainLiftingFoil.ID);

  static final PhysicalQuantityInSet MAIN_FOIL_MAX_ANGLE_OF_ATTACK
      = new PhysicalQuantityInSet(PhysicalQuantity.MAX_ANGLE_OF_ATTACK, MainLiftingFoil.ID);

  @NonNull
  private final PhysicalQuantityInSet[] weightSources;

  public LiftByAngleOfAttackStrategy(@NonNull PhysicalQuantityInSet... weightSources)
  {
    this.weightSources = weightSources;
    checkUnitsOfWeightSources();
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

  @Override
  public boolean setValue(AllValues allValues)
  {
    if (allValues.isValueKnown(new PhysicalQuantityInSet(PhysicalQuantity.ANGLE_OF_ATTACK, MainLiftingFoil.ID)))
    {
      return false;
    }
    if (allValues.isValueKnown(new PhysicalQuantityInSet(PhysicalQuantity.LIFT, MainLiftingFoil.ID)))
    {
      return false;
    }
    if (!allValues.isValueKnown(new PhysicalQuantityInSet(PhysicalQuantity.MAX_ANGLE_OF_ATTACK, MainLiftingFoil.ID)))
    {
      return false;
    }
    if (allValues.isValueKnown(new PhysicalQuantityInSet(PhysicalQuantity.LIFT, Hull.ID)))
    {
      return false;
    }
    if (!allWeightSourceValuesAreKnown(allValues))
    {
      return false;
    }

    double maxAngleOfAttack = allValues.getKnownValue(
        new PhysicalQuantityInSet(PhysicalQuantity.MAX_ANGLE_OF_ATTACK, MainLiftingFoil.ID));
    CalculatedPhysicalQuantityValue mainWingMaxLift = calculateLiftOfMainWing(maxAngleOfAttack, allValues);
    if (mainWingMaxLift == null)
    {
      return false;
    }
    double weight = getSumOfWeightSourcesValues(allValues);
    if (weight > mainWingMaxLift.getValue())
    {
      // main wing lift cannot compensate weight
      allValues.setCalculatedValueNoOverwrite(
          new PhysicalQuantityInSet(PhysicalQuantity.ANGLE_OF_ATTACK, MainLiftingFoil.ID),
          maxAngleOfAttack,
          getCalculatedByDescription(allValues),
          getSourceValuesWithNames(allValues));
      allValues.setCalculatedValueNoOverwrite(
          new PhysicalQuantityInSet(PhysicalQuantity.LIFT, Hull.ID),
          weight - mainWingMaxLift.getValue(),
          getCalculatedByDescription(allValues),
          getSourceValuesWithNames(allValues));
      return true;
    }
    CalculatedPhysicalQuantityValue mainWingLift = mainWingMaxLift;
    double angleOfAttack = maxAngleOfAttack;
    double step = angleOfAttack;
    int tries = 20;
    while (Math.abs(weight - mainWingLift.getValue()) > weight/1000 && tries > 0)
    {
      if (mainWingLift.getValue() > weight)
      {
        angleOfAttack -= step;
      }
      else
      {
        angleOfAttack += step;
      }
      step = step/2;
      mainWingLift = calculateLiftOfMainWing(angleOfAttack, allValues);
      tries--;
    }
    if (Math.abs(weight - mainWingLift.getValue()) < weight/1000)
    {
      allValues.setCalculatedValueNoOverwrite(
          new PhysicalQuantityInSet(PhysicalQuantity.ANGLE_OF_ATTACK, MainLiftingFoil.ID),
          angleOfAttack,
          getCalculatedByDescription(allValues),
          getSourceValuesWithNames(allValues));
      allValues.setCalculatedValueNoOverwrite(
          new PhysicalQuantityInSet(PhysicalQuantity.LIFT, Hull.ID),
          0,
          getCalculatedByDescription(allValues),
          getSourceValuesWithNames(allValues));
      return true;
    }
    log.info("Could not calculate main lifting foil angle of attack");
    return false;
  }

  private CalculatedPhysicalQuantityValue calculateLiftOfMainWing(
      double angleOfAttack, AllValues allValues)
  {
    CalculationState.set(MainLiftingFoil.ID + ":" + PhysicalQuantity.ANGLE_OF_ATTACK, angleOfAttack);
    AllValues allValuesForCalculation = new AllValues(allValues);
    allValuesForCalculation.moveCalculatedValuesToStartValues();
    allValuesForCalculation.getValueSet(MainLiftingFoil.ID).setStartValueNoOverwrite(
        new PhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, angleOfAttack));
    allValuesForCalculation.calculate(MAIN_FOIL_LIFT);

    CalculatedPhysicalQuantityValue maxLift
        = allValuesForCalculation
        .getValueSet(MainLiftingFoil.ID)
        .getCalculatedValues()
        .getPhysicalQuantityValue(PhysicalQuantity.LIFT);
    return maxLift;
  }

  public boolean allWeightSourceValuesAreKnown(AllValues allValues)
  {
    return Arrays.stream(weightSources).allMatch(allValues::isValueKnown);
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
