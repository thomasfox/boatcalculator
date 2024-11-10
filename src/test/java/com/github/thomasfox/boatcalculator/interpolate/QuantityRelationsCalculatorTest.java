package com.github.thomasfox.boatcalculator.interpolate;

import static  org.assertj.core.api.Assertions.assertThat;
import static  org.assertj.core.api.Assertions.offset;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.value.CalculatedPhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValues;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.valueset.impl.DaggerboardOrKeel;
import com.github.thomasfox.boatcalculator.xfoil.XfoilResultLoader;

public class QuantityRelationsCalculatorTest
{
  private final QuantityRelationsCalculator quantityRelationsCalculator = new QuantityRelationsCalculator();

  private final XfoilResultLoader xfoilResultLoader = new XfoilResultLoader();

  private QuantityRelation reynolds100000QuantityRelation;

  private QuantityRelation reynolds200000QuantityRelation;

  private DaggerboardOrKeel daggerboardOrKeel;

  @Before
  public void loadQuantityRelations()
  {
    Reader reader = new InputStreamReader(
        getClass().getResourceAsStream("/xfoil-100000.txt"),
        StandardCharsets.ISO_8859_1);
    reynolds100000QuantityRelation = xfoilResultLoader.load(reader);

    reader = new InputStreamReader(
        getClass().getResourceAsStream("/xfoil-200000.txt"),
        StandardCharsets.ISO_8859_1);
    reynolds200000QuantityRelation = xfoilResultLoader.load(reader);
  }

  @Before
  public void resetValueSet()
  {
    daggerboardOrKeel = new DaggerboardOrKeel();
  }

  @Test
  public void applyQuantityRelations_noNoFixedValues()
  {
    givenQuantityRelationsContainRe100000QuantityRelation();

    Set<String> changed = quantityRelationsCalculator.applyQuantityRelations(daggerboardOrKeel);

    assertThat(changed).isEmpty();

    assertThat(daggerboardOrKeel.getCalculatedValues().getAsList()).hasSize(0);
  }

