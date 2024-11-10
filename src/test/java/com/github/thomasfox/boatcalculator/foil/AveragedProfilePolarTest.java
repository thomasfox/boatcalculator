package com.github.thomasfox.boatcalculator.foil;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.interpolate.QuantityRelation;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValues;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;
import org.assertj.core.data.Offset;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AveragedProfilePolarTest
{
  @Test
  public void testAverage_samePolarForAllReynoldsNumbers()
  {
    AveragedProfilePolar sut = new AveragedProfilePolar(createQuantityRelations(
        -1,
        1.001,
        0.2,
        0.02d),
        6d);
    ModifiableWingParameters modifiableWingParameters = new ModifiableWingParameters();
    HalfFoilGeometry halfFoilGeometry = TrapezoidalHalfFoilGeometry.fromModifiableWingParameters(
        modifiableWingParameters);
    QuantityRelation quantityRelation = sut.average(halfFoilGeometry);

    PhysicalQuantityValues expectedFixedValues = new PhysicalQuantityValues();
    expectedFixedValues.setValue(PhysicalQuantity.NCRIT, 9);
    assertThat(quantityRelation.getFixedQuantities()).isEqualToComparingFieldByField(expectedFixedValues);
    List<PhysicalQuantityValues> relatedQuantityValues = quantityRelation.getRelatedQuantityValues();
    assertThat(relatedQuantityValues).hasSize(5);
    double angleOfAttack = -1d;
    for (PhysicalQuantityValues relatedQuantityValue : relatedQuantityValues) {
      assertThat(relatedQuantityValue.getContainedQuantities()).containsOnly(
          PhysicalQuantity.ANGLE_OF_ATTACK,
          PhysicalQuantity.LIFT_COEFFICIENT,
          PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
      assertThat(relatedQuantityValue.getValue(PhysicalQuantity.ANGLE_OF_ATTACK))
          .isCloseTo(angleOfAttack, Offset.offset(0.0000001d));
      double liftCoefficient = angleOfAttack * 0.2;
      assertThat(relatedQuantityValue.getValue(PhysicalQuantity.LIFT_COEFFICIENT))
          .isCloseTo(liftCoefficient, Offset.offset(0.0000001d));
      double profileDragCoefficient = Math.abs(angleOfAttack) * 0.02 + 0.01d;
      assertThat(relatedQuantityValue.getValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT))
          .isCloseTo(profileDragCoefficient, Offset.offset(0.0000001d));
      angleOfAttack += 0.5;
    }
  }

  @Test
  public void testAverage_differentMinAndMaxAnglesOfAttack()
  {
    AveragedProfilePolar sut = new AveragedProfilePolar(createQuantityRelations(
        -0.5,
        0.5001,
        0.2,
        0.02d),
        6d);
    ModifiableWingParameters modifiableWingParameters = new ModifiableWingParameters();
    HalfFoilGeometry halfFoilGeometry = TrapezoidalHalfFoilGeometry.fromModifiableWingParameters(
        modifiableWingParameters);
    QuantityRelation quantityRelation = sut.average(halfFoilGeometry);

    PhysicalQuantityValues expectedFixedValues = new PhysicalQuantityValues();
    expectedFixedValues.setValue(PhysicalQuantity.NCRIT, 9);
    assertThat(quantityRelation.getFixedQuantities()).isEqualToComparingFieldByField(expectedFixedValues);
    List<PhysicalQuantityValues> relatedQuantityValues = quantityRelation.getRelatedQuantityValues();
    assertThat(relatedQuantityValues).hasSize(3);
    double angleOfAttack = -0.5d;
    for (PhysicalQuantityValues relatedQuantityValue : relatedQuantityValues) {
      assertThat(relatedQuantityValue.getContainedQuantities()).containsOnly(
          PhysicalQuantity.ANGLE_OF_ATTACK,
          PhysicalQuantity.LIFT_COEFFICIENT,
          PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
      assertThat(relatedQuantityValue.getValue(PhysicalQuantity.ANGLE_OF_ATTACK))
          .isCloseTo(angleOfAttack, Offset.offset(0.0000001d));
      double liftCoefficient = angleOfAttack * 0.2;
      assertThat(relatedQuantityValue.getValue(PhysicalQuantity.LIFT_COEFFICIENT))
          .isCloseTo(liftCoefficient, Offset.offset(0.0000001d));
      double profileDragCoefficient = Math.abs(angleOfAttack) * 0.02 + 0.01d;
      assertThat(relatedQuantityValue.getValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT))
          .isCloseTo(profileDragCoefficient, Offset.offset(0.0000001d));
      angleOfAttack += 0.5;
    }
  }

  @Test
  public void testAverage_differentPolars()
  {
    AveragedProfilePolar sut = new AveragedProfilePolar(createQuantityRelations(
        -1,
        1.001,
        0.5,
        0.001),
        6d);
    ModifiableWingParameters modifiableWingParameters = new ModifiableWingParameters();
    HalfFoilGeometry halfFoilGeometry = TrapezoidalHalfFoilGeometry.fromModifiableWingParameters(
        modifiableWingParameters);
    QuantityRelation quantityRelation = sut.average(halfFoilGeometry);

    PhysicalQuantityValues expectedFixedValues = new PhysicalQuantityValues();
    expectedFixedValues.setValue(PhysicalQuantity.NCRIT, 9);
    assertThat(quantityRelation.getFixedQuantities()).isEqualToComparingFieldByField(expectedFixedValues);
    List<PhysicalQuantityValues> relatedQuantityValues = quantityRelation.getRelatedQuantityValues();
    assertThat(relatedQuantityValues).hasSize(5);
    double angleOfAttack = -1d;
    for (PhysicalQuantityValues relatedQuantityValue : relatedQuantityValues) {
      assertThat(relatedQuantityValue.getContainedQuantities()).containsOnly(
          PhysicalQuantity.ANGLE_OF_ATTACK,
          PhysicalQuantity.LIFT_COEFFICIENT,
          PhysicalQuantity.PROFILE_DRAG_COEFFICIENT);
      assertThat(relatedQuantityValue.getValue(PhysicalQuantity.ANGLE_OF_ATTACK))
          .isCloseTo(angleOfAttack, Offset.offset(0.0000001d));
      double liftCoefficient = angleOfAttack * 0.254318d;
      assertThat(relatedQuantityValue.getValue(PhysicalQuantity.LIFT_COEFFICIENT))
          .isCloseTo(liftCoefficient, Offset.offset(0.0000001d));
      double profileDragCoefficient = Math.abs(angleOfAttack) * 0.005237304d + 0.01d;
      assertThat(relatedQuantityValue.getValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT))
          .isCloseTo(profileDragCoefficient, Offset.offset(0.0000001d));
      angleOfAttack += 0.5;
    }
  }

  private List<QuantityRelation> createQuantityRelations(
      double differentMinAngleOfAttack,
      double differentMaxAngleOfAttack,
      double re20000LiftCoefficientFactor,
      double re50000ProfileDragCoefficientFactor)
  {
    List<QuantityRelation> result = new ArrayList<>();
    result.add(createQuantityRelation(200000d, -1d, 1.001d, re20000LiftCoefficientFactor, 0.02d));
    result.add(createQuantityRelation(500000d, differentMinAngleOfAttack, 1.001d, 0.2d, re50000ProfileDragCoefficientFactor));
    result.add(createQuantityRelation(1000000d, -1d, differentMaxAngleOfAttack, 0.2d,0.02d));
    return result;
  }

  private QuantityRelation createQuantityRelation(
      double reynoldsNumber,
      double minAngleOfAttack,
      double maxAngleOfAttack,
      double liftCoefficientFactor,
      double profileDragCoefficientFactor)
  {
    PhysicalQuantityValues fixedValues = new PhysicalQuantityValues();
    fixedValues.setValue(new SimplePhysicalQuantityValue(PhysicalQuantity.NCRIT, 9d));
    fixedValues.setValue(new SimplePhysicalQuantityValue(PhysicalQuantity.REYNOLDS_NUMBER, reynoldsNumber));
    List<PhysicalQuantityValues> relatedValues = new ArrayList<>();
    double angleOfAttack = minAngleOfAttack;
    while (angleOfAttack < maxAngleOfAttack)
    {
      PhysicalQuantityValues dataPoint = new PhysicalQuantityValues();
      dataPoint.setValue(
          PhysicalQuantity.ANGLE_OF_ATTACK,
          angleOfAttack);
      dataPoint.setValue(
          PhysicalQuantity.LIFT_COEFFICIENT,
          angleOfAttack * liftCoefficientFactor);
      dataPoint.setValue(
          PhysicalQuantity.PROFILE_DRAG_COEFFICIENT,
          Math.abs(angleOfAttack) * profileDragCoefficientFactor + 0.01d);
      relatedValues.add(dataPoint);
      angleOfAttack += 0.5;
    }
    QuantityRelation result = new QuantityRelation("polar", fixedValues, relatedValues, null);
    return result;
  }


}