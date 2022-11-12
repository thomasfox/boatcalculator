package com.github.thomasfox.boatcalculator.calculate.strategy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.github.thomasfox.boatcalculator.calculate.CalculationResult;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.valueset.ValuesAndCalculationRules;
import com.github.thomasfox.boatcalculator.valueset.impl.BoatGlobalValues;
import com.github.thomasfox.boatcalculator.valueset.impl.DaggerboardOrKeel;
import com.github.thomasfox.boatcalculator.valueset.impl.Rigg;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
public class DaggerboardLiftStrategy implements StepComputationStrategy
{
  private static PhysicalQuantityInSet targetQuantityInSet
      = new PhysicalQuantityInSet(PhysicalQuantity.LIFT, DaggerboardOrKeel.ID);

  private static PhysicalQuantityInSet riggLateralForceQuantityInSet
      = new PhysicalQuantityInSet(PhysicalQuantity.LATERAL_FORCE, Rigg.ID);
  private static PhysicalQuantityInSet heelAngleAngleQuantityInSet
      = new PhysicalQuantityInSet(PhysicalQuantity.WINDWARD_HEEL_ANGLE, BoatGlobalValues.ID);

  @NonNull
  private final PhysicalQuantityInSet[] weightSources;


  public DaggerboardLiftStrategy(PhysicalQuantityInSet[] weightSources)
  {
    this.weightSources = weightSources;
    checkUnitsOfWeightSources();
  }

  private String getCalculatedByDescription(ValuesAndCalculationRules allValues)
  {
    StringBuilder result = new StringBuilder();
    for (PhysicalQuantityInSet source : getInputs())
    {
      if (result.length() > 0 )
      {
        result.append(", ");
      }
      result.append(allValues.getNameOfSetWithId(source.getSetId()))
          .append(":")
          .append(source.getPhysicalQuantity().getDisplayName());
    }
    return result.toString();
  }

  @Override
  public boolean step(ValuesAndCalculationRules allValues)
  {
    PhysicalQuantityValue targetValue = allValues.getKnownPhysicalQuantityValue(targetQuantityInSet);
    PhysicalQuantityValue riggLateralForceValue = allValues.getKnownPhysicalQuantityValue(riggLateralForceQuantityInSet);
    PhysicalQuantityValue heelAngleValue = allValues.getKnownPhysicalQuantityValue(heelAngleAngleQuantityInSet);

    if (riggLateralForceValue != null
        && heelAngleValue != null
        && (targetValue == null || targetValue.isTrial()))
    {
      double daggerboardLift;
      if (heelAngleValue.getValue() == 0d)
      {
        daggerboardLift = riggLateralForceValue.getValue();
      }
      else
      {
        if (!allWeightSourceValuesAreKnown(allValues))
        {
          return false;
        }
        double weight = getSumOfWeightSourcesValues(allValues);
        daggerboardLift = riggLateralForceValue.getValue()
            - Math.sin(heelAngleValue.getValue()*Math.PI/180) * weight;
      }
      CalculationResult result = new CalculationResult(
          daggerboardLift,
          targetValue == null ? null : targetValue.getValue(),
          riggLateralForceValue.isTrial() || heelAngleValue.isTrial());
      allValues.setCalculatedValueNoOverwrite(
          targetQuantityInSet,
          daggerboardLift,
          getCalculatedByDescription(allValues),
          result.isTrial(),
          getSourceValuesWithSetId(allValues, riggLateralForceValue, heelAngleValue));
      return !result.relativeDifferenceIsBelowThreshold();
    }
    return false;
  }

  @Override
  public Set<PhysicalQuantityInSet> getOutputs()
  {
    Set<PhysicalQuantityInSet> result = new HashSet<>();
    result.add(targetQuantityInSet);
    return result;
  }

  @Override
  public Set<PhysicalQuantityInSet> getInputs()
  {
    Set<PhysicalQuantityInSet> result = new HashSet<>();
    result.add(riggLateralForceQuantityInSet);
    result.add(heelAngleAngleQuantityInSet);
    return result;
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

  private boolean allWeightSourceValuesAreKnown(ValuesAndCalculationRules allValues)
  {
    return Arrays.stream(weightSources).allMatch(allValues::isValueKnown);
  }

  private double getSumOfWeightSourcesValues(ValuesAndCalculationRules allValues)
  {
    double result = 0d;
    for (PhysicalQuantityInSet source : weightSources)
    {
      result += allValues.getKnownValue(source);
    }
    return result;
  }

  private PhysicalQuantityValueWithSetId[] getSourceValuesWithSetId(
      ValuesAndCalculationRules allValues,
      PhysicalQuantityValue riggLateralForceValue,
      PhysicalQuantityValue heelAngleValue)
  {
    if (heelAngleValue.getValue() == 0)
    {
      return new PhysicalQuantityValueWithSetId[] {new SimplePhysicalQuantityValueWithSetId(
          riggLateralForceValue,
          Rigg.ID)};
    }
    PhysicalQuantityValueWithSetId[] result = new PhysicalQuantityValueWithSetId[weightSources.length + 2];

    result[0] = new SimplePhysicalQuantityValueWithSetId(
        riggLateralForceValue,
        Rigg.ID);
    result[1] = new SimplePhysicalQuantityValueWithSetId(
        heelAngleValue,
        BoatGlobalValues.ID);
    int i = 2;
    for (PhysicalQuantityInSet source : weightSources)
    {
      result[i] = new SimplePhysicalQuantityValueWithSetId(
          allValues.getKnownPhysicalQuantityValue(source),
          source.getSetId());
      ++i;
    }
    return result;
  }


}
