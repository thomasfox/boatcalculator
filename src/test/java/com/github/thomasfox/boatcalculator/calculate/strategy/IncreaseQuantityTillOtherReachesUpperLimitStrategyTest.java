package com.github.thomasfox.boatcalculator.calculate.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.value.CalculatedPhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.valueset.ValuesAndCalculationRules;
import com.github.thomasfox.boatcalculator.valueset.impl.Crew;
import com.github.thomasfox.boatcalculator.valueset.impl.Rigg;

public class IncreaseQuantityTillOtherReachesUpperLimitStrategyTest
{
  ValuesAndCalculationRules valuesAndCalculationRules;

  IncreaseQuantityTillOtherReachesUpperLimitStrategy increaseQuantityTillOtherReachesUpperLimitStrategy;

  @Before
  public void before()
  {
    increaseQuantityTillOtherReachesUpperLimitStrategy
        = new IncreaseQuantityTillOtherReachesUpperLimitStrategy(
            PhysicalQuantity.LEVER_WEIGHT, Crew.ID, 1.8,
            PhysicalQuantity.LIFT_COEFFICIENT_3D, Rigg.ID, 1.5);

    valuesAndCalculationRules = new ValuesAndCalculationRules();
    valuesAndCalculationRules.add(new Crew());
    valuesAndCalculationRules.add(new Rigg());

    valuesAndCalculationRules.add(increaseQuantityTillOtherReachesUpperLimitStrategy);
  }


  @Test
  public void step_scannedQuantityNotKnown()
  {
    boolean shouldContinue
        = increaseQuantityTillOtherReachesUpperLimitStrategy.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isTrue();

    assertThat(valuesAndCalculationRules.getValueSet(Crew.ID)
                .getCalculatedValue(PhysicalQuantity.LEVER_WEIGHT))
        .isNull();

    CalculatedPhysicalQuantityValue liftCoefficient3D
        = valuesAndCalculationRules.getValueSet(Rigg.ID)
            .getCalculatedValue(PhysicalQuantity.LIFT_COEFFICIENT_3D);
    assertThat(liftCoefficient3D.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.LIFT_COEFFICIENT_3D);
    assertThat(liftCoefficient3D.getValue()).isEqualTo(1.5d);
    assertThat(liftCoefficient3D.isTrial()).isTrue();
    assertThat(liftCoefficient3D.getCalculatedBy()).isEqualTo(
        "IncreaseQuantityTillOtherReachesUpperLimitStrategy trial Value");
    assertThat(liftCoefficient3D.getCalculatedFrom().getAsList()).isEqualTo(List.of(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.LIFT_COEFFICIENT_3D, 1.5d), Rigg.ID)));
  }

  @Test
  public void step_scannedQuantityAlreadyKnown()
  {
    givenThatLiftCoefficient3DIsOneAndNoTrialValue();

    boolean shouldContinue
        = increaseQuantityTillOtherReachesUpperLimitStrategy.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isFalse();

    assertThat(valuesAndCalculationRules.getValueSet(Crew.ID)
                .getCalculatedValue(PhysicalQuantity.LEVER_WEIGHT))
        .isNull();

    CalculatedPhysicalQuantityValue liftCoefficient3D
        = valuesAndCalculationRules.getValueSet(Rigg.ID)
            .getCalculatedValue(PhysicalQuantity.LIFT_COEFFICIENT_3D);
    assertThat(liftCoefficient3D.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.LIFT_COEFFICIENT_3D);
    assertThat(liftCoefficient3D.getValue()).isEqualTo(1d);
    assertThat(liftCoefficient3D.isTrial()).isFalse();
    assertThat(liftCoefficient3D.getCalculatedBy()).isEqualTo("me");
    assertThat(liftCoefficient3D.getCalculatedFrom().getAsList()).isEmpty();
  }

