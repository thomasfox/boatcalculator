package com.github.thomasfox.boatcalculator.calculate.strategy;

import java.util.HashSet;
import java.util.Set;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;
import com.github.thomasfox.boatcalculator.valueset.ValuesAndCalculationRules;
import com.github.thomasfox.boatcalculator.valueset.impl.DaggerboardOrKeel;
import com.github.thomasfox.boatcalculator.valueset.impl.LeverSailDaggerboard;
import com.github.thomasfox.boatcalculator.valueset.impl.Rigg;

import lombok.Getter;
import lombok.ToString;

/**
 * Calculates the Lever between Rigg and Daggerboard
 * from the center of effort height of the rigg
 * and the span of the daggerboard.
 */
@Getter
@ToString
public class LeverSailDaggerboardStrategy implements StepComputationStrategy
{
  @Override
  public boolean step(ValuesAndCalculationRules allValues)
  {
    ValueSet rigg = allValues.getValueSetNonNull(Rigg.ID);
    ValueSet daggerboard = allValues.getValueSetNonNull(DaggerboardOrKeel.ID);
    ValueSet leverSailDaggerboard = allValues.getValueSetNonNull(LeverSailDaggerboard.ID);
    PhysicalQuantityValue leverSailDaggerboardQuantityValue = leverSailDaggerboard.getKnownQuantityValue(PhysicalQuantity.LEVER_BETWEEN_FORCES);
    PhysicalQuantityValue riggCenterOfEffortHeight = rigg.getKnownQuantityValue(PhysicalQuantity.RIGG_CENTER_OF_EFFORT_HEIGHT);
    PhysicalQuantityValue daggerboardSpan = daggerboard.getKnownQuantityValue(PhysicalQuantity.WING_SPAN);
    if (riggCenterOfEffortHeight != null && daggerboardSpan != null && (leverSailDaggerboardQuantityValue == null || leverSailDaggerboardQuantityValue.isTrial()))
    {
      leverSailDaggerboard.setCalculatedValueNoOverwrite(
          new SimplePhysicalQuantityValue(
              PhysicalQuantity.LEVER_BETWEEN_FORCES,
              riggCenterOfEffortHeight.getValue() + (daggerboardSpan.getValue() / 2)),
          rigg.getDisplayName() + ":" +  PhysicalQuantity.RIGG_CENTER_OF_EFFORT_HEIGHT.getDisplayName()
              + ", " + leverSailDaggerboard.getDisplayName() + ":" +  PhysicalQuantity.WING_SPAN.getDisplayName(),
          true,
          new SimplePhysicalQuantityValueWithSetId(riggCenterOfEffortHeight, rigg.getId()),
          new SimplePhysicalQuantityValueWithSetId(daggerboardSpan, daggerboard.getId()));
    }
    return false;
  }

  @Override
  public Set<PhysicalQuantityInSet> getOutputs()
  {
    Set<PhysicalQuantityInSet> result = new HashSet<>();
    result.add(new PhysicalQuantityInSet(PhysicalQuantity.LEVER_BETWEEN_FORCES, LeverSailDaggerboard.ID));
    return result;
  }

  @Override
  public Set<PhysicalQuantityInSet> getInputs()
  {
    Set<PhysicalQuantityInSet> result = new HashSet<>();
    result.add(new PhysicalQuantityInSet(PhysicalQuantity.RIGG_CENTER_OF_EFFORT_HEIGHT, Rigg.ID));
    result.add(new PhysicalQuantityInSet(PhysicalQuantity.WING_SPAN, DaggerboardOrKeel.ID));
    return result;
  }
}
