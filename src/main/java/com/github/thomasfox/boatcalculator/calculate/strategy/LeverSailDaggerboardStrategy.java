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

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * Calculates the Lever between a Sail (Rigg, Trampolien wing ...) and Daggerboard
 * from the center of effort height of the rigg
 * and the center of effort of the daggerboard.
 * If not riding, the center of effort of the daggerboard is taken to be
 * half the wing span of the daggerboard.
 * If foiling, the riding height is taken into account to calculate the
 * center of effort of the daggerboard.
 */
@Getter
@ToString
public class LeverSailDaggerboardStrategy implements StepComputationStrategy
{
  private final ValueSet sailValueSet;

  private final ValueSet targetValueSet;

  public LeverSailDaggerboardStrategy(
      @NonNull ValueSet sailValueSet,
      @NonNull ValueSet targetValueSet)
  {
    this.sailValueSet = sailValueSet;
    this.targetValueSet = targetValueSet;
  }

  @Override
  public boolean step(ValuesAndCalculationRules allValues)
  {
    ValueSet daggerboard = allValues.getValueSetNonNull(DaggerboardOrKeel.ID);
    ValueSet leverSailDaggerboard = allValues.getValueSetNonNull(targetValueSet.getId());
    ValueSet hull = allValues.getValueSetNonNull(Hull.ID);
    ValueSet boatGlobalValues = allValues.getValueSetNonNull(BoatGlobalValues.ID);
    PhysicalQuantityValue leverSailDaggerboardQuantityValue = leverSailDaggerboard.getKnownQuantityValue(PhysicalQuantity.LEVER_BETWEEN_FORCES);
    PhysicalQuantityValue riggCenterOfEffortHeight = sailValueSet.getKnownQuantityValue(PhysicalQuantity.CENTER_OF_EFFORT_HEIGHT);
    PhysicalQuantityValue daggerboardSpanInWater = daggerboard.getKnownQuantityValue(PhysicalQuantity.WING_SPAN_IN_MEDIUM);
    PhysicalQuantityValue hullDrag = hull.getKnownQuantityValue(PhysicalQuantity.TOTAL_DRAG);
    PhysicalQuantityValue velocity = boatGlobalValues.getKnownQuantityValue(PhysicalQuantity.VELOCITY);
    if (riggCenterOfEffortHeight != null
        && daggerboardSpanInWater != null
        && hullDrag != null
        && velocity != null
        && (leverSailDaggerboardQuantityValue == null || leverSailDaggerboardQuantityValue.isTrial()))
    {
      boolean foiling = hullDrag.getValue() < 0.0001 && velocity.getValue() > 0.01;
      PhysicalQuantityValue ridingHeight = boatGlobalValues.getKnownQuantityValue(PhysicalQuantity.RIDING_HEIGHT);

      if (!foiling || ridingHeight != null)
      {
        double daggerboardCenterOfEffortHeight;
        if (foiling)
        {
          daggerboardCenterOfEffortHeight = daggerboardSpanInWater.getValue()/2 + ridingHeight.getValue();
        }
        else
        {
          daggerboardCenterOfEffortHeight = daggerboardSpanInWater.getValue() / 2;
        }
        daggerboard.setCalculatedValueNoOverwrite(
            new SimplePhysicalQuantityValue(
                PhysicalQuantity.CENTER_OF_EFFORT_HEIGHT,
                daggerboardCenterOfEffortHeight),
            daggerboard.getDisplayName() + ":" +  PhysicalQuantity.WING_SPAN_IN_MEDIUM.getDisplayName()
                + ", " + boatGlobalValues.getDisplayName() + ":" +  PhysicalQuantity.RIDING_HEIGHT.getDisplayName()
                + ", " + hull.getDisplayName() + ":" +  PhysicalQuantity.TOTAL_DRAG.getDisplayName(),
            true,
            new SimplePhysicalQuantityValueWithSetId(daggerboardSpanInWater, daggerboard.getId()),
            new SimplePhysicalQuantityValueWithSetId(hullDrag, hull.getId()),
            new SimplePhysicalQuantityValueWithSetId(ridingHeight, boatGlobalValues.getId()));
        leverSailDaggerboard.setCalculatedValueNoOverwrite(
            new SimplePhysicalQuantityValue(
                PhysicalQuantity.LEVER_BETWEEN_FORCES,
                riggCenterOfEffortHeight.getValue() + daggerboardCenterOfEffortHeight),
            sailValueSet.getDisplayName() + ":" +  PhysicalQuantity.CENTER_OF_EFFORT_HEIGHT.getDisplayName()
                + ", " + daggerboard.getDisplayName() + ":" +  PhysicalQuantity.WING_SPAN_IN_MEDIUM.getDisplayName()
                + ", " + boatGlobalValues.getDisplayName() + ":" +  PhysicalQuantity.RIDING_HEIGHT.getDisplayName()
                + ", " + hull.getDisplayName() + ":" +  PhysicalQuantity.TOTAL_DRAG.getDisplayName(),
            true,
            new SimplePhysicalQuantityValueWithSetId(riggCenterOfEffortHeight, sailValueSet.getId()),
            new SimplePhysicalQuantityValueWithSetId(daggerboardSpanInWater, daggerboard.getId()),
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
    result.add(new PhysicalQuantityInSet(PhysicalQuantity.LEVER_BETWEEN_FORCES, targetValueSet.getId()));
    return result;
  }

  @Override
  public Set<PhysicalQuantityInSet> getInputs()
  {
    Set<PhysicalQuantityInSet> result = new HashSet<>();
    result.add(new PhysicalQuantityInSet(PhysicalQuantity.CENTER_OF_EFFORT_HEIGHT, sailValueSet.getId()));
    result.add(new PhysicalQuantityInSet(PhysicalQuantity.WING_SPAN, DaggerboardOrKeel.ID));
    return result;
  }
}