@Test
  public void step_scannedQuantityTrialValueAndLimitedQuantityUnknown()
  {
    givenThatLiftCoefficient3DIsOneAndTrialValue();

    boolean shouldContinue
        = increaseQuantityTillOtherReachesUpperLimitStrategy.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isFalse();

    assertThat(valuesAndCalculationRules.getValueSet(Crew.ID)
                .getCalculatedValue(PhysicalQuantity.LEVER_WEIGHT))
        .isNull();

    CalculatedPhysicalQuantityValue liftCoefficient3D
        = valuesAndCalculationRules.getValueSet(Rigg.ID)
            .getCalculatedValue(PhysicalQuantity.LIFT_COEFFICIENT_3D);
    assertThat(liftCoefficient3D.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.LIFT_COEFFICIENT_3D);
    assertThat(liftCoefficient3D.getValue()).isEqualTo(1d);
    assertThat(liftCoefficient3D.isTrial()).isTrue();
    assertThat(liftCoefficient3D.getCalculatedBy()).isEqualTo("me");
    assertThat(liftCoefficient3D.getCalculatedFrom().getAsList()).isEmpty();
  }

  @Test
  public void step_scannedQuantityTrialValueAndlimitedQuantityIsTrialBelowLimit()
  {
    givenThatLiftCoefficient3DIsOneAndTrialValue();
    givenThatLeverWeightIsKnownAndBelowThreshold();

    boolean shouldContinue
      = increaseQuantityTillOtherReachesUpperLimitStrategy.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isTrue();

    CalculatedPhysicalQuantityValue leverWeight
        = valuesAndCalculationRules.getValueSet(Crew.ID)
            .getCalculatedValue(PhysicalQuantity.LEVER_WEIGHT);
    assertThat(leverWeight.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.LEVER_WEIGHT);
    assertThat(leverWeight.getValue()).isEqualTo(0.9d);
    assertThat(leverWeight.isTrial()).isTrue();
    assertThat(leverWeight.getCalculatedBy()).isEqualTo("me");
    assertThat(leverWeight.getCalculatedFrom().getAsList()).isEmpty();

    CalculatedPhysicalQuantityValue liftCoefficient3D
        = valuesAndCalculationRules.getValueSet(Rigg.ID)
            .getCalculatedValue(PhysicalQuantity.LIFT_COEFFICIENT_3D);
    assertThat(liftCoefficient3D.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.LIFT_COEFFICIENT_3D);
    assertThat(liftCoefficient3D.getValue()).isEqualTo(1.5d);
    assertThat(liftCoefficient3D.isTrial()).isTrue();
    assertThat(liftCoefficient3D.getCalculatedBy()).isEqualTo(
        "IncreaseQuantityTillOtherReachesUpperLimitStrategy trial Value");
    assertThat(liftCoefficient3D.getCalculatedFrom().getAsList()).isEqualTo(List.of(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.LEVER_WEIGHT, 0.9d), Crew.ID)));
  }

  @Test
  public void step_scannedQuantityTrialValueAndlimitedQuantityIsTrialAboveLimit()
  {
    givenThatLeverWeightIsKnownAndAboveThreshold();
    givenThatLiftCoefficient3DIsOneAndTrialValue();

    boolean shouldContinue
      = increaseQuantityTillOtherReachesUpperLimitStrategy.step(valuesAndCalculationRules);

    assertThat(shouldContinue).isTrue();

    CalculatedPhysicalQuantityValue leverWeight
        = valuesAndCalculationRules.getValueSet(Crew.ID)
            .getCalculatedValue(PhysicalQuantity.LEVER_WEIGHT);
    assertThat(leverWeight.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.LEVER_WEIGHT);
    assertThat(leverWeight.getValue()).isEqualTo(3.6d);
    assertThat(leverWeight.isTrial()).isTrue();
    assertThat(leverWeight.getCalculatedBy()).isEqualTo("me");
    assertThat(leverWeight.getCalculatedFrom().getAsList()).isEmpty();

    CalculatedPhysicalQuantityValue liftCoefficient3D
        = valuesAndCalculationRules.getValueSet(Rigg.ID)
            .getCalculatedValue(PhysicalQuantity.LIFT_COEFFICIENT_3D);
    assertThat(liftCoefficient3D.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.LIFT_COEFFICIENT_3D);
    assertThat(liftCoefficient3D.getValue()).isEqualTo(0d);
    assertThat(liftCoefficient3D.isTrial()).isTrue();
    assertThat(liftCoefficient3D.getCalculatedBy()).isEqualTo(
        "IncreaseQuantityTillOtherReachesUpperLimitStrategy trial Value");
    assertThat(liftCoefficient3D.getCalculatedFrom().getAsList()).isEqualTo(List.of(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.LEVER_WEIGHT, 3.6d), Crew.ID)));
  }

  public void givenThatLiftCoefficient3DIsOneAndNoTrialValue()
  {
    valuesAndCalculationRules.getValueSet(Rigg.ID).setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.LIFT_COEFFICIENT_3D, 1d), "me", false);
  }

  public void givenThatLiftCoefficient3DIsOneAndTrialValue()
  {
    valuesAndCalculationRules.getValueSet(Rigg.ID).setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.LIFT_COEFFICIENT_3D, 1d), "me", true);
  }

  public void givenThatLeverWeightIsKnownAndBelowThreshold()
  {
    valuesAndCalculationRules.getValueSet(Crew.ID).setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.LEVER_WEIGHT, 0.9d), "me", true);
  }

  public void givenThatLeverWeightIsKnownAndAboveThreshold()
  {
    valuesAndCalculationRules.getValueSet(Crew.ID).setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.LEVER_WEIGHT, 3.6d), "me", true);
  }

}
