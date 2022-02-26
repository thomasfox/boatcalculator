package com.github.thomasfox.boatcalculator.calculate.strategy;

import static  org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.value.CalculatedPhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.valueset.ValuesAndCalculationRules;
import com.github.thomasfox.boatcalculator.valueset.impl.BoatGlobalValues;
import com.github.thomasfox.boatcalculator.valueset.impl.Rigg;

public class QuantityDifferenceTest
{

  ValuesAndCalculationRules valuesAndCalculationRules;

  QuantityDifference quantityDifference;

  @Before
  public void before()
  {
    quantityDifference = new QuantityDifference(
        new PhysicalQuantityInSet(PhysicalQuantity.FORWARD_FORCE, BoatGlobalValues.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.DRIVING_FORCE, Rigg.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, BoatGlobalValues.ID));

    valuesAndCalculationRules = new ValuesAndCalculationRules();
    valuesAndCalculationRules.add(new BoatGlobalValues());
    valuesAndCalculationRules.add(new Rigg());

    valuesAndCalculationRules.add(quantityDifference);
  }

  @Test
  public void calculate_noSourceSet()
  {
    boolean shouldContinue = quantityDifference.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isFalse();
    assertThat(valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID)
                .getCalculatedValue(PhysicalQuantity.FORWARD_FORCE))
        .isNull();
  }

  @Test
  public void calculate_toSubtractSet()
  {
    givenThatTotalDragIsKnown();

    boolean shouldContinue = quantityDifference.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isFalse();
    assertThat(
            valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID)
                .getCalculatedValue(PhysicalQuantity.FORWARD_FORCE))
        .isNull();
  }

  @Test
  public void calculate_toSubtractFromSet()
  {
    givenThatDrivingForceIsKnown();

    boolean shouldContinue = quantityDifference.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isFalse();
    assertThat(
            valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID)
                .getCalculatedValue(PhysicalQuantity.FORWARD_FORCE))
        .isNull();
  }

  @Test
  public void calculate_allSourcesSet()
  {
    givenThatTotalDragIsKnown();
    givenThatDrivingForceIsKnown();

    boolean shouldContinue = quantityDifference.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isTrue();
    CalculatedPhysicalQuantityValue result = valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID)
        .getCalculatedValue(PhysicalQuantity.FORWARD_FORCE);
    assertThat(result.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.FORWARD_FORCE);
    assertThat(result.getValue()).isEqualTo(1d);
    assertThat(result.isTrial()).isFalse();
    assertThat(result.getCalculatedBy()).isEqualTo("Rigg:Vortriebskraft - Boot:Gesamtwiderstand");
    assertThat(result.getCalculatedFrom().getAsList()).isEqualTo(List.of(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.DRIVING_FORCE, 3d), Rigg.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.TOTAL_DRAG, 2d), BoatGlobalValues.ID)));
  }

  @Test
  public void calculate_allSourcesSetAndOneOfThemIsTrialValue()
  {
    givenTotalDragIsTrialValue();
    givenThatDrivingForceIsKnown();

    boolean shouldContinue = quantityDifference.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isTrue();
    CalculatedPhysicalQuantityValue result = valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID)
        .getCalculatedValue(PhysicalQuantity.FORWARD_FORCE);
    assertThat(result.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.FORWARD_FORCE);
    assertThat(result.getValue()).isEqualTo(1d);
    assertThat(result.isTrial()).isTrue();
    assertThat(result.getCalculatedBy()).isEqualTo("Rigg:Vortriebskraft - Boot:Gesamtwiderstand");
    assertThat(result.getCalculatedFrom().getAsList()).isEqualTo(List.of(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.DRIVING_FORCE, 3d), Rigg.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.TOTAL_DRAG, 2d), BoatGlobalValues.ID)));
  }

  @Test
  public void calculate_allSourcesAreSetAndTargetIsSet()
  {
    givenThatTotalDragIsKnown();
    givenThatDrivingForceIsKnown();
    givenThatForwardForceIsFive();

    boolean shouldContinue = quantityDifference.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isFalse();
    CalculatedPhysicalQuantityValue result = valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID)
        .getCalculatedValue(PhysicalQuantity.FORWARD_FORCE);
    assertThat(result.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.FORWARD_FORCE);
    assertThat(result.getValue()).isEqualTo(5d);
    assertThat(result.isTrial()).isFalse();
    assertThat(result.getCalculatedFrom().getAsList()).isEmpty();
    assertThat(result.getCalculatedBy()).isEqualTo("me");
  }

  @Test
  public void calculate_allSourcesAreSetAndTargetIsSetToTrialValue()
  {
    givenThatTotalDragIsKnown();
    givenThatDrivingForceIsKnown();
    givenThatForwardForceIsTrialValue();

    boolean shouldContinue = quantityDifference.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isTrue();
    CalculatedPhysicalQuantityValue result = valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID)
        .getCalculatedValue(PhysicalQuantity.FORWARD_FORCE);
    assertThat(result.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.FORWARD_FORCE);
    assertThat(result.getValue()).isEqualTo(1d);
    assertThat(result.isTrial()).isFalse();
    assertThat(result.getCalculatedBy()).isEqualTo("Rigg:Vortriebskraft - Boot:Gesamtwiderstand");
    assertThat(result.getCalculatedFrom().getAsList()).isEqualTo(List.of(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.DRIVING_FORCE, 3d), Rigg.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.TOTAL_DRAG, 2d), BoatGlobalValues.ID)));
  }

  @Test
  public void calculate_allSourcesAreSetAndTargetIsSetToTrialValueCloseToCalculatedValue()
  {
    givenThatTotalDragIsKnown();
    givenThatDrivingForceIsKnown();
    givenThatForwardForceIsTrialValueCloseToCalculatedValue();

    boolean shouldContinue = quantityDifference.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isFalse();
    CalculatedPhysicalQuantityValue result = valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID)
        .getCalculatedValue(PhysicalQuantity.FORWARD_FORCE);
    assertThat(result.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.FORWARD_FORCE);
    assertThat(result.getValue()).isEqualTo(1d);
    assertThat(result.isTrial()).isFalse();
    assertThat(result.getCalculatedBy()).isEqualTo("Rigg:Vortriebskraft - Boot:Gesamtwiderstand");
    assertThat(result.getCalculatedFrom().getAsList()).isEqualTo(List.of(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.DRIVING_FORCE, 3d), Rigg.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.TOTAL_DRAG, 2d), BoatGlobalValues.ID)));
  }

  private void givenThatTotalDragIsKnown()
  {
    valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID).setStartValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.TOTAL_DRAG, 2d));
  }

  private void givenTotalDragIsTrialValue()
  {
    valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID).setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.TOTAL_DRAG, 2d), "me", true);
  }

  private void givenThatDrivingForceIsKnown()
  {
    valuesAndCalculationRules.getValueSet(Rigg.ID).setStartValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.DRIVING_FORCE, 3d));
  }

  private void givenThatForwardForceIsFive()
  {
    valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID).setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.FORWARD_FORCE, 5d), "me", false);
  }

  private void givenThatForwardForceIsTrialValue()
  {
    valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID).setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.FORWARD_FORCE, 5d), "me", true);
  }

  private void givenThatForwardForceIsTrialValueCloseToCalculatedValue()
  {
    double totalDrag = valuesAndCalculationRules
        .getValueSet(BoatGlobalValues.ID)
        .getKnownQuantityValue(PhysicalQuantity.TOTAL_DRAG)
        .getValue();
    double drivingForce = valuesAndCalculationRules
        .getValueSet(Rigg.ID)
        .getKnownQuantityValue(PhysicalQuantity.DRIVING_FORCE)
        .getValue();
    double setValue = (drivingForce - totalDrag) * 1.000001d;
    valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID).setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.FORWARD_FORCE, setValue), "me", true);
  }
}
