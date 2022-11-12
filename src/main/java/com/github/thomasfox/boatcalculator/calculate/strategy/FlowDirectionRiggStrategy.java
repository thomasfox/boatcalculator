package com.github.thomasfox.boatcalculator.calculate.strategy;

import java.util.HashSet;
import java.util.Set;

import com.github.thomasfox.boatcalculator.calculate.CalculationResult;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.valueset.ValuesAndCalculationRules;
import com.github.thomasfox.boatcalculator.valueset.impl.BoatGlobalValues;
import com.github.thomasfox.boatcalculator.valueset.impl.Rigg;

import lombok.Getter;
import lombok.ToString;

/**
 * A sum of values. One quantity is known to be the sum of other quantities.
 * Optionally, factors can be applied to the parts of the sum.
 */
@Getter
@ToString
public class FlowDirectionRiggStrategy implements StepComputationStrategy
{
  private static PhysicalQuantityInSet targetQuantityInSet
      = new PhysicalQuantityInSet(PhysicalQuantity.FLOW_DIRECTION, Rigg.ID);

  private static PhysicalQuantityInSet apparentWindAngleQuantityInSet
      = new PhysicalQuantityInSet(PhysicalQuantity.APPARENT_WIND_ANGLE, BoatGlobalValues.ID);
  private static PhysicalQuantityInSet heelAngleAngleQuantityInSet
      = new PhysicalQuantityInSet(PhysicalQuantity.WINDWARD_HEEL_ANGLE, BoatGlobalValues.ID);

  public FlowDirectionRiggStrategy()
  {
  }

  private String getCalculatedByDescription(ValuesAndCalculationRules allValues)
  {
    StringBuilder result = new StringBuilder();
    for (PhysicalQuantityInSet source : getInputs())
    {
      if (result.length() > 0 )
      {
        result.append(" + ");
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
    PhysicalQuantityValue apparentWindAngleValue = allValues.getKnownPhysicalQuantityValue(apparentWindAngleQuantityInSet);
    PhysicalQuantityValue heelAngleValue = allValues.getKnownPhysicalQuantityValue(heelAngleAngleQuantityInSet);

    if (apparentWindAngleValue != null
        && heelAngleValue != null
        && (targetValue == null || targetValue.isTrial()))
    {
      double riggFlowDirection;
      if (heelAngleValue.getValue() == 0d)
      {
        riggFlowDirection = apparentWindAngleValue.getValue();
      }
      else
      {
        riggFlowDirection = Math.asin(Math.sin(apparentWindAngleValue.getValue() * Math.PI/180)
                * Math.cos(heelAngleValue.getValue() * Math.PI/180))
                * 180 / Math.PI;
      }
      CalculationResult result = new CalculationResult(
          riggFlowDirection,
          targetValue == null ? null : targetValue.getValue(),
          apparentWindAngleValue.isTrial() || heelAngleValue.isTrial());
      allValues.setCalculatedValueNoOverwrite(
          targetQuantityInSet,
          riggFlowDirection,
          getCalculatedByDescription(allValues),
          result.isTrial(),
          new SimplePhysicalQuantityValueWithSetId(
              apparentWindAngleValue,
              BoatGlobalValues.ID),
          new SimplePhysicalQuantityValueWithSetId(
              heelAngleValue,
              BoatGlobalValues.ID));
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
    result.add(apparentWindAngleQuantityInSet);
    result.add(heelAngleAngleQuantityInSet);
    return result;
  }
}
