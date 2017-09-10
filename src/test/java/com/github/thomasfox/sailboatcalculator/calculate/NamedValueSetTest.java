package com.github.thomasfox.sailboatcalculator.calculate;

import static  org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class NamedValueSetTest
{

  private NamedValueSet sut;

  @Rule
  public ExpectedException expectedExeption = ExpectedException.none();

  @Before
  public void setUp()
  {
    sut = new NamedValueSet("namedValueSetId", "namedValueSetName");
  }

  @Test
  public void testOrderOfGetKnownValue_allValuesSet()
  {
    // arrange
    sut.setFixedValueNoOverwrite(PhysicalQuantity.WEIGHT, 10d);
    sut.setStartValue(PhysicalQuantity.WEIGHT, 20d);
    sut.setCalculatedValue(PhysicalQuantity.WEIGHT, 30d);

    // act
    PhysicalQuantityValue result = sut.getKnownValue(PhysicalQuantity.WEIGHT);

    // assert
    assertThat(result.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.WEIGHT);
    assertThat(result.getValue()).isEqualTo(10d);
  }

  @Test
  public void testOrderOfGetKnownValue_fixedValueNotSet()
  {
    // arrange
    sut.setStartValueNoOverwrite(PhysicalQuantity.WEIGHT, 20d);
    sut.setCalculatedValue(PhysicalQuantity.WEIGHT, 30d);

    // act
    PhysicalQuantityValue result = sut.getKnownValue(PhysicalQuantity.WEIGHT);

    // assert
    assertThat(result.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.WEIGHT);
    assertThat(result.getValue()).isEqualTo(30d);
  }

  @Test
  public void testOrderOfGetKnownValue_onlyStartValueSet()
  {
    // arrange
    sut.setStartValueNoOverwrite(PhysicalQuantity.WEIGHT, 20d);

    // act
    PhysicalQuantityValue result = sut.getKnownValue(PhysicalQuantity.WEIGHT);

    // assert
    assertThat(result.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.WEIGHT);
    assertThat(result.getValue()).isEqualTo(20d);
  }

  @Test
  public void testOrderOfGetKnownValues_allValuesSet()
  {
    // arrange
    sut.setFixedValueNoOverwrite(PhysicalQuantity.WEIGHT, 10d);
    sut.setStartValue(PhysicalQuantity.WEIGHT, 20d);
    sut.setCalculatedValue(PhysicalQuantity.WEIGHT, 30d);

    // act
    PhysicalQuantityValues result = sut.getKnownValues();

    // assert
    assertThat(result.getAsList()).containsOnly(new PhysicalQuantityValue(PhysicalQuantity.WEIGHT, 10d));
  }

  @Test
  public void testOrderOfGetKnownValues_fixedValueNotSet()
  {
    // arrange
    sut.setStartValue(PhysicalQuantity.WEIGHT, 20d);
    sut.setCalculatedValue(PhysicalQuantity.WEIGHT, 30d);

    // act
    PhysicalQuantityValues result = sut.getKnownValues();

    // assert
    assertThat(result.getAsList()).containsOnly(new PhysicalQuantityValue(PhysicalQuantity.WEIGHT, 30d));
  }

  @Test
  public void testOrderOfGetKnownValues_onlyStartValueSet()
  {
    // arrange
    sut.setStartValue(PhysicalQuantity.WEIGHT, 20d);

    // act
    PhysicalQuantityValues result = sut.getKnownValues();

    // assert
    assertThat(result.getAsList()).containsOnly(new PhysicalQuantityValue(PhysicalQuantity.WEIGHT, 20d));
  }

  @Test
  public void testIsValueKnown_true()
  {
    // arrange
    sut.setStartValue(PhysicalQuantity.WEIGHT, 20d);

    // act
    boolean result = sut.isValueKnown(PhysicalQuantity.WEIGHT);

    // assert
    assertThat(result).isTrue();
  }

  @Test
  public void testIsValueKnown_false()
  {
    // arrange
    sut.setFixedValueNoOverwrite(PhysicalQuantity.LIFT, 20d);
    sut.setStartValue(PhysicalQuantity.LIFT, 20d);
    sut.setCalculatedValue(PhysicalQuantity.LIFT, 20d);

    // act
    boolean result = sut.isValueKnown(PhysicalQuantity.WEIGHT);

    // assert
    assertThat(result).isFalse();
  }

  @Test
  public void testCopyConstructorAndSetters()
  {
    // arrange
    givenAllSutFieldsAreFilled();

    // act
    NamedValueSet result = new NamedValueSet(sut);

    // assert equality
    assertThat(result).isEqualToComparingFieldByField(sut);
    // assert setters have worked
    assertThat(result.getId()).isEqualTo("namedValueSetId");
    assertThat(result.getName()).isEqualTo("namedValueSetName");
    assertThat(result.getToInput()).containsOnly(
        PhysicalQuantity.ANGLE_OF_ATTACK, PhysicalQuantity.WEIGHT);
    assertThat(result.getFixedValues().getAsList()).containsOnly(
        new PhysicalQuantityValue(PhysicalQuantity.DENSITY, 1005d),
        new PhysicalQuantityValue(PhysicalQuantity.KINEMATIC_VISCOSITY, 1.2E-6d));
    assertThat(result.getStartValues().getAsList()).containsOnly(
        new PhysicalQuantityValue(PhysicalQuantity.DRIVING_FORCE, 10d),
        new PhysicalQuantityValue(PhysicalQuantity.LATERAL_FORCE, 13d));
    assertThat(result.getCalculatedValues().getAsList()).containsOnly(
        new PhysicalQuantityValue(PhysicalQuantity.LIFT, 20d),
        new PhysicalQuantityValue(PhysicalQuantity.POINTING_ANGLE, 23d));
  }

  @Test
  public void testSetStartValueNoOverwrite_startValueKnown()
  {
    // arrange
    sut.setStartValue(PhysicalQuantity.WEIGHT, 10d);

    // assert
    expectedExeption.expect(IllegalArgumentException.class);

    // act
    sut.setStartValueNoOverwrite(PhysicalQuantity.WEIGHT, 10d);
  }

  @Test
  public void testSetStartValueNoOverwrite_calculatedValueKnown()
  {
    // arrange
    sut.setCalculatedValue(PhysicalQuantity.WEIGHT, 10d);

    // assert
    expectedExeption.expect(IllegalArgumentException.class);

    // act
    sut.setStartValueNoOverwrite(PhysicalQuantity.WEIGHT, 10d);
  }

  @Test
  public void testSetStartValueNoOverwrite_fixedValueKnown()
  {
    // arrange
    sut.setFixedValueNoOverwrite(PhysicalQuantity.WEIGHT, 10d);

    // assert
    expectedExeption.expect(IllegalArgumentException.class);

    // act
    sut.setStartValueNoOverwrite(PhysicalQuantity.WEIGHT, 10d);
  }

  @Test
  public void testSetFixedValueNoOverwrite_startValueKnown()
  {
    // arrange
    sut.setStartValue(PhysicalQuantity.WEIGHT, 10d);

    // assert
    expectedExeption.expect(IllegalArgumentException.class);

    // act
    sut.setFixedValueNoOverwrite(PhysicalQuantity.WEIGHT, 10d);
  }

  @Test
  public void testSetFixedValueNoOverwrite_calculatedValueKnown()
  {
    // arrange
    sut.setCalculatedValue(PhysicalQuantity.WEIGHT, 10d);

    // assert
    expectedExeption.expect(IllegalArgumentException.class);

    // act
    sut.setFixedValueNoOverwrite(PhysicalQuantity.WEIGHT, 10d);
  }

  @Test
  public void testSetFixedValueNoOverwrite_fixedValueKnown()
  {
    // arrange
    sut.setFixedValueNoOverwrite(PhysicalQuantity.WEIGHT, 10d);

    // assert
    expectedExeption.expect(IllegalArgumentException.class);

    // act
    sut.setFixedValueNoOverwrite(PhysicalQuantity.WEIGHT, 10d);
  }

  @Test
  public void testSeCalculatedValueNoOverwrite_startValueKnown()
  {
    // arrange
    sut.setStartValue(PhysicalQuantity.WEIGHT, 10d);

    // assert
    expectedExeption.expect(IllegalArgumentException.class);

    // act
    sut.setCalculatedValueNoOverwrite(PhysicalQuantity.WEIGHT, 10d);
  }

  @Test
  public void testSetCalculatedValueNoOverwrite_calculatedValueKnown()
  {
    // arrange
    sut.setCalculatedValue(PhysicalQuantity.WEIGHT, 10d);

    // assert
    expectedExeption.expect(IllegalArgumentException.class);

    // act
    sut.setCalculatedValueNoOverwrite(PhysicalQuantity.WEIGHT, 10d);
  }

  @Test
  public void testSetCalculatedValueNoOverwrite_fixedValueKnown()
  {
    // arrange
    sut.setFixedValueNoOverwrite(PhysicalQuantity.WEIGHT, 10d);

    // assert
    expectedExeption.expect(IllegalArgumentException.class);

    // act
    sut.setCalculatedValueNoOverwrite(PhysicalQuantity.WEIGHT, 10d);
  }

  @Test
  public void testGetFixedValue()
  {
    // arrange
    sut.setFixedValueNoOverwrite(PhysicalQuantity.WEIGHT, 10d);
    sut.setStartValue(PhysicalQuantity.WEIGHT, 20d);
    sut.setCalculatedValue(PhysicalQuantity.WEIGHT, 30d);

    // act
    double result = sut.getFixedValue(PhysicalQuantity.WEIGHT);

    // assert
    assertThat(result).isEqualTo(10d);
  }

  @Test
  public void testGetStartValue()
  {
    // arrange
    sut.setFixedValueNoOverwrite(PhysicalQuantity.WEIGHT, 10d);
    sut.setStartValue(PhysicalQuantity.WEIGHT, 20d);
    sut.setCalculatedValue(PhysicalQuantity.WEIGHT, 30d);

    // act
    double result = sut.getStartValue(PhysicalQuantity.WEIGHT);

    // assert
    assertThat(result).isEqualTo(20d);
  }

  @Test
  public void testGetCalculatedValue()
  {
    // arrange
    sut.setFixedValueNoOverwrite(PhysicalQuantity.WEIGHT, 10d);
    sut.setStartValue(PhysicalQuantity.WEIGHT, 20d);
    sut.setCalculatedValue(PhysicalQuantity.WEIGHT, 30d);

    // act
    double result = sut.getCalculatedValue(PhysicalQuantity.WEIGHT);

    // assert
    assertThat(result).isEqualTo(30d);
  }


  @Test
  public void testClearCalculatedValue_valueExists()
  {
    // arrange
    givenAllSutFieldsAreFilled();

    // act
    Double result = sut.clearCalculatedValue(PhysicalQuantity.LIFT);

    // assert returned value is equal to existing value
    assertThat(result).isEqualTo(20d);
    // assert calculated value is removed
    assertThat(sut.getCalculatedValues().getAsList()).containsOnly(
        new PhysicalQuantityValue(PhysicalQuantity.POINTING_ANGLE, 23d));

    // assert rest of object stays the same
    assertThat(sut.getId()).isEqualTo("namedValueSetId");
    assertThat(sut.getName()).isEqualTo("namedValueSetName");
    assertThat(sut.getToInput()).containsOnly(
        PhysicalQuantity.ANGLE_OF_ATTACK, PhysicalQuantity.WEIGHT);
    assertThat(sut.getFixedValues().getAsList()).containsOnly(
        new PhysicalQuantityValue(PhysicalQuantity.DENSITY, 1005d),
        new PhysicalQuantityValue(PhysicalQuantity.KINEMATIC_VISCOSITY, 1.2E-6d));
    assertThat(sut.getStartValues().getAsList()).containsOnly(
        new PhysicalQuantityValue(PhysicalQuantity.DRIVING_FORCE, 10d),
        new PhysicalQuantityValue(PhysicalQuantity.LATERAL_FORCE, 13d));
  }

  @Test
  public void testClearCalculatedValue_valueDoesNotExist()
  {
    // arrange
    givenAllSutFieldsAreFilled();
    NamedValueSet expected = new NamedValueSet(sut);

    // act
    Double result = sut.clearCalculatedValue(PhysicalQuantity.WEIGHT);

    // assert returned value is equal to existing value (i.e. null)
    assertThat(result).isNull();
    // assert Object stays the same
    assertThat(sut).isEqualToComparingFieldByField(expected);
  }

  @Test
  public void testClearCalculatedValues()
  {
    // arrange
    givenAllSutFieldsAreFilled();

    // act
    sut.clearCalculatedValues();

    // assert calculated values are removed
    assertThat(sut.getCalculatedValues().getAsList()).containsOnly();

    // assert rest of object stays the same
    assertThat(sut.getId()).isEqualTo("namedValueSetId");
    assertThat(sut.getName()).isEqualTo("namedValueSetName");
    assertThat(sut.getToInput()).containsOnly(
        PhysicalQuantity.ANGLE_OF_ATTACK, PhysicalQuantity.WEIGHT);
    assertThat(sut.getFixedValues().getAsList()).containsOnly(
        new PhysicalQuantityValue(PhysicalQuantity.DENSITY, 1005d),
        new PhysicalQuantityValue(PhysicalQuantity.KINEMATIC_VISCOSITY, 1.2E-6d));
    assertThat(sut.getStartValues().getAsList()).containsOnly(
        new PhysicalQuantityValue(PhysicalQuantity.DRIVING_FORCE, 10d),
        new PhysicalQuantityValue(PhysicalQuantity.LATERAL_FORCE, 13d));
  }

  @Test
  public void testClearStartValues()
  {
    // arrange
    givenAllSutFieldsAreFilled();

    // act
    sut.clearStartValues();

    // assert calculated values are removed
    assertThat(sut.getStartValues().getAsList()).containsOnly();

    // assert rest of object stays the same
    assertThat(sut.getId()).isEqualTo("namedValueSetId");
    assertThat(sut.getName()).isEqualTo("namedValueSetName");
    assertThat(sut.getToInput()).containsOnly(
        PhysicalQuantity.ANGLE_OF_ATTACK, PhysicalQuantity.WEIGHT);
    assertThat(sut.getFixedValues().getAsList()).containsOnly(
        new PhysicalQuantityValue(PhysicalQuantity.DENSITY, 1005d),
        new PhysicalQuantityValue(PhysicalQuantity.KINEMATIC_VISCOSITY, 1.2E-6d));
    assertThat(sut.getCalculatedValues().getAsList()).containsOnly(
        new PhysicalQuantityValue(PhysicalQuantity.LIFT, 20d),
        new PhysicalQuantityValue(PhysicalQuantity.POINTING_ANGLE, 23d));
  }

  @Test
  public void testMoveCalculatedValuesToStartValues()
  {
    // arrange
    givenAllSutFieldsAreFilled();

    // act
    sut.moveCalculatedValuesToStartValues();

    // assert start and calculated values have expected content
    assertThat(sut.getStartValues().getAsList()).containsOnly(
        new PhysicalQuantityValue(PhysicalQuantity.DRIVING_FORCE, 10d),
        new PhysicalQuantityValue(PhysicalQuantity.LATERAL_FORCE, 13d),
        new PhysicalQuantityValue(PhysicalQuantity.LIFT, 20d),
        new PhysicalQuantityValue(PhysicalQuantity.POINTING_ANGLE, 23d));
    assertThat(sut.getCalculatedValues().getAsList()).containsOnly();

    // assert rest of object stays the same
    assertThat(sut.getId()).isEqualTo("namedValueSetId");
    assertThat(sut.getName()).isEqualTo("namedValueSetName");
    assertThat(sut.getToInput()).containsOnly(
        PhysicalQuantity.ANGLE_OF_ATTACK, PhysicalQuantity.WEIGHT);
    assertThat(sut.getFixedValues().getAsList()).containsOnly(
        new PhysicalQuantityValue(PhysicalQuantity.DENSITY, 1005d),
        new PhysicalQuantityValue(PhysicalQuantity.KINEMATIC_VISCOSITY, 1.2E-6d));
  }

  private void givenAllSutFieldsAreFilled()
  {
    sut.getToInput().add(PhysicalQuantity.ANGLE_OF_ATTACK);
    sut.addToInput(PhysicalQuantity.WEIGHT);
    sut.setFixedValueNoOverwrite(PhysicalQuantity.DENSITY, 1005d);
    sut.setFixedValueNoOverwrite(new PhysicalQuantityValue(PhysicalQuantity.KINEMATIC_VISCOSITY, 1.2E-6d));
    sut.setStartValue(PhysicalQuantity.DRIVING_FORCE, 10d);
    sut.setStartValueNoOverwrite(PhysicalQuantity.LATERAL_FORCE, 13d);
    sut.setCalculatedValue(PhysicalQuantity.LIFT, 20d);
    sut.setCalculatedValueNoOverwrite(PhysicalQuantity.POINTING_ANGLE, 23d);
  }
}
