package com.github.thomasfox.boatcalculator.calculate.strategy;

import static  org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.value.CalculatedPhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.valueset.ValuesAndCalculationRules;
import com.github.thomasfox.boatcalculator.valueset.impl.BoatGlobalValues;
import com.github.thomasfox.boatcalculator.valueset.impl.DaggerboardOrKeel;

public class QuantityEqualityTest
{

  ValuesAndCalculationRules valuesAndCalculationRules;

  QuantityEquality quantityEquality;

  @Before
  public void before()
  {
    quantityEquality = new QuantityEquality(
        PhysicalQuantity.VELOCITY, BoatGlobalValues.ID,
        PhysicalQuantity.VELOCITY, DaggerboardOrKeel.ID);

    valuesAndCalculationRules = new ValuesAndCalculationRules();
    valuesAndCalculationRules.add(new BoatGlobalValues());
    valuesAndCalculationRules.add(new DaggerboardOrKeel());

    valuesAndCalculationRules.add(quantityEquality);
  }

  @Test
  public void calculate_sourceIsNotSet()
  {
    boolean shouldContinue = quantityEquality.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isFalse();
    assertThat(
            valuesAndCalculationRules.getValueSet(DaggerboardOrKeel.ID)
                .getCalculatedValue(PhysicalQuantity.VELOCITY))
        .isNull();
  }

  @Test
  public void calculate_sourceIsSet()
  {
    givenThatBoatVelocityIsKnown();

    boolean shouldContinue = quantityEquality.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isTrue();
    CalculatedPhysicalQuantityValue result = valuesAndCalculationRules.getValueSet(DaggerboardOrKeel.ID)
        .getCalculatedValue(PhysicalQuantity.VELOCITY);
    assertThat(result.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.VELOCITY);
    assertThat(result.getValue()).isEqualTo(2.3d);
    assertThat(result.isTrial()).isFalse();
    assertThat(result.getCalculatedFrom().getAsList()).isEqualTo(
        List.of(new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.VELOCITY, 2.3d),
            BoatGlobalValues.ID)));
    assertThat(result.getCalculatedBy()).isEqualTo("Boot:Geschwindigkeit");
  }

  @Test
  public void calculate_sourceAndTargetAreSet()
  {
    givenThatBoatVelocityIsKnown();
    givenThatDaggerboardVelocityIsThree();

    boolean shouldContinue = quantityEquality.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isFalse();
    CalculatedPhysicalQuantityValue result = valuesAndCalculationRules.getValueSet(DaggerboardOrKeel.ID)
        .getCalculatedValue(PhysicalQuantity.VELOCITY);
    assertThat(result.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.VELOCITY);
    assertThat(result.getValue()).isEqualTo(3d);
    assertThat(result.isTrial()).isFalse();
    assertThat(result.getCalculatedFrom().getAsList()).isEmpty();
    assertThat(result.getCalculatedBy()).isEqualTo("me");
  }

  @Test
  public void calculate_sourceIsSetAndTargetIsSetToTrialValue()
  {
    givenThatBoatVelocityIsKnown();
    givenThatDaggerboardVelocityIsTrialValue();

    boolean shouldContinue = quantityEquality.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isTrue();
    CalculatedPhysicalQuantityValue result = valuesAndCalculationRules.getValueSet(DaggerboardOrKeel.ID)
        .getCalculatedValue(PhysicalQuantity.VELOCITY);
    assertThat(result.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.VELOCITY);
    assertThat(result.getValue()).isEqualTo(2.3d);
    assertThat(result.isTrial()).isFalse();
    assertThat(result.getCalculatedFrom().getAsList()).isEqualTo(
        List.of(new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.VELOCITY, 2.3d),
            BoatGlobalValues.ID)));
    assertThat(result.getCalculatedBy()).isEqualTo("Boot:Geschwindigkeit");
  }


  @Test
  public void calculate_sourceIsSetAndTargetIsTrialValueCloseToSourceValue()
  {
    givenThatBoatVelocityIsKnown();
    givenThatDaggerboardVelocityIsTrialValueCloseToSourceValue();

    boolean shouldContinue = quantityEquality.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isFalse();
    CalculatedPhysicalQuantityValue result = valuesAndCalculationRules.getValueSet(DaggerboardOrKeel.ID)
        .getCalculatedValue(PhysicalQuantity.VELOCITY);
    assertThat(result.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.VELOCITY);
    assertThat(result.getValue()).isEqualTo(2.3d);
    assertThat(result.isTrial()).isFalse();
    assertThat(result.getCalculatedFrom().getAsList()).isEqualTo(
        List.of(new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.VELOCITY, 2.3d),
            BoatGlobalValues.ID)));
    assertThat(result.getCalculatedBy()).isEqualTo("Boot:Geschwindigkeit");
  }

  @Test
  public void calculate_sourceIsTrialValue()
  {
    givenThatBoatVelocityIsTrialValue();

    boolean shouldContinue = quantityEquality.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isTrue();
    CalculatedPhysicalQuantityValue result = valuesAndCalculationRules.getValueSet(DaggerboardOrKeel.ID)
        .getCalculatedValue(PhysicalQuantity.VELOCITY);
    assertThat(result.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.VELOCITY);
    assertThat(result.getValue()).isEqualTo(2.3d);
    assertThat(result.isTrial()).isTrue();
    assertThat(result.getCalculatedFrom().getAsList()).isEqualTo(
        List.of(new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.VELOCITY, 2.3d),
            BoatGlobalValues.ID)));
    assertThat(result.getCalculatedBy()).isEqualTo("Boot:Geschwindigkeit");
  }

  private void givenThatBoatVelocityIsKnown()
  {
    valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID).setStartValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.VELOCITY, 2.3d));
  }

  private void givenThatBoatVelocityIsTrialValue()
  {
    valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID).setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.VELOCITY, 2.3d), "me", true);
  }

  private void givenThatDaggerboardVelocityIsThree()
  {
    valuesAndCalculationRules.getValueSet(DaggerboardOrKeel.ID).setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.VELOCITY, 3d), "me", false);
  }

  private void givenThatDaggerboardVelocityIsTrialValue()
  {
    valuesAndCalculationRules.getValueSet(DaggerboardOrKeel.ID).setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.VELOCITY, 3d), "me", true);
  }

  private void givenThatDaggerboardVelocityIsTrialValueCloseToSourceValue()
  {
    double sourceValue = valuesAndCalculationRules
        .getValueSet(BoatGlobalValues.ID)
        .getKnownQuantityValue(PhysicalQuantity.VELOCITY)
        .getValue();

    valuesAndCalculationRules.getValueSet(DaggerboardOrKeel.ID).setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.VELOCITY, sourceValue*1.000001d), "me", true);
  }
}