  @Test
  public void applyQuantityRelations_singleQuantityRelationAndMatchingFixedQuantity()
  {
    givenQuantityRelationsContainRe200000QuantityRelation();
    givenDaggerboardHasRe200000();
    givenAngleOfAttackIsZero();
    givenProfileDragCoefficientIsTrialValue(); // trial result values should be overwritten

    Set<String> changed = quantityRelationsCalculator.applyQuantityRelations(daggerboardOrKeel);

    assertThat(changed).containsOnly("DaggerboardOrKeel:Calculated polar for: XY 123");

    assertThat(daggerboardOrKeel.getCalculatedValues().getAsList()).hasSize(2);

    // values from xfoil-200000.txt for angle of attack 0
    CalculatedPhysicalQuantityValue liftCoefficient
        = daggerboardOrKeel.getCalculatedValue(PhysicalQuantity.LIFT_COEFFICIENT);
    assertThat(liftCoefficient.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.LIFT_COEFFICIENT);
    assertThat(liftCoefficient.getValue()).isEqualTo(0d);
    assertThat(liftCoefficient.isTrial()).isFalse();
    assertThat(liftCoefficient.getCalculatedBy()).isEqualTo("Calculated polar for: XY 123");
    assertThat(liftCoefficient.getCalculatedFrom().getAsList()).containsOnly(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.NCRIT, 9d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.REYNOLDS_NUMBER, 200000d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, 0d), DaggerboardOrKeel.ID));

    CalculatedPhysicalQuantityValue profileDragCoefficient
        = daggerboardOrKeel.getCalculatedValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    assertThat(profileDragCoefficient.getPhysicalQuantity())
        .isEqualTo(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    assertThat(profileDragCoefficient.getValue()).isEqualTo(0.05d);
    assertThat(profileDragCoefficient.isTrial()).isFalse();
    assertThat(profileDragCoefficient.getCalculatedBy()).isEqualTo("Calculated polar for: XY 123");
    assertThat(profileDragCoefficient.getCalculatedFrom().getAsList()).containsOnly(
        new SimplePhysicalQuantityValueWithSetId(new SimplePhysicalQuantityValue(
            PhysicalQuantity.NCRIT, 9d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(new SimplePhysicalQuantityValue(
            PhysicalQuantity.REYNOLDS_NUMBER, 200000d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(new SimplePhysicalQuantityValue(
            PhysicalQuantity.ANGLE_OF_ATTACK, 0d), DaggerboardOrKeel.ID));
  }

  /**
   * This checks that even if the fixed value for the only existing quantity is not matching,
   * it is used for calculation anyway.
   * Whether this is good or bad can be discussed, but it is the current behavior.
   */
  @Test
  public void applyQuantityRelations_singleQuantityRelationAndNonmatchingFixedQuantity()
  {
    givenQuantityRelationsContainRe100000QuantityRelation();
    givenDaggerboardHasRe200000();
    givenAngleOfAttackIsZero();
    givenProfileDragCoefficientIsTrialValue(); // trial result values should be overwritten

    Set<String> changed = quantityRelationsCalculator.applyQuantityRelations(daggerboardOrKeel);

    assertThat(changed).containsOnly("DaggerboardOrKeel:Calculated polar for: XY 123");

    assertThat(daggerboardOrKeel.getCalculatedValues().getAsList()).hasSize(2);

    // values from xfoil-100000.txt for angle of attack 0
    CalculatedPhysicalQuantityValue liftCoefficient
        = daggerboardOrKeel.getCalculatedValue(PhysicalQuantity.LIFT_COEFFICIENT);
    assertThat(liftCoefficient.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.LIFT_COEFFICIENT);
    assertThat(liftCoefficient.getValue()).isEqualTo(0d);
    assertThat(liftCoefficient.isTrial()).isFalse();
    assertThat(liftCoefficient.getCalculatedBy()).isEqualTo("Calculated polar for: XY 123");
    assertThat(liftCoefficient.getCalculatedFrom().getAsList()).containsOnly(
        new SimplePhysicalQuantityValueWithSetId(new SimplePhysicalQuantityValue(
            PhysicalQuantity.NCRIT, 9d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(new SimplePhysicalQuantityValue(
            PhysicalQuantity.REYNOLDS_NUMBER, 200000d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(new SimplePhysicalQuantityValue(
            PhysicalQuantity.ANGLE_OF_ATTACK, 0d), DaggerboardOrKeel.ID));

    CalculatedPhysicalQuantityValue profileDragCoefficient
        = daggerboardOrKeel.getCalculatedValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    assertThat(profileDragCoefficient.getPhysicalQuantity())
        .isEqualTo(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    assertThat(profileDragCoefficient.getValue()).isEqualTo(0.1d);
    assertThat(profileDragCoefficient.isTrial()).isFalse();
    assertThat(profileDragCoefficient.getCalculatedBy()).isEqualTo("Calculated polar for: XY 123");
    assertThat(profileDragCoefficient.getCalculatedFrom().getAsList()).containsOnly(
        new SimplePhysicalQuantityValueWithSetId(new SimplePhysicalQuantityValue(
            PhysicalQuantity.NCRIT, 9d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(new SimplePhysicalQuantityValue(
            PhysicalQuantity.REYNOLDS_NUMBER, 200000d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(new SimplePhysicalQuantityValue(
            PhysicalQuantity.ANGLE_OF_ATTACK, 0d), DaggerboardOrKeel.ID));
  }

  @Test
  public void applyQuantityRelations_interpolatedFixedQuantity()
  {
    givenQuantityRelationsContainRe100000QuantityRelation();
    givenQuantityRelationsContainRe200000QuantityRelation();
    givenDaggerboardHasRe150000();
    givenAngleOfAttackIsZero();
    givenProfileDragCoefficientIsTrialValue(); // trial result values should be overwritten

    Set<String> changed = quantityRelationsCalculator.applyQuantityRelations(daggerboardOrKeel);

    assertThat(changed).containsOnly("DaggerboardOrKeel:Calculated polar for: XY 123");

    assertThat(daggerboardOrKeel.getCalculatedValues().getAsList()).hasSize(2);

    // values interpolated between xfoil-100000.txt and xfoil-200000.txt for angle of attack 0
    CalculatedPhysicalQuantityValue liftCoefficient
        = daggerboardOrKeel.getCalculatedValue(PhysicalQuantity.LIFT_COEFFICIENT);
    assertThat(liftCoefficient.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.LIFT_COEFFICIENT);
    assertThat(liftCoefficient.getValue()).isEqualTo(0d);
    assertThat(liftCoefficient.isTrial()).isFalse();
    assertThat(liftCoefficient.getCalculatedBy()).isEqualTo(
        "Calculated polar for: XY 123 and Calculated polar for: XY 123");
    assertThat(liftCoefficient.getCalculatedFrom().getAsList()).containsOnly(
        new SimplePhysicalQuantityValueWithSetId(new SimplePhysicalQuantityValue(
            PhysicalQuantity.NCRIT, 9d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(new SimplePhysicalQuantityValue(
            PhysicalQuantity.REYNOLDS_NUMBER, 150000d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(new SimplePhysicalQuantityValue(
            PhysicalQuantity.ANGLE_OF_ATTACK, 0d), DaggerboardOrKeel.ID));

    CalculatedPhysicalQuantityValue profileDragCoefficient
        = daggerboardOrKeel.getCalculatedValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    assertThat(profileDragCoefficient.getPhysicalQuantity())
        .isEqualTo(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    assertThat(profileDragCoefficient.getValue()).isEqualTo((0.1d + 0.05d) / 2);
    assertThat(profileDragCoefficient.isTrial()).isFalse();
    assertThat(profileDragCoefficient.getCalculatedBy()).isEqualTo(
        "Calculated polar for: XY 123 and Calculated polar for: XY 123");
    assertThat(profileDragCoefficient.getCalculatedFrom().getAsList()).containsOnly(
        new SimplePhysicalQuantityValueWithSetId(new SimplePhysicalQuantityValue(
            PhysicalQuantity.NCRIT, 9d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(new SimplePhysicalQuantityValue(
            PhysicalQuantity.REYNOLDS_NUMBER, 150000d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(new SimplePhysicalQuantityValue(
            PhysicalQuantity.ANGLE_OF_ATTACK, 0d), DaggerboardOrKeel.ID));
  }

  @Test
  public void applyQuantityRelations_interpolatedFixedQuantityOutsideOfRange()
  {
    givenQuantityRelationsContainRe100000QuantityRelation();
    givenQuantityRelationsContainRe200000QuantityRelation();
    givenDaggerboardHasRe300000();
    givenAngleOfAttackIsZero();
    givenProfileDragCoefficientIsTrialValue(); // trial result values should be overwritten

    Set<String> changed = quantityRelationsCalculator.applyQuantityRelations(daggerboardOrKeel);

    assertThat(changed).containsOnly("DaggerboardOrKeel:Calculated polar for: XY 123");

    assertThat(daggerboardOrKeel.getCalculatedValues().getAsList()).hasSize(2);

    // values from xfoil-200000.txt for angle of attack 0
    CalculatedPhysicalQuantityValue liftCoefficient
        = daggerboardOrKeel.getCalculatedValue(PhysicalQuantity.LIFT_COEFFICIENT);
    assertThat(liftCoefficient.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.LIFT_COEFFICIENT);
    assertThat(liftCoefficient.getValue()).isEqualTo(0d);
    assertThat(liftCoefficient.isTrial()).isFalse();
    assertThat(liftCoefficient.getCalculatedBy()).isEqualTo("Calculated polar for: XY 123");
    assertThat(liftCoefficient.getCalculatedFrom().getAsList()).containsOnly(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.NCRIT, 9d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.REYNOLDS_NUMBER, 300000d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, 0d), DaggerboardOrKeel.ID));

    CalculatedPhysicalQuantityValue profileDragCoefficient
        = daggerboardOrKeel.getCalculatedValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    assertThat(profileDragCoefficient.getPhysicalQuantity())
        .isEqualTo(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    assertThat(profileDragCoefficient.getValue()).isEqualTo(0.05d);
    assertThat(profileDragCoefficient.isTrial()).isFalse();
    assertThat(liftCoefficient.getCalculatedBy()).isEqualTo("Calculated polar for: XY 123");
    assertThat(profileDragCoefficient.getCalculatedFrom().getAsList()).containsOnly(
        new SimplePhysicalQuantityValueWithSetId(new SimplePhysicalQuantityValue(
            PhysicalQuantity.NCRIT, 9d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(new SimplePhysicalQuantityValue(
            PhysicalQuantity.REYNOLDS_NUMBER, 300000d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(new SimplePhysicalQuantityValue(
            PhysicalQuantity.ANGLE_OF_ATTACK, 0d), DaggerboardOrKeel.ID));
  }

  @Test
  public void applyQuantityRelations_interpolatedCalculatedQuantity()
  {
    givenQuantityRelationsContainRe200000QuantityRelation();
    givenDaggerboardHasRe200000();
    givenAngleOfAttackIsFive();
    givenProfileDragCoefficientIsTrialValue(); // trial result values should be overwritten

    Set<String> changed = quantityRelationsCalculator.applyQuantityRelations(daggerboardOrKeel);

    assertThat(changed).containsOnly("DaggerboardOrKeel:Calculated polar for: XY 123");

    assertThat(daggerboardOrKeel.getCalculatedValues().getAsList()).hasSize(2);

    // values from xfoil-200000.txt interpolated for angle of attack 5
    CalculatedPhysicalQuantityValue liftCoefficient
        = daggerboardOrKeel.getCalculatedValue(PhysicalQuantity.LIFT_COEFFICIENT);
    assertThat(liftCoefficient.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.LIFT_COEFFICIENT);
    assertThat(liftCoefficient.getValue()).isEqualTo(1.1d/2);
    assertThat(liftCoefficient.isTrial()).isFalse();
    assertThat(liftCoefficient.getCalculatedBy()).isEqualTo("Calculated polar for: XY 123");
    assertThat(liftCoefficient.getCalculatedFrom().getAsList()).containsOnly(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.NCRIT, 9d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.REYNOLDS_NUMBER, 200000d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, 5d), DaggerboardOrKeel.ID));

    CalculatedPhysicalQuantityValue profileDragCoefficient
        = daggerboardOrKeel.getCalculatedValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    assertThat(profileDragCoefficient.getPhysicalQuantity())
        .isEqualTo(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    assertThat(profileDragCoefficient.getValue()).isEqualTo((0.05d + 0.1d)/2);
    assertThat(profileDragCoefficient.isTrial()).isFalse();
    assertThat(liftCoefficient.getCalculatedBy()).isEqualTo("Calculated polar for: XY 123");
    assertThat(profileDragCoefficient.getCalculatedFrom().getAsList()).containsOnly(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.NCRIT, 9d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.REYNOLDS_NUMBER, 200000d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, 5d), DaggerboardOrKeel.ID));
  }

  @Test
  public void applyQuantityRelations_interpolatedCalculatedQuantity_targetsAreCloseToCalculatedValues()
  {
    givenQuantityRelationsContainRe200000QuantityRelation();
    givenDaggerboardHasRe200000();
    givenAngleOfAttackIsFive();
    givenProfileDragCoefficientIsTrialValueCloseToZeroPointZeroSevenFive();
    givenLiftCoefficientIsTrialValueCloseToZeroPointFiveFive();

    Set<String> changed = quantityRelationsCalculator.applyQuantityRelations(daggerboardOrKeel);

    assertThat(changed).isEmpty();

    assertThat(daggerboardOrKeel.getCalculatedValues().getAsList()).hasSize(2);

    // values from xfoil-200000.txt interpolated for angle of attack 5
    CalculatedPhysicalQuantityValue liftCoefficient
        = daggerboardOrKeel.getCalculatedValue(PhysicalQuantity.LIFT_COEFFICIENT);
    assertThat(liftCoefficient.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.LIFT_COEFFICIENT);
    assertThat(liftCoefficient.getValue()).isEqualTo(1.1d/2);
    assertThat(liftCoefficient.isTrial()).isFalse();
    assertThat(liftCoefficient.getCalculatedBy()).isEqualTo("Calculated polar for: XY 123");
    assertThat(liftCoefficient.getCalculatedFrom().getAsList()).containsOnly(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.NCRIT, 9d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.REYNOLDS_NUMBER, 200000d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, 5d), DaggerboardOrKeel.ID));

    CalculatedPhysicalQuantityValue profileDragCoefficient
        = daggerboardOrKeel.getCalculatedValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    assertThat(profileDragCoefficient.getPhysicalQuantity())
        .isEqualTo(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    assertThat(profileDragCoefficient.getValue()).isEqualTo((0.05d + 0.1d)/2);
    assertThat(profileDragCoefficient.isTrial()).isFalse();
    assertThat(liftCoefficient.getCalculatedBy()).isEqualTo("Calculated polar for: XY 123");
    assertThat(profileDragCoefficient.getCalculatedFrom().getAsList()).containsOnly(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.NCRIT, 9d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.REYNOLDS_NUMBER, 200000d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, 5d), DaggerboardOrKeel.ID));
  }

  @Test
  public void applyQuantityRelations_interpolatedFixedAndCalculatedQuantity()
  {
    givenQuantityRelationsContainRe100000QuantityRelation();
    givenQuantityRelationsContainRe200000QuantityRelation();
    givenDaggerboardHasRe150000();
    givenAngleOfAttackIsFive();
    givenProfileDragCoefficientIsTrialValue(); // trial result values should be overwritten

    Set<String> changed = quantityRelationsCalculator.applyQuantityRelations(daggerboardOrKeel);

    assertThat(changed).containsOnly("DaggerboardOrKeel:Calculated polar for: XY 123");
    assertThat(daggerboardOrKeel.getCalculatedValues().getAsList()).hasSize(2);

    // values from xfoil-200000.txt interpolated for angle of attack 5
    CalculatedPhysicalQuantityValue liftCoefficient
        = daggerboardOrKeel.getCalculatedValue(PhysicalQuantity.LIFT_COEFFICIENT);
    assertThat(liftCoefficient.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.LIFT_COEFFICIENT);
    assertThat(liftCoefficient.getValue()).isCloseTo((1d + 1.1d)/4, offset(0.00001));
    assertThat(liftCoefficient.isTrial()).isFalse();
    assertThat(liftCoefficient.getCalculatedBy()).isEqualTo(
        "Calculated polar for: XY 123 and Calculated polar for: XY 123");
    assertThat(liftCoefficient.getCalculatedFrom().getAsList()).containsOnly(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.NCRIT, 9d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.REYNOLDS_NUMBER, 150000d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, 5d), DaggerboardOrKeel.ID));

    CalculatedPhysicalQuantityValue profileDragCoefficient
        = daggerboardOrKeel.getCalculatedValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    assertThat(profileDragCoefficient.getPhysicalQuantity())
        .isEqualTo(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    assertThat(profileDragCoefficient.getValue()).isCloseTo((0.05d + 0.1d + 0.1d + 0.2d)/4, offset(0.00001));
    assertThat(profileDragCoefficient.isTrial()).isFalse();
    assertThat(profileDragCoefficient.getCalculatedBy()).isEqualTo(
        "Calculated polar for: XY 123 and Calculated polar for: XY 123");
    assertThat(profileDragCoefficient.getCalculatedFrom().getAsList()).containsOnly(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.NCRIT, 9d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.REYNOLDS_NUMBER, 150000d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, 5d), DaggerboardOrKeel.ID));
  }

  @Test
  public void applyQuantityRelations_interpolatedFixedAndCalculatedQuantity_targetsAreCloseToCalculatedValues()
  {
    givenQuantityRelationsContainRe100000QuantityRelation();
    givenQuantityRelationsContainRe200000QuantityRelation();
    givenDaggerboardHasRe150000();
    givenAngleOfAttackIsFive();
    givenProfileDragCoefficientIsTrialValueCloseToZeroPointOneOneTwoFive();
    givenLiftCoefficientIsTrialValueCloseToZeroPointFiveTwoFive();

    Set<String> changed = quantityRelationsCalculator.applyQuantityRelations(daggerboardOrKeel);

    assertThat(changed).isEmpty();
    assertThat(daggerboardOrKeel.getCalculatedValues().getAsList()).hasSize(2);

    // values from xfoil-200000.txt interpolated for angle of attack 5
    CalculatedPhysicalQuantityValue liftCoefficient
        = daggerboardOrKeel.getCalculatedValue(PhysicalQuantity.LIFT_COEFFICIENT);
    assertThat(liftCoefficient.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.LIFT_COEFFICIENT);
    assertThat(liftCoefficient.getValue()).isCloseTo((1d + 1.1d)/4, offset(0.00001));
    assertThat(liftCoefficient.isTrial()).isFalse();
    assertThat(liftCoefficient.getCalculatedBy()).isEqualTo(
        "Calculated polar for: XY 123 and Calculated polar for: XY 123");
    assertThat(liftCoefficient.getCalculatedFrom().getAsList()).containsOnly(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.NCRIT, 9d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.REYNOLDS_NUMBER, 150000d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, 5d), DaggerboardOrKeel.ID));

    CalculatedPhysicalQuantityValue profileDragCoefficient
        = daggerboardOrKeel.getCalculatedValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    assertThat(profileDragCoefficient.getPhysicalQuantity())
        .isEqualTo(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    assertThat(profileDragCoefficient.getValue()).isCloseTo((0.05d + 0.1d + 0.1d + 0.2d)/4, offset(0.00001));
    assertThat(profileDragCoefficient.isTrial()).isFalse();
    assertThat(profileDragCoefficient.getCalculatedBy()).isEqualTo(
        "Calculated polar for: XY 123 and Calculated polar for: XY 123");
    assertThat(profileDragCoefficient.getCalculatedFrom().getAsList()).containsOnly(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.NCRIT, 9d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.REYNOLDS_NUMBER, 150000d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, 5d), DaggerboardOrKeel.ID));
  }

  @Test
  public void applyQuantityRelations_interpolatedFixedAndCalculatedQuantityAndFixedValueIsTrial()
  {
    givenQuantityRelationsContainRe100000QuantityRelation();
    givenQuantityRelationsContainRe200000QuantityRelation();
    givenDaggerboardHasRe150000AsTrialValue();
    givenAngleOfAttackIsFive();

    quantityRelationsCalculator.applyQuantityRelations(daggerboardOrKeel);

    assertThat(daggerboardOrKeel.getCalculatedValues().getAsList()).hasSize(3);

    CalculatedPhysicalQuantityValue reynoldsNumber
        = daggerboardOrKeel.getCalculatedValue(PhysicalQuantity.REYNOLDS_NUMBER);
    assertThat(reynoldsNumber.isTrial()).isTrue();

    CalculatedPhysicalQuantityValue liftCoefficient
        = daggerboardOrKeel.getCalculatedValue(PhysicalQuantity.LIFT_COEFFICIENT);
    assertThat(liftCoefficient.isTrial()).isTrue();

    CalculatedPhysicalQuantityValue profileDragCoefficient
        = daggerboardOrKeel.getCalculatedValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    assertThat(profileDragCoefficient.isTrial()).isTrue();
  }

  @Test
  public void applyQuantityRelations_interpolatedFixedAndCalculatedQuantityAndProvidedValueIsTrial()
  {
    givenQuantityRelationsContainRe100000QuantityRelation();
    givenQuantityRelationsContainRe200000QuantityRelation();
    givenDaggerboardHasRe150000();
    givenAngleOfAttackIsFiveAsTrialValue();

    quantityRelationsCalculator.applyQuantityRelations(daggerboardOrKeel);

    assertThat(daggerboardOrKeel.getCalculatedValues().getAsList()).hasSize(3);

    CalculatedPhysicalQuantityValue angleOfAttack
        = daggerboardOrKeel.getCalculatedValue(PhysicalQuantity.ANGLE_OF_ATTACK);
    assertThat(angleOfAttack.isTrial()).isTrue();

    CalculatedPhysicalQuantityValue liftCoefficient
        = daggerboardOrKeel.getCalculatedValue(PhysicalQuantity.LIFT_COEFFICIENT);
    assertThat(liftCoefficient.isTrial()).isTrue();

    CalculatedPhysicalQuantityValue profileDragCoefficient
        = daggerboardOrKeel.getCalculatedValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    assertThat(profileDragCoefficient.isTrial()).isTrue();
  }

  @Test
  public void getFixedValueInterpolationCandidates_noQuantityRelationsNoFixedValues()
  {
    Map<PhysicalQuantityValues, QuantityRelation> result
        = quantityRelationsCalculator.getFixedValueInterpolationCandidates(daggerboardOrKeel);

    assertThat(result).hasSize(0);
  }

  @Test
  public void getFixedValueInterpolationCandidates_oneQuantityRelationsNoFixedValues()
  {
    givenQuantityRelationsContainRe100000QuantityRelation();

    Map<PhysicalQuantityValues, QuantityRelation> result
        = quantityRelationsCalculator.getFixedValueInterpolationCandidates(daggerboardOrKeel);

    assertThat(result).hasSize(1);

    PhysicalQuantityValues key = result.keySet().iterator().next();
    assertThat(key.getAsList()).hasSize(1);
    assertThat(key.getAsList()).containsOnly(
        new SimplePhysicalQuantityValue(PhysicalQuantity.REYNOLDS_NUMBER, 100000d));

    QuantityRelation quantityRelation = result.get(key);
    assertThat(quantityRelation).isSameAs(reynolds100000QuantityRelation);
  }

  @Test
  public void getFixedValueInterpolationCandidates_twoQuantityRelationsNoFixedValues()
  {
    givenQuantityRelationsContainRe100000QuantityRelation();
    givenQuantityRelationsContainRe200000QuantityRelation();

    Map<PhysicalQuantityValues, QuantityRelation> result
        = quantityRelationsCalculator.getFixedValueInterpolationCandidates(daggerboardOrKeel);

    assertThat(result).hasSize(2);
    Iterator<PhysicalQuantityValues> keyIterator = result.keySet().iterator();

    PhysicalQuantityValues key = keyIterator.next();
    assertThat(key.getAsList()).hasSize(1);
    assertThat(key.getAsList()).containsOnly(
        new SimplePhysicalQuantityValue(PhysicalQuantity.REYNOLDS_NUMBER, 100000d));

    QuantityRelation quantityRelation = result.get(key);
    assertThat(quantityRelation).isSameAs(reynolds100000QuantityRelation);

    key = keyIterator.next();
    assertThat(key.getAsList()).hasSize(1);
    assertThat(key.getAsList()).containsOnly(
        new SimplePhysicalQuantityValue(PhysicalQuantity.REYNOLDS_NUMBER, 200000d));

    quantityRelation = result.get(key);
    assertThat(quantityRelation).isSameAs(reynolds200000QuantityRelation);
  }

  @Test
  public void getFixedValueInterpolationCandidates_twoQuantityRelationsMatchingFixedValue()
  {
    givenQuantityRelationsContainRe100000QuantityRelation();
    givenQuantityRelationsContainRe200000QuantityRelation();
    givenDaggerboardHasRe200000();

    Map<PhysicalQuantityValues, QuantityRelation> result
        = quantityRelationsCalculator.getFixedValueInterpolationCandidates(daggerboardOrKeel);

    assertThat(result).hasSize(1);
    Iterator<PhysicalQuantityValues> keyIterator = result.keySet().iterator();

    PhysicalQuantityValues key = keyIterator.next();
    assertThat(key.getAsList()).isEmpty();

    QuantityRelation quantityRelation = result.get(key);
    assertThat(quantityRelation).isSameAs(reynolds200000QuantityRelation);
  }

  @Test
  public void getFixedValueInterpolationCandidates_twoQuantityRelationsNonmatchingFixedValues()
  {
    givenQuantityRelationsContainRe100000QuantityRelation();
    givenQuantityRelationsContainRe200000QuantityRelation();
    givenDaggerboardHasRe150000();

    Map<PhysicalQuantityValues, QuantityRelation> result
        = quantityRelationsCalculator.getFixedValueInterpolationCandidates(daggerboardOrKeel);

    assertThat(result).hasSize(2);
    Iterator<PhysicalQuantityValues> keyIterator = result.keySet().iterator();

    PhysicalQuantityValues key = keyIterator.next();
    assertThat(key.getAsList()).hasSize(1);
    assertThat(key.getAsList()).containsOnly(
        new SimplePhysicalQuantityValue(PhysicalQuantity.REYNOLDS_NUMBER, 100000d));

    QuantityRelation quantityRelation = result.get(key);
    assertThat(quantityRelation).isSameAs(reynolds100000QuantityRelation);

    key = keyIterator.next();
    assertThat(key.getAsList()).hasSize(1);
    assertThat(key.getAsList()).containsOnly(
        new SimplePhysicalQuantityValue(PhysicalQuantity.REYNOLDS_NUMBER, 200000d));

    quantityRelation = result.get(key);
    assertThat(quantityRelation).isSameAs(reynolds200000QuantityRelation);
  }

  @Test
  public void setValuesFromQuantityRelation_keyIsTrial()
  {
    givenQuantityRelationsContainRe200000QuantityRelation();
    givenDaggerboardHasRe200000();
    givenAngleOfAttackIsFiveAsTrialValue();

    quantityRelationsCalculator.setValuesFromQuantityRelation(
        daggerboardOrKeel,
        reynolds200000QuantityRelation);

    CalculatedPhysicalQuantityValue profileDragCoefficient
        = daggerboardOrKeel.getCalculatedValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    assertThat(profileDragCoefficient.getPhysicalQuantity())
        .isEqualTo(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    assertThat(profileDragCoefficient.getValue()).isCloseTo((0.05d + 0.1d)/2, offset(0.00001));
    assertThat(profileDragCoefficient.isTrial()).isTrue();
    assertThat(profileDragCoefficient.getCalculatedBy()).isEqualTo("Calculated polar for: XY 123");
    assertThat(profileDragCoefficient.getCalculatedFrom().getAsList()).containsOnly(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.NCRIT, 9d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.REYNOLDS_NUMBER, 200000d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, 5d), DaggerboardOrKeel.ID));
  }

  @Test
  public void setValuesFromQuantityRelation_fixedValueIsTrial()
  {
    givenQuantityRelationsContainRe200000QuantityRelation();
    givenDaggerboardHasRe150000AsTrialValue();
    givenAngleOfAttackIsFive();

    quantityRelationsCalculator.setValuesFromQuantityRelation(
        daggerboardOrKeel,
        reynolds200000QuantityRelation);

    CalculatedPhysicalQuantityValue profileDragCoefficient
        = daggerboardOrKeel.getCalculatedValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    assertThat(profileDragCoefficient.getPhysicalQuantity())
        .isEqualTo(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
    assertThat(profileDragCoefficient.getValue()).isCloseTo((0.05d + 0.1d)/2, offset(0.00001));
    assertThat(profileDragCoefficient.isTrial()).isTrue();
    assertThat(profileDragCoefficient.getCalculatedBy()).isEqualTo("Calculated polar for: XY 123");
    assertThat(profileDragCoefficient.getCalculatedFrom().getAsList()).containsOnly(
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.NCRIT, 9d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.REYNOLDS_NUMBER, 150000d), DaggerboardOrKeel.ID),
        new SimplePhysicalQuantityValueWithSetId(
            new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, 5d), DaggerboardOrKeel.ID));
  }


  private void givenQuantityRelationsContainRe100000QuantityRelation()
  {
    daggerboardOrKeel.getQuantityRelations().add(reynolds100000QuantityRelation);
  }

  private void givenQuantityRelationsContainRe200000QuantityRelation()
  {
    daggerboardOrKeel.getQuantityRelations().add(reynolds200000QuantityRelation);
  }

  private void givenDaggerboardHasRe150000()
  {
    daggerboardOrKeel.setStartValueNoOverwrite(PhysicalQuantity.REYNOLDS_NUMBER, 150000d);
  }

  private void givenDaggerboardHasRe150000AsTrialValue()
  {
    daggerboardOrKeel.setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.REYNOLDS_NUMBER, 150000d), "me", true);
  }

  private void givenDaggerboardHasRe200000()
  {
    daggerboardOrKeel.setStartValueNoOverwrite(PhysicalQuantity.REYNOLDS_NUMBER, 200000d);
  }

  private void givenDaggerboardHasRe300000()
  {
    daggerboardOrKeel.setStartValueNoOverwrite(PhysicalQuantity.REYNOLDS_NUMBER, 300000d);
  }

  private void givenAngleOfAttackIsZero()
  {
    daggerboardOrKeel.setStartValueNoOverwrite(PhysicalQuantity.ANGLE_OF_ATTACK, 0d);
  }

  private void givenAngleOfAttackIsFive()
  {
    daggerboardOrKeel.setStartValueNoOverwrite(PhysicalQuantity.ANGLE_OF_ATTACK, 5d);
  }

  private void givenAngleOfAttackIsFiveAsTrialValue()
  {
    daggerboardOrKeel.setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, 5d), "me", true);
  }

  private void givenProfileDragCoefficientIsTrialValue()
  {
    daggerboardOrKeel.setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT, 1d), "me", true);
  }

  private void givenProfileDragCoefficientIsTrialValueCloseToZeroPointZeroSevenFive()
  {
    daggerboardOrKeel.setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT, 0.07500001), "me", true);

  }


  private void givenProfileDragCoefficientIsTrialValueCloseToZeroPointOneOneTwoFive()
  {
    daggerboardOrKeel.setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT, 0.11250001), "me", true);

  }

  private void givenLiftCoefficientIsTrialValueCloseToZeroPointFiveFive()
  {
    daggerboardOrKeel.setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.LIFT_COEFFICIENT, 0.5500001), "me", true);

  }

  private void givenLiftCoefficientIsTrialValueCloseToZeroPointFiveTwoFive()
  {
    daggerboardOrKeel.setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.LIFT_COEFFICIENT, 0.5250001), "me", true);
  }

}
