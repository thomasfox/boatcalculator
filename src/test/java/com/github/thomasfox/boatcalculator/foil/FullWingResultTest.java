package com.github.thomasfox.boatcalculator.foil;

import static  org.assertj.core.api.Assertions.assertThat;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.valueset.SimpleValueSet;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;
import junit.framework.TestCase;
import org.assertj.core.data.Offset;
import org.junit.Test;

public class FullWingResultTest extends TestCase
{
  @Test
  public void testConstructor()
  {
    ValueSet valueSet = new SimpleValueSet("test", "FullWingResultTest");
    valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(PhysicalQuantity.WING_SPAN_IN_MEDIUM, 0.1d));
    valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(PhysicalQuantity.AREA_IN_MEDIUM, 0.5d));
    valueSet.setCalculatedValue(
        new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, 10d),
        "test",
        false);
    valueSet.setCalculatedValue(
        new SimplePhysicalQuantityValue(PhysicalQuantity.INDUCED_DRAG, 20d),
        "test",
        false);

    ValueSet valueSetWithOtherSpan = new SimpleValueSet("test", "FullWingResultTest");
    valueSetWithOtherSpan.setStartValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.WING_SPAN_IN_MEDIUM, 0.101d));
    valueSetWithOtherSpan.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(PhysicalQuantity.AREA_IN_MEDIUM, 0.5d));
    valueSetWithOtherSpan.setCalculatedValue(
        new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, 10.1d),
        "test",
        false);
    valueSetWithOtherSpan.setCalculatedValue(
        new SimplePhysicalQuantityValue(PhysicalQuantity.INDUCED_DRAG, 19.9999999d),
        "test",
        false);

    ValueSet valueSetWithOtherArea = new SimpleValueSet("test", "FullWingResultTest");
    valueSetWithOtherArea.setStartValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.WING_SPAN_IN_MEDIUM, 0.1d));
    valueSetWithOtherArea.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(PhysicalQuantity.AREA_IN_MEDIUM, 0.505d));
    valueSetWithOtherArea.setCalculatedValue(
        new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, 9.9d),
        "test",
        false);
    valueSetWithOtherArea.setCalculatedValue(
        new SimplePhysicalQuantityValue(PhysicalQuantity.INDUCED_DRAG, 20.1d),
        "test",
        false);

    FullWingResult result = new FullWingResult(valueSet, valueSetWithOtherSpan, valueSetWithOtherArea);

    assertThat(result.getAngleOfAttack().getValue()).isEqualTo(10d);
    assertThat(result.getInducedDrag().getValue()).isEqualTo(20d);
    assertThat(result.getAngleOfAttack().getGradientWithRespectToWingSpan())
        .isCloseTo(100d, Offset.offset(0.00001));
    assertThat(result.getInducedDrag().getGradientWithRespectToWingSpan())
        .isCloseTo(-0.0001d, Offset.offset(0.0000001));
    assertThat(result.getAngleOfAttack().getGradientWithRespectToArea())
        .isCloseTo(-20d, Offset.offset(0.00001));
    assertThat(result.getInducedDrag().getGradientWithRespectToArea())
        .isCloseTo(20d, Offset.offset(0.0000001));
  }
}