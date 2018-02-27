package com.github.thomasfox.boatcalculator.calculate.value;

import static  org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.calculate.QuantityNotPresentException;
import com.github.thomasfox.boatcalculator.value.CalculatedPhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValues;
import com.github.thomasfox.boatcalculator.valueset.SimpleValueSet;

public class SimpleValueSetTest
{

  private SimpleValueSet sut;

  @Rule
  public ExpectedException expectedExeption = ExpectedException.none();

  @Before
  public void setUp()
  {
    sut = new SimpleValueSet("valueSetId", "valueSetName");
  }

  @Test
  public void testOrderOfGetKnownValue_allValuesSet()
  {
    // arrange
    sut.setFixedValueNoOverwrite(PhysicalQuantity.MASS, 10d);
    sut.setStartValue(PhysicalQuantity.MASS, 20d);
    sut.setCalculatedValue(new PhysicalQuantityValue(PhysicalQuantity.MASS, 30d), "calculatedBy");

    // act
    PhysicalQuantityValue result = sut.getKnownValue(PhysicalQuantity.MASS);

    // assert
    assertThat(result.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.MASS);
    assertThat(result.getValue()).isEqualTo(10d);
  }

  @Test
  public void testOrderOfGetKnownValue_fixedValueNotSet()
  {
    // arrange
    sut.setStartValueNoOverwrite(PhysicalQuantity.MASS, 20d);
    sut.setCalculatedValue(new PhysicalQuantityValue(PhysicalQuantity.MASS, 30d), "calculatedBy");

    // act
    PhysicalQuantityValue result = sut.getKnownValue(PhysicalQuantity.MASS);

    // assert
    assertThat(result.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.MASS);
    assertThat(result.getValue()).isEqualTo(30d);
  }

  @Test
  public void testOrderOfGetKnownValue_onlyStartValueSet()
  {
    // arrange
    sut.setStartValueNoOverwrite(PhysicalQuantity.MASS, 20d);

    // act
    PhysicalQuantityValue result = sut.getKnownValue(PhysicalQuantity.MASS);

    // assert
    assertThat(result.getPhysicalQuantity()).isEqualTo(PhysicalQuantity.MASS);
    assertThat(result.getValue()).isEqualTo(20d);
  }

  @Test
  public void testGetKnownValues_CollectionAsInput()
  {
    // arrange
    sut.setStartValue(PhysicalQuantity.MASS, 10d);

    sut.setCalculatedValue(new PhysicalQuantityValue(PhysicalQuantity.LIFT, 20d), "someLiftCalculation");
    sut.setStartValue(PhysicalQuantity.LIFT, 30d);

    sut.setFixedValueNoOverwrite(PhysicalQuantity.BENDING, 40d);
    sut.setCalculatedValue(new PhysicalQuantityValue(PhysicalQuantity.BENDING, 50d), "someBendingCalculation");
    sut.setStartValue(PhysicalQuantity.BENDING, 60d);

    sut.setStartValue(PhysicalQuantity.FORCE, 70d);

    // act
    PhysicalQuantityValues result
        = sut.getKnownValues(Lists.newArrayList(PhysicalQuantity.MASS, PhysicalQuantity.LIFT, PhysicalQuantity.BENDING));

    // assert
    assertThat(result.getAsList()).containsOnly(
        new PhysicalQuantityValue(PhysicalQuantity.MASS, 10d),
        new PhysicalQuantityValue(PhysicalQuantity.LIFT, 20d),
        new PhysicalQuantityValue(PhysicalQuantity.BENDING, 40d));
  }

  @Test
  public void testGetKnownValuesAsArray_CollectionAsInput_quantityMissing()
  {
    // arrange
    sut.setStartValue(PhysicalQuantity.MASS, 20d);

    // assert
    expectedExeption.expect(QuantityNotPresentException.class);
    expectedExeption.expectMessage("The quantity Durchbiegung is needed but not present");

    // act
    sut.getKnownValues(Lists.newArrayList(PhysicalQuantity.MASS, PhysicalQuantity.BENDING));
  }

