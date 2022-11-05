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
import com.github.thomasfox.boatcalculator.valueset.impl.BoatGlobalValues;
import com.github.thomasfox.boatcalculator.valueset.impl.DaggerboardOrKeel;
import com.github.thomasfox.boatcalculator.valueset.impl.Hull;
import com.github.thomasfox.boatcalculator.valueset.impl.LeverSailDaggerboard;
import com.github.thomasfox.boatcalculator.valueset.impl.Rigg;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * Checks whether the boat is foiling.
 * If yes, the span in Medium is set ot the span minus the riding height.
 * If no, sthe span in medium is set to the span.
 */
@Getter
@ToString
public class ReduceSpanInMediumWhenFoilingStrategy implements StepComputationStrategy
{
  private final ValueSet targetSet;

  public ReduceSpanInMediumWhenFoilingStrategy(@NonNull ValueSet targetSet)
  {
    this.targetSet = targetSet;
  }

  @Override
  public boolean step(ValuesAndCalculationRules allValues)
  {
    ValueSet hull = allValues.getValueSetNonNull(Hull.ID);
    ValueSet boatGlobalValues = allValues.getValueSetNonNull(BoatGlobalValues.ID);
    PhysicalQuantityValue hullDrag = hull.getKnownQuantityValue(PhysicalQuantity.TOTAL_DRAG);
    PhysicalQuantityValue velocity = boatGlobalValues.getKnownQuantityValue(PhysicalQuantity.VELOCITY);
    PhysicalQuantityValue targetSetWingSpan = targetSet.getKnownQuantityValue(PhysicalQuantity.WING_SPAN);
    PhysicalQuantityValue targetSetWingSpanInMedium = targetSet.getKnownQuantityValue(PhysicalQuantity.WING_SPAN_IN_MEDIUM);
    if (hullDrag != null
        && targetSetWingSpan != null
        && velocity != null
        && (targetSetWingSpanInMedium == null || targetSetWingSpanInMedium.isTrial()))
    {
      boolean foiling = hullDrag.getValue() < 0.0001 && velocity.getValue() > 0.01;
      PhysicalQuantityValue ridingHeight = boatGlobalValues.getKnownQuantityValue(PhysicalQuantity.RIDING_HEIGHT);

      if (!foiling || ridingHeight != null)
      {
        double wingSpanInMedium;
        if (foiling)
        {
          wingSpanInMedium = targetSetWingSpan.getValue() - ridingHeight.getValue();
        }
        else
        {
          wingSpanInMedium = targetSetWingSpan.getValue();
        }
        targetSetWingSpanInMedium = new  SimplePhysicalQuantityValue(
                PhysicalQuantity.WING_SPAN_IN_MEDIUM,
                wingSpanInMedium);
        targetSet.setCalculatedValueNoOverwrite(
            targetSetWingSpanInMedium,
            targetSet.getDisplayName() + ":" +  PhysicalQuantity.WING_SPAN.getDisplayName()
                + ", " + boatGlobalValues.getDisplayName() + ":" +  PhysicalQuantity.RIDING_HEIGHT.getDisplayName()
                + ", " + hull.getDisplayName() + ":" +  PhysicalQuantity.TOTAL_DRAG.getDisplayName(),
            true,
            new SimplePhysicalQuantityValueWithSetId(targetSetWingSpan, targetSet.getId()),
            new SimplePhysicalQuantityValueWithSetId(hullDrag, hull.getId()),
            new SimplePhysicalQuantityValueWithSetId(ridingHeight, boatGlobalValues.getId()));
      }
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
    result.add(new PhysicalQuantityInSet(PhysicalQuantity.CENTER_OF_EFFORT_HEIGHT, Rigg.ID));
    result.add(new PhysicalQuantityInSet(PhysicalQuantity.WING_SPAN, DaggerboardOrKeel.ID));
    return result;
  }
}
