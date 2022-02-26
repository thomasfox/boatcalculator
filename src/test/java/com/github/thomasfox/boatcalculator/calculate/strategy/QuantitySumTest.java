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
import com.github.thomasfox.boatcalculator.valueset.impl.DaggerboardOrKeel;
import com.github.thomasfox.boatcalculator.valueset.impl.Rudder;

public class QuantitySumTest
{

  ValuesAndCalculationRules valuesAndCalculationRules;

  QuantitySum quantitySum;

  @Before
  public void before()
  {
    quantitySum = new QuantitySum(
        new PhysicalQuantityInSet(PhysicalQuantity.LIFT, BoatGlobalValues.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.LIFT, DaggerboardOrKeel.ID),
        new PhysicalQuantityInSet(PhysicalQuantity.LIFT, Rudder.ID));

    valuesAndCalculationRules = new ValuesAndCalculationRules();
    valuesAndCalculationRules.add(new BoatGlobalValues());
    valuesAndCalculationRules.add(new DaggerboardOrKeel());
    valuesAndCalculationRules.add(new Rudder());

    valuesAndCalculationRules.add(quantitySum);
  }

  @Test
  public void calculate_noSourceSet()
  {
    boolean shouldContinue = quantitySum.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isFalse();
    assertThat(
            valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID)
                .getCalculatedValue(PhysicalQuantity.LIFT))
        .isNull();
  }

  @Test
  public void calculate_sourceSet()
  {
    givenThatDaggerboardLiftIsKnown();

    boolean shouldContinue = quantitySum.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isFalse();
    assertThat(
            valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID)
                .getCalculatedValue(PhysicalQuantity.LIFT))
        .isNull();
  }

  @Test
  public void calculate_allSourcesSet()
  {
    givenThatDaggerboardLiftIsKnown();
    givenThatRudderLiftIsKnown();

    boolean shouldContinue = quantitySum.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isTrue();
    CalculatedPhysicalQuantityValue result = valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID)
        .getCalculatedValue(PhysicalQuantity.LIFT);
    assertThat(result.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.LIFT);
    assertThat(result.getValue()).isEqualTo(6d);
    assertThat(result.isTrial()).isFalse();
    assertThat(result.getCalculatedBy()).isEqualTo("Schwert/Kiel:Auftrieb + Ruder:Auftrieb");
    assertThat(result.getCalculatedFrom().getAsList()).isEqualTo(List.of(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.LIFT, 2d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.LIFT, 4d), Rudder.ID)));
  }

  @Test
  public void calculate_allSourcesSetAndOneOfThemIsTrialValue()
  {
    givenThatDaggerboardLiftIsTrialValue();
    givenThatRudderLiftIsKnown();

    boolean shouldContinue = quantitySum.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isTrue();
    CalculatedPhysicalQuantityValue result = valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID)
        .getCalculatedValue(PhysicalQuantity.LIFT);
    assertThat(result.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.LIFT);
    assertThat(result.getValue()).isEqualTo(6d);
    assertThat(result.isTrial()).isTrue();
    assertThat(result.getCalculatedBy()).isEqualTo("Schwert/Kiel:Auftrieb + Ruder:Auftrieb");
    assertThat(result.getCalculatedFrom().getAsList()).isEqualTo(List.of(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.LIFT, 2d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.LIFT, 4d), Rudder.ID)));
  }

  @Test
  public void calculate_allSourcesAreSetAndTargetIsSet()
  {
    givenThatDaggerboardLiftIsKnown();
    givenThatRudderLiftIsKnown();
    givenThatBoatLiftIsThree();

    boolean shouldContinue = quantitySum.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isFalse();
    CalculatedPhysicalQuantityValue result = valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID)
        .getCalculatedValue(PhysicalQuantity.LIFT);
    assertThat(result.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.LIFT);
    assertThat(result.getValue()).isEqualTo(3d);
    assertThat(result.isTrial()).isFalse();
    assertThat(result.getCalculatedFrom().getAsList()).isEmpty();
    assertThat(result.getCalculatedBy()).isEqualTo("me");
  }

  @Test
  public void calculate_allSourcesAreSetAndTargetIsSetToTrialValue()
  {
    givenThatDaggerboardLiftIsKnown();
    givenThatRudderLiftIsKnown();
    givenThatBoatLiftIsTrialValue();

    boolean shouldContinue = quantitySum.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isTrue();
    CalculatedPhysicalQuantityValue result = valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID)
        .getCalculatedValue(PhysicalQuantity.LIFT);
    assertThat(result.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.LIFT);
    assertThat(result.getValue()).isEqualTo(6d);
    assertThat(result.isTrial()).isFalse();
    assertThat(result.getCalculatedBy()).isEqualTo("Schwert/Kiel:Auftrieb + Ruder:Auftrieb");
    assertThat(result.getCalculatedFrom().getAsList()).isEqualTo(List.of(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.LIFT, 2d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.LIFT, 4d), Rudder.ID)));
  }

  @Test
  public void calculate_allSourcesAreSetAndTargetIsSetToTrialValueCloseToCalculatedValue()
  {
    givenThatDaggerboardLiftIsKnown();
    givenThatRudderLiftIsKnown();
    givenThatBoatLiftIsTrialValueCloseToCalculatedValue();

    boolean shouldContinue = quantitySum.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isFalse();
    CalculatedPhysicalQuantityValue result = valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID)
        .getCalculatedValue(PhysicalQuantity.LIFT);
    assertThat(result.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.LIFT);
    assertThat(result.getValue()).isEqualTo(6d);
    assertThat(result.isTrial()).isFalse();
    assertThat(result.getCalculatedBy()).isEqualTo("Schwert/Kiel:Auftrieb + Ruder:Auftrieb");
    assertThat(result.getCalculatedFrom().getAsList()).isEqualTo(List.of(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.LIFT, 2d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.LIFT, 4d), Rudder.ID)));
  }

  private void givenThatDaggerboardLiftIsKnown()
  {
    valuesAndCalculationRules.getValueSet(DaggerboardOrKeel.ID).setStartValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.LIFT, 2d));
  }

  private void givenThatDaggerboardLiftIsTrialValue()
  {
    valuesAndCalculationRules.getValueSet(DaggerboardOrKeel.ID).setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.LIFT, 2d), "me", true);
  }


  private void givenThatRudderLiftIsKnown()
  {
    valuesAndCalculationRules.getValueSet(Rudder.ID).setStartValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.LIFT, 4d));
  }

  private void givenThatBoatLiftIsThree()
  {
    valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID).setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.LIFT, 3d), "me", false);
  }

  private void givenThatBoatLiftIsTrialValue()
  {
    valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID).setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.LIFT, 3d), "me", true);
  }

  private void givenThatBoatLiftIsTrialValueCloseToCalculatedValue()
  {
    double rudderLift = valuesAndCalculationRules
        .getValueSet(Rudder.ID)
        .getKnownQuantityValue(PhysicalQuantity.LIFT)
        .getValue();
    double daggerboardLift = valuesAndCalculationRules
        .getValueSet(DaggerboardOrKeel.ID)
        .getKnownQuantityValue(PhysicalQuantity.LIFT)
        .getValue();
    double setValue = (rudderLift + daggerboardLift) * 1.000001d;
    valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID).setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.LIFT, setValue), "me", true);
  }
}
