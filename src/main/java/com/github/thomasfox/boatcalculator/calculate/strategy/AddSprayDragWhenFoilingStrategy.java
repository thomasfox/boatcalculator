package com.github.thomasfox.boatcalculator.calculate.strategy;

import java.util.HashSet;
import java.util.Set;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.calculate.impl.SurfacePiercingDragCoefficientCalculator;
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
 * If yes, the spray drag coefficent is calculated.
 * If no, the spray drag coefficient is set to zero.
 */
@Getter
@ToString
public class AddSprayDragWhenFoilingStrategy implements StepComputationStrategy
{
  private final ValueSet targetSet;

  public AddSprayDragWhenFoilingStrategy(@NonNull ValueSet targetSet)
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
    PhysicalQuantityValue targetSetSurfacePiercingDragCoefficient
        = targetSet.getKnownQuantityValue(PhysicalQuantity.SURFACE_PIERCING_DRAG_COEFFICIENT);
    if (hullDrag != null
        && velocity != null
        && (targetSetSurfacePiercingDragCoefficient == null || targetSetSurfacePiercingDragCoefficient.isTrial()))
    {
      boolean foiling = hullDrag.getValue() < 0.0001 && velocity.getValue() > 0.01;
      PhysicalQuantityValue targetSetAreaInMedium
          = targetSet.getKnownQuantityValue(PhysicalQuantity.AREA_IN_MEDIUM);
      PhysicalQuantityValue targetSetThickness
          = targetSet.getKnownQuantityValue(PhysicalQuantity.WING_THICKNESS);

      if (!foiling || (targetSetAreaInMedium != null && targetSetThickness != null))
      {
        double surfacePiercingDragCoefficient;
        if (foiling)
        {
          surfacePiercingDragCoefficient = new SurfacePiercingDragCoefficientCalculator().calculate(targetSet).getValue();
        }
        else
        {
          surfacePiercingDragCoefficient = 0;
        }
        targetSet.setCalculatedValueNoOverwrite(
            new  SimplePhysicalQuantityValue(
                PhysicalQuantity.WING_SPAN_IN_MEDIUM,
                surfacePiercingDragCoefficient),
            targetSet.getDisplayName() + ":" +  PhysicalQuantity.AREA_IN_MEDIUM.getDisplayName()
                + ", " + targetSet.getDisplayName() + ":" +  PhysicalQuantity.WING_THICKNESS.getDisplayName()
                + ", " + boatGlobalValues.getDisplayName() + ":" +  PhysicalQuantity.RIDING_HEIGHT.getDisplayName()
                + ", " + hull.getDisplayName() + ":" +  PhysicalQuantity.TOTAL_DRAG.getDisplayName(),
            true,
            new SimplePhysicalQuantityValueWithSetId(targetSetAreaInMedium, targetSet.getId()),
            new SimplePhysicalQuantityValueWithSetId(targetSetThickness, targetSet.getId()),
            new SimplePhysicalQuantityValueWithSetId(hullDrag, hull.getId()),
            new SimplePhysicalQuantityValueWithSetId(velocity, boatGlobalValues.getId()));
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