  @Test
  public void testGetKnownValuesAsArray()
  {
    // arrange
    sut.setStartValue(PhysicalQuantity.MASS, 10d);

    sut.setCalculatedValue(new PhysicalQuantityValue(PhysicalQuantity.LIFT, 20d), "someLiftCalculation");
    sut.setStartValue(PhysicalQuantity.LIFT, 30d);

    sut.setFixedValueNoOverwrite(PhysicalQuantity.BENDING, 40d);
    sut.setCalculatedValue(new PhysicalQuantityValue(PhysicalQuantity.BENDING, 50d), "someBendingCalculation");
    sut.setStartValue(PhysicalQuantity.BENDING, 60d);

    sut.setStartValue(PhysicalQuantity.FORCE, 70d);

    // act
    PhysicalQuantityValueWithSetId[] result
        = sut.getKnownValuesAsArray(Lists.newArrayList(PhysicalQuantity.MASS, PhysicalQuantity.LIFT, PhysicalQuantity.BENDING));

    // assert
    assertThat(result).containsOnly(
        new PhysicalQuantityValueWithSetId(PhysicalQuantity.MASS, 10d, "valueSetName"),
        new PhysicalQuantityValueWithSetId(PhysicalQuantity.LIFT, 20d, "valueSetName"),
        new PhysicalQuantityValueWithSetId(PhysicalQuantity.BENDING, 40d, "valueSetName"));
  }

  @Test
  public void testGetKnownValuesAsArray_quantityMissing()
  {
    // arrange
    sut.setStartValue(PhysicalQuantity.MASS, 20d);

    // assert
    expectedExeption.expect(QuantityNotPresentException.class);
    expectedExeption.expectMessage("The quantity Durchbiegung is needed but not present");

    // act
    sut.getKnownValuesAsArray(Lists.newArrayList(PhysicalQuantity.MASS, PhysicalQuantity.BENDING));
  }

  @Test
  public void testIsValueKnown_true()
  {
    // arrange
    sut.setStartValue(PhysicalQuantity.MASS, 20d);

    // act
    boolean result = sut.isValueKnown(PhysicalQuantity.MASS);

    // assert
    assertThat(result).isTrue();
  }

  @Test
  public void testIsValueKnown_false()
  {
    // arrange
    sut.setFixedValueNoOverwrite(PhysicalQuantity.LIFT, 20d);
    sut.setStartValue(PhysicalQuantity.LIFT, 20d);
    sut.setCalculatedValue(new PhysicalQuantityValue(PhysicalQuantity.LIFT, 20d), "calculatedBy");

    // act
    boolean result = sut.isValueKnown(PhysicalQuantity.MASS);

    // assert
    assertThat(result).isFalse();
  }

  @Test
  public void testCopyConstructorAndSetters()
  {
    // arrange
    givenAllSutFieldsAreFilled();

    // act
    SimpleValueSet result = new SimpleValueSet(sut);

    // assert equality
    assertThat(result).isEqualToComparingFieldByField(sut);
    // assert setters have worked
    assertThat(result.getId()).isEqualTo("valueSetId");
    assertThat(result.getDisplayName()).isEqualTo("valueSetName");
    assertThat(result.getToInput()).containsOnly(
        PhysicalQuantity.ANGLE_OF_ATTACK, PhysicalQuantity.MASS);
    assertThat(result.getFixedValues().getAsList()).containsOnly(
        new PhysicalQuantityValue(PhysicalQuantity.DENSITY, 1005d),
        new PhysicalQuantityValue(PhysicalQuantity.KINEMATIC_VISCOSITY, 1.2E-6d));
    assertThat(result.getStartValues().getAsList()).containsOnly(
        new PhysicalQuantityValue(PhysicalQuantity.DRIVING_FORCE, 10d),
        new PhysicalQuantityValue(PhysicalQuantity.LATERAL_FORCE, 13d));
    assertThat(result.getCalculatedValues().getAsList()).containsOnly(
        new CalculatedPhysicalQuantityValue(new PhysicalQuantityValue(PhysicalQuantity.LIFT, 20d), "calculatedBy"),
        new CalculatedPhysicalQuantityValue(new PhysicalQuantityValue(PhysicalQuantity.POINTING_ANGLE, 23d), "calculatedBy"));
  }

  @Test
  public void testSetStartValueNoOverwrite()
  {
    // act
    sut.setStartValueNoOverwrite(PhysicalQuantity.MASS, 10d);

    // assert
    assertThat(sut.getStartValue(PhysicalQuantity.MASS)).isEqualTo(new PhysicalQuantityValue(PhysicalQuantity.MASS, 10d));
  }

  @Test
  public void testSetStartValueNoOverwrite_startValueKnown()
  {
    // arrange
    sut.setStartValue(PhysicalQuantity.MASS, 10d);

    // assert
    expectedExeption.expect(IllegalArgumentException.class);

    // act
    sut.setStartValueNoOverwrite(PhysicalQuantity.MASS, 10d);
  }

  @Test
  public void testSetStartValueNoOverwrite_calculatedValueKnown()
  {
    // arrange
    sut.setCalculatedValue(new PhysicalQuantityValue(PhysicalQuantity.MASS, 10d), "calculatedBy");

    // assert
    expectedExeption.expect(IllegalArgumentException.class);

    // act
    sut.setStartValueNoOverwrite(PhysicalQuantity.MASS, 10d);
  }

