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

public class DriftToStabilityTest
{

  ValuesAndCalculationRules valuesAndCalculationRules;

  DriftToStableStateStrategy driftToStableStateStrategy;

  @Before
  public void before()
  {
    driftToStableStateStrategy = new DriftToStableStateStrategy(
        PhysicalQuantity.ANGLE_OF_ATTACK, DaggerboardOrKeel.ID,
        PhysicalQuantity.DRIFT_ANGLE, BoatGlobalValues.ID,
        0d);

    valuesAndCalculationRules = new ValuesAndCalculationRules();
    valuesAndCalculationRules.add(new BoatGlobalValues());
    valuesAndCalculationRules.add(new DaggerboardOrKeel());

    valuesAndCalculationRules.add(driftToStableStateStrategy);
  }

  @Test
  public void step_targetAlreadyKnown()
  {
    givenThatDriftAngleIsKnown();

    boolean shouldContinue = driftToStableStateStrategy.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isFalse();
    assertThat(
            valuesAndCalculationRules.getValueSet(DaggerboardOrKeel.ID)
                .getCalculatedValue(PhysicalQuantity.ANGLE_OF_ATTACK))
        .isNull();
    assertThat(
        valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID)
            .getCalculatedValue(PhysicalQuantity.DRIFT_ANGLE))
        .isNull();
  }

  @Test
  public void step_sourceAlreadyKnown()
  {
    givenThatAngleOfAttackIsKnown();

    boolean shouldContinue = driftToStableStateStrategy.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isFalse();
    assertThat(
            valuesAndCalculationRules.getValueSet(DaggerboardOrKeel.ID)
                .getCalculatedValue(PhysicalQuantity.ANGLE_OF_ATTACK))
        .isNull();
    CalculatedPhysicalQuantityValue driftAngle
        = valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID)
            .getCalculatedValue(PhysicalQuantity.DRIFT_ANGLE);
    assertThat(driftAngle.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.DRIFT_ANGLE);
    assertThat(driftAngle.getValue()).isEqualTo(0d);
    assertThat(driftAngle.isTrial()).isTrue();
    assertThat(driftAngle.getCalculatedBy()).isEqualTo("DriftToStableStateStrategy trial value");
    assertThat(driftAngle.getCalculatedFrom().getAsList()).isEqualTo(List.of(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.DRIFT_ANGLE, 0d), BoatGlobalValues.ID)));
  }

  @Test
  public void step_nothingSet()
  {
    boolean shouldContinue = driftToStableStateStrategy.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isFalse();
    assertThat(
        valuesAndCalculationRules.getValueSet(DaggerboardOrKeel.ID)
            .getCalculatedValue(PhysicalQuantity.ANGLE_OF_ATTACK))
        .isNull();
    CalculatedPhysicalQuantityValue driftAngle
        = valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID)
            .getCalculatedValue(PhysicalQuantity.DRIFT_ANGLE);
    assertThat(driftAngle.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.DRIFT_ANGLE);
    assertThat(driftAngle.getValue()).isEqualTo(0d);
    assertThat(driftAngle.isTrial()).isTrue();
    assertThat(driftAngle.getCalculatedBy()).isEqualTo("DriftToStableStateStrategy trial value");
    assertThat(driftAngle.getCalculatedFrom().getAsList()).isEqualTo(List.of(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.DRIFT_ANGLE, 0d), BoatGlobalValues.ID)));
  }

  @Test
  public void step_sourceAndTargetAreSet()
  {
    givenThatDriftAngleIsTrialValue();
    givenThatAngleOfAttackIsTrialValue();

    boolean shouldContinue = driftToStableStateStrategy.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isTrue();

    CalculatedPhysicalQuantityValue angleOfAttack
        = valuesAndCalculationRules.getValueSet(DaggerboardOrKeel.ID)
            .getCalculatedValue(PhysicalQuantity.ANGLE_OF_ATTACK);
    assertThat(angleOfAttack.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.ANGLE_OF_ATTACK);
    assertThat(angleOfAttack.getValue()).isEqualTo(3d);
    assertThat(angleOfAttack.isTrial()).isTrue();
    assertThat(angleOfAttack.getCalculatedBy()).isEqualTo("meToo");
    assertThat(angleOfAttack.getCalculatedFrom().getAsList()).isEmpty();

    CalculatedPhysicalQuantityValue driftAngle
        = valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID)
            .getCalculatedValue(PhysicalQuantity.DRIFT_ANGLE);
    assertThat(driftAngle.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.DRIFT_ANGLE);
    assertThat(driftAngle.getValue()).isEqualTo(3d);
    assertThat(driftAngle.isTrial()).isTrue();
    assertThat(driftAngle.getCalculatedBy()).isEqualTo("DriftToStableStateStrategy trial value");
    assertThat(driftAngle.getCalculatedFrom().getAsList()).isEqualTo(List.of(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, 3d), DaggerboardOrKeel.ID)));
  }

  @Test
  public void step_sourceAndTargetAreSetToNearlySameValue()
  {
    givenThatDriftAngleIsTrialValue();
    givenThatAngleOfAttackIsTrialCloseToDriftAngle();

    boolean shouldContinue = driftToStableStateStrategy.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isFalse();

    CalculatedPhysicalQuantityValue angleOfAttack
        = valuesAndCalculationRules.getValueSet(DaggerboardOrKeel.ID)
            .getCalculatedValue(PhysicalQuantity.ANGLE_OF_ATTACK);
    assertThat(angleOfAttack.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.ANGLE_OF_ATTACK);
    assertThat(angleOfAttack.getValue()).isEqualTo(2d * 1.0001);
    assertThat(angleOfAttack.isTrial()).isTrue();
    assertThat(angleOfAttack.getCalculatedBy()).isEqualTo("meToo");
    assertThat(angleOfAttack.getCalculatedFrom().getAsList()).isEmpty();

    CalculatedPhysicalQuantityValue driftAngle
        = valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID)
            .getCalculatedValue(PhysicalQuantity.DRIFT_ANGLE);
    assertThat(driftAngle.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.DRIFT_ANGLE);
    assertThat(driftAngle.getValue()).isEqualTo(2d * 1.0001);
    assertThat(driftAngle.isTrial()).isTrue();
    assertThat(driftAngle.getCalculatedBy()).isEqualTo("DriftToStableStateStrategy trial value");
    assertThat(driftAngle.getCalculatedFrom().getAsList()).isEqualTo(List.of(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, 2d * 1.0001d), DaggerboardOrKeel.ID)));
  }

  @Test
  public void step_sourceAndTargetAreSetToSameValue()
  {
    givenThatDriftAngleIsTrialValue();
    givenThatAngleOfAttackIsTrialEqualToDriftAngle();

    boolean shouldContinue = driftToStableStateStrategy.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isFalse();

    CalculatedPhysicalQuantityValue angleOfAttack
        = valuesAndCalculationRules.getValueSet(DaggerboardOrKeel.ID)
            .getCalculatedValue(PhysicalQuantity.ANGLE_OF_ATTACK);
    assertThat(angleOfAttack.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.ANGLE_OF_ATTACK);
    assertThat(angleOfAttack.getValue()).isEqualTo(2d);
    assertThat(angleOfAttack.isTrial()).isTrue();
    assertThat(angleOfAttack.getCalculatedBy()).isEqualTo("meToo");
    assertThat(angleOfAttack.getCalculatedFrom().getAsList()).isEmpty();

    CalculatedPhysicalQuantityValue driftAngle
        = valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID)
            .getCalculatedValue(PhysicalQuantity.DRIFT_ANGLE);
    assertThat(driftAngle.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.DRIFT_ANGLE);
    assertThat(driftAngle.getValue()).isEqualTo(2d);
    assertThat(driftAngle.isTrial()).isTrue();
    assertThat(driftAngle.getCalculatedBy()).isEqualTo("DriftToStableStateStrategy trial value");
    assertThat(driftAngle.getCalculatedFrom().getAsList()).isEqualTo(List.of(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, 2d), DaggerboardOrKeel.ID)));
  }



  private void givenThatDriftAngleIsKnown()
  {
    valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID).setStartValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.DRIFT_ANGLE, 2d));
  }

  private void givenThatAngleOfAttackIsKnown()
  {
    valuesAndCalculationRules.getValueSet(DaggerboardOrKeel.ID).setStartValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, 3d));
  }

  private void givenThatDriftAngleIsTrialValue()
  {
    valuesAndCalculationRules.getValueSet(BoatGlobalValues.ID).setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.DRIFT_ANGLE, 2d), "me", true);
  }

  private void givenThatAngleOfAttackIsTrialValue()
  {
    valuesAndCalculationRules.getValueSet(DaggerboardOrKeel.ID).setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, 3d), "meToo", true);
  }

  private void givenThatAngleOfAttackIsTrialCloseToDriftAngle()
  {
    double driftAngle = valuesAndCalculationRules.getKnownValue(
        new PhysicalQuantityInSet(PhysicalQuantity.DRIFT_ANGLE, BoatGlobalValues.ID));
    valuesAndCalculationRules.getValueSet(DaggerboardOrKeel.ID).setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, driftAngle * 1.0001), "meToo", true);
  }

  private void givenThatAngleOfAttackIsTrialEqualToDriftAngle()
  {
    double driftAngle = valuesAndCalculationRules.getKnownValue(
        new PhysicalQuantityInSet(PhysicalQuantity.DRIFT_ANGLE, BoatGlobalValues.ID));
    valuesAndCalculationRules.getValueSet(DaggerboardOrKeel.ID).setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, driftAngle), "meToo", true);
  }

}
