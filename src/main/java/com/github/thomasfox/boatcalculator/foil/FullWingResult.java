package com.github.thomasfox.boatcalculator.foil;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;
import lombok.Getter;

@Getter
class FullWingResult
{
  private final ValueWithWingSpanAndAreaGradients angleOfAttack = new ValueWithWingSpanAndAreaGradients();

  private final ValueWithWingSpanAndAreaGradients inducedDrag = new ValueWithWingSpanAndAreaGradients();

  public FullWingResult(ValueSet valueSet, ValueSet valueSetWithOtherWingSpan, ValueSet valueSetWithOtherArea)
  {
    if (valueSet.getCalculatedValue(PhysicalQuantity.ANGLE_OF_ATTACK) == null
        || valueSet.getCalculatedValue(PhysicalQuantity.INDUCED_DRAG) == null) {
      throw new IllegalStateException("ANGLE_OF_ATTACK oder INDUCED_DRAG sind unbekannt");
    }
    if (valueSetWithOtherWingSpan.getCalculatedValue(PhysicalQuantity.ANGLE_OF_ATTACK) == null
        || valueSetWithOtherWingSpan.getCalculatedValue(PhysicalQuantity.INDUCED_DRAG) == null) {
      throw new IllegalStateException("ANGLE_OF_ATTACK oder INDUCED_DRAG sind unbekannt in valueSetWithOtherWingSpan");
    }

    if (valueSetWithOtherArea.getCalculatedValue(PhysicalQuantity.ANGLE_OF_ATTACK) == null
        || valueSetWithOtherArea.getCalculatedValue(PhysicalQuantity.INDUCED_DRAG) == null) {
      throw new IllegalStateException("ANGLE_OF_ATTACK oder INDUCED_DRAG sind unbekannt in valueSetWithIncreasedArea");
    }

    angleOfAttack.setValue(valueSet.getCalculatedValue(PhysicalQuantity.ANGLE_OF_ATTACK).getValue());
    inducedDrag.setValue(valueSet.getCalculatedValue(PhysicalQuantity.INDUCED_DRAG).getValue());

    {
      double wingSpan = valueSet.getKnownQuantityValue(PhysicalQuantity.WING_SPAN_IN_MEDIUM).getValue();
      double otherWingSpan = valueSetWithOtherWingSpan.getKnownQuantityValue(PhysicalQuantity.WING_SPAN_IN_MEDIUM).getValue();
      double changeInWingSpan = (otherWingSpan - wingSpan);

      double angleOfAttackWithOtherWingSpan = valueSetWithOtherWingSpan.getCalculatedValue(PhysicalQuantity.ANGLE_OF_ATTACK).getValue();
      double inducedDragWithOtherWingSpan = valueSetWithOtherWingSpan.getCalculatedValue(PhysicalQuantity.INDUCED_DRAG).getValue();

      angleOfAttack.setGradientWithRespectToWingSpan(
          (angleOfAttackWithOtherWingSpan - angleOfAttack.getValue()) / changeInWingSpan);
      inducedDrag.setGradientWithRespectToWingSpan(
          (inducedDragWithOtherWingSpan - inducedDrag.getValue()) / changeInWingSpan);
    }

    {
      double area = valueSet.getKnownQuantityValue(PhysicalQuantity.AREA_IN_MEDIUM).getValue();
      double otherArea = valueSetWithOtherArea.getKnownQuantityValue(PhysicalQuantity.AREA_IN_MEDIUM).getValue();
      double changeInArea = (otherArea - area);

      double angleOfAttackWithOtherArea = valueSetWithOtherArea.getCalculatedValue(PhysicalQuantity.ANGLE_OF_ATTACK).getValue();
      double inducedDragWithOtherArea = valueSetWithOtherArea.getCalculatedValue(PhysicalQuantity.INDUCED_DRAG).getValue();

      angleOfAttack.setGradientWithRespectToArea(
          (angleOfAttackWithOtherArea - angleOfAttack.getValue()) / changeInArea);
      inducedDrag.setGradientWithRespectToArea(
          (inducedDragWithOtherArea - inducedDrag.getValue()) / changeInArea);
    }
  }
}