  @Test
  public void testSetStartValueNoOverwrite_fixedValueKnown()
  {
    // arrange
    sut.setFixedValueNoOverwrite(PhysicalQuantity.MASS, 10d);

    // assert
    expectedExeption.expect(IllegalArgumentException.class);

    // act
    sut.setStartValueNoOverwrite(PhysicalQuantity.MASS, 10d);
  }

  @Test
  public void testSetFixedValueNoOverwrite()
  {
    // act
    sut.setFixedValueNoOverwrite(PhysicalQuantity.MASS, 10d);

    // assert
    assertThat(sut.getFixedValue(PhysicalQuantity.MASS)).isEqualTo(new PhysicalQuantityValue(PhysicalQuantity.MASS, 10d));
  }

  @Test
  public void testSetFixedValueNoOverwrite_startValueKnown()
  {
    // arrange
    sut.setStartValue(PhysicalQuantity.MASS, 10d);

    // assert
    expectedExeption.expect(IllegalArgumentException.class);

    // act
    sut.setFixedValueNoOverwrite(PhysicalQuantity.MASS, 10d);
  }

  @Test
  public void testSetFixedValueNoOverwrite_calculatedValueKnown()
  {
    // arrange
    sut.setCalculatedValue(new PhysicalQuantityValue(PhysicalQuantity.MASS, 10d), "calculatedBy");

    // assert
    expectedExeption.expect(IllegalArgumentException.class);

    // act
    sut.setFixedValueNoOverwrite(PhysicalQuantity.MASS, 10d);
  }

  @Test
  public void testSetFixedValueNoOverwrite_fixedValueKnown()
  {
    // arrange
    sut.setFixedValueNoOverwrite(PhysicalQuantity.MASS, 10d);

    // assert
    expectedExeption.expect(IllegalArgumentException.class);

    // act
    sut.setFixedValueNoOverwrite(PhysicalQuantity.MASS, 10d);
  }

  @Test
  public void testSetCalculatedValueNoOverwrite()
  {
    // act
    sut.setCalculatedValueNoOverwrite(
        new PhysicalQuantityValue(PhysicalQuantity.MASS, 10d),
        "calculatedBy",
        new PhysicalQuantityValueWithSetId(PhysicalQuantity.BENDING, 20d, "setName"));

    // assert
    assertThat(sut.getCalculatedValues().getAsList()).containsOnly(
        new CalculatedPhysicalQuantityValue(
            new PhysicalQuantityValue(PhysicalQuantity.MASS, 10d),
            "calculatedBy",
            new PhysicalQuantityValueWithSetId(PhysicalQuantity.BENDING, 20d, "setName")));
  }

  @Test
  public void testSetCalculatedValueNoOverwrite_startValueKnown()
  {
    // arrange
    sut.setStartValue(PhysicalQuantity.MASS, 10d);

    // assert
    expectedExeption.expect(IllegalArgumentException.class);

    // act
    sut.setCalculatedValueNoOverwrite(new PhysicalQuantityValue(PhysicalQuantity.MASS, 10d), "calculatedBy");
  }

  @Test
  public void testSetCalculatedValueNoOverwrite_calculatedValueKnown()
  {
    // arrange
    sut.setCalculatedValue(new PhysicalQuantityValue(PhysicalQuantity.MASS, 10d), "calculatedBy");

    // assert
    expectedExeption.expect(IllegalArgumentException.class);

    // act
    sut.setCalculatedValueNoOverwrite(new PhysicalQuantityValue(PhysicalQuantity.MASS, 10d), "calculatedBy");
  }

  @Test
  public void testSetCalculatedValueNoOverwrite_fixedValueKnown()
  {
    // arrange
    sut.setFixedValueNoOverwrite(PhysicalQuantity.MASS, 10d);

    // assert
    expectedExeption.expect(IllegalArgumentException.class);

    // act
    sut.setCalculatedValueNoOverwrite(new PhysicalQuantityValue(PhysicalQuantity.MASS, 10d), "calculatedBy");
  }

  @Test
  public void testGetFixedValue()
  {
    // arrange
    sut.setFixedValueNoOverwrite(PhysicalQuantity.MASS, 10d);
    sut.setStartValue(PhysicalQuantity.MASS, 20d);
    sut.setCalculatedValue(new PhysicalQuantityValue(PhysicalQuantity.MASS, 30d), "calculatedBy");

    // act
    PhysicalQuantityValue result = sut.getFixedValue(PhysicalQuantity.MASS);

    // assert
    assertThat(result).isEqualTo(new PhysicalQuantityValue(PhysicalQuantity.MASS, 10d));
  }

  @Test
  public void testGetStartValue()
  {
    // arrange
    sut.setFixedValueNoOverwrite(PhysicalQuantity.MASS, 10d);
    sut.setStartValue(PhysicalQuantity.MASS, 20d);
    sut.setCalculatedValue(new PhysicalQuantityValue(PhysicalQuantity.MASS, 30d), "calculatedBy");

    // act
    PhysicalQuantityValue result = sut.getStartValue(PhysicalQuantity.MASS);

    // assert
    assertThat(result).isEqualTo(new PhysicalQuantityValue(PhysicalQuantity.MASS, 20d));
  }

  @Test
  public void testGetCalculatedValue()
  {
    // arrange
    sut.setFixedValueNoOverwrite(PhysicalQuantity.MASS, 10d);
    sut.setStartValue(PhysicalQuantity.MASS, 20d);
    sut.setCalculatedValue(new PhysicalQuantityValue(PhysicalQuantity.MASS, 30d), "calculatedBy");

    // act
    CalculatedPhysicalQuantityValue result = sut.getCalculatedValue(PhysicalQuantity.MASS);

    // assert
    assertThat(result).isEqualTo(new CalculatedPhysicalQuantityValue(
        new PhysicalQuantityValue(PhysicalQuantity.MASS, 30d), "calculatedBy"));
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
        new CalculatedPhysicalQuantityValue(new PhysicalQuantityValue(PhysicalQuantity.POINTING_ANGLE, 23d), "calculatedBy"));

    // assert rest of object stays the same
    assertThat(sut.getId()).isEqualTo("valueSetId");
    assertThat(sut.getDisplayName()).isEqualTo("valueSetName");
    assertThat(sut.getToInput()).containsOnly(
        PhysicalQuantity.ANGLE_OF_ATTACK, PhysicalQuantity.MASS);
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
    SimpleValueSet expected = new SimpleValueSet(sut);

    // act
    Double result = sut.clearCalculatedValue(PhysicalQuantity.MASS);

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
    assertThat(sut.getId()).isEqualTo("valueSetId");
    assertThat(sut.getDisplayName()).isEqualTo("valueSetName");
    assertThat(sut.getToInput()).containsOnly(
        PhysicalQuantity.ANGLE_OF_ATTACK, PhysicalQuantity.MASS);
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
    assertThat(sut.getId()).isEqualTo("valueSetId");
    assertThat(sut.getDisplayName()).isEqualTo("valueSetName");
    assertThat(sut.getToInput()).containsOnly(
        PhysicalQuantity.ANGLE_OF_ATTACK, PhysicalQuantity.MASS);
    assertThat(sut.getFixedValues().getAsList()).containsOnly(
        new PhysicalQuantityValue(PhysicalQuantity.DENSITY, 1005d),
        new PhysicalQuantityValue(PhysicalQuantity.KINEMATIC_VISCOSITY, 1.2E-6d));
    assertThat(sut.getCalculatedValues().getAsList()).containsOnly(
        new CalculatedPhysicalQuantityValue(new PhysicalQuantityValue(PhysicalQuantity.LIFT, 20d), "calculatedBy"),
        new CalculatedPhysicalQuantityValue(new PhysicalQuantityValue(PhysicalQuantity.POINTING_ANGLE, 23d), "calculatedBy"));
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
    assertThat(sut.getId()).isEqualTo("valueSetId");
    assertThat(sut.getDisplayName()).isEqualTo("valueSetName");
    assertThat(sut.getToInput()).containsOnly(
        PhysicalQuantity.ANGLE_OF_ATTACK, PhysicalQuantity.MASS);
    assertThat(sut.getFixedValues().getAsList()).containsOnly(
        new PhysicalQuantityValue(PhysicalQuantity.DENSITY, 1005d),
        new PhysicalQuantityValue(PhysicalQuantity.KINEMATIC_VISCOSITY, 1.2E-6d));
  }

  private void givenAllSutFieldsAreFilled()
  {
    sut.getToInput().add(PhysicalQuantity.ANGLE_OF_ATTACK);
    sut.addToInput(PhysicalQuantity.MASS);
    sut.setFixedValueNoOverwrite(PhysicalQuantity.DENSITY, 1005d);
    sut.setFixedValueNoOverwrite(new PhysicalQuantityValue(PhysicalQuantity.KINEMATIC_VISCOSITY, 1.2E-6d));
    sut.setStartValue(PhysicalQuantity.DRIVING_FORCE, 10d);
    sut.setStartValueNoOverwrite(PhysicalQuantity.LATERAL_FORCE, 13d);
    sut.setCalculatedValue(new PhysicalQuantityValue(PhysicalQuantity.LIFT, 20d), "calculatedBy");
    sut.setCalculatedValueNoOverwrite(new PhysicalQuantityValue(PhysicalQuantity.POINTING_ANGLE, 23d), "calculatedBy");
  }
}
