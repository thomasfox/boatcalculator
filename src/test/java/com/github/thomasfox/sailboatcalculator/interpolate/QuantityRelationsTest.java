package com.github.thomasfox.sailboatcalculator.interpolate;

import static  org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValues;

public class QuantityRelationsTest
{
  private QuantityRelations sut;

  @Rule
  public ExpectedException expectedExeption = ExpectedException.none();

  public void setUp()
  {
    sut = QuantityRelations.builder().build();
  }

  @Test
  public void testConstructor_ok()
  {
    // arrange
    PhysicalQuantityValues fixedValues = new PhysicalQuantityValues();
    fixedValues.setValue(PhysicalQuantity.WEIGHT, 20d);

    List<PhysicalQuantityValues> relatedValues = new ArrayList<>();
    PhysicalQuantityValues relatedValuesEntry = new PhysicalQuantityValues();
    relatedValuesEntry.setValue(PhysicalQuantity.FORCE, 30d);
    relatedValuesEntry.setValue(PhysicalQuantity.BENDING, 10d);
    relatedValues.add(relatedValuesEntry);
    relatedValuesEntry = new PhysicalQuantityValues();
    relatedValuesEntry.setValue(PhysicalQuantity.FORCE, 60d);
    relatedValuesEntry.setValue(PhysicalQuantity.BENDING, 20d);
    relatedValues.add(relatedValuesEntry);

    // act
    sut = new QuantityRelations("myName", fixedValues, relatedValues, PhysicalQuantity.FORCE);

    // assert
    assertThat(sut.getName()).isEqualTo("myName");

    assertThat(sut.getFixedQuantities().getAsList()).containsOnly(new PhysicalQuantityValue(PhysicalQuantity.WEIGHT, 20d));
    assertThat(sut.getFixedQuantities()).isNotSameAs(fixedValues);

    assertThat(sut.getRelatedQuantityValues()).hasSize(2);
    assertThat(sut.getRelatedQuantityValues().get(0).getAsList()).hasSize(2);
    assertThat(sut.getRelatedQuantityValues().get(0).getAsList()).containsOnly(
        new PhysicalQuantityValue(PhysicalQuantity.FORCE, 30d),
        new PhysicalQuantityValue(PhysicalQuantity.BENDING, 10d));
    assertThat(sut.getRelatedQuantityValues().get(1).getAsList()).containsOnly(
        new PhysicalQuantityValue(PhysicalQuantity.FORCE, 60d),
        new PhysicalQuantityValue(PhysicalQuantity.BENDING, 20d));

    assertThat(sut.getKeyQuantity()).isEqualTo(PhysicalQuantity.FORCE);
  }

  @Test
  public void testConstructor_differentQuantitiesInLines()
  {
    // arrange
    List<PhysicalQuantityValues> relatedValues = new ArrayList<>();
    PhysicalQuantityValues relatedValuesEntry = new PhysicalQuantityValues();
    relatedValuesEntry.setValue(PhysicalQuantity.FORCE, 30d);
    relatedValuesEntry.setValue(PhysicalQuantity.BENDING, 10d);
    relatedValues.add(relatedValuesEntry);
    relatedValuesEntry = new PhysicalQuantityValues();
    relatedValuesEntry.setValue(PhysicalQuantity.FORCE, 60d);
    relatedValuesEntry.setValue(PhysicalQuantity.WEIGHT, 20d);
    relatedValues.add(relatedValuesEntry);

    // assert
    expectedExeption.expect(IllegalArgumentException.class);

    // act
    sut = new QuantityRelations("myName", new PhysicalQuantityValues(), relatedValues, PhysicalQuantity.FORCE);
  }

  @Test
  public void testGetRelatedQuantityValues_ok()
  {
    // arrange
    List<PhysicalQuantityValues> relatedValues = new ArrayList<>();
    PhysicalQuantityValues relatedValuesEntry = new PhysicalQuantityValues();
    relatedValuesEntry.setValue(PhysicalQuantity.FORCE, 0d);
    relatedValuesEntry.setValue(PhysicalQuantity.BENDING, 0d);
    relatedValuesEntry.setValue(PhysicalQuantity.VELOCITY, 0d);
    relatedValues.add(relatedValuesEntry);
    relatedValuesEntry = new PhysicalQuantityValues();
    relatedValuesEntry.setValue(PhysicalQuantity.FORCE, 20d);
    relatedValuesEntry.setValue(PhysicalQuantity.BENDING, 10d);
    relatedValuesEntry.setValue(PhysicalQuantity.VELOCITY, 40d);
    relatedValues.add(relatedValuesEntry);
    relatedValuesEntry = new PhysicalQuantityValues();
    relatedValuesEntry.setValue(PhysicalQuantity.FORCE, 40d);
    relatedValuesEntry.setValue(PhysicalQuantity.BENDING, 20d);
    relatedValuesEntry.setValue(PhysicalQuantity.VELOCITY, 80d);
    relatedValues.add(relatedValuesEntry);
    sut = new QuantityRelations("myName", new PhysicalQuantityValues(), relatedValues, PhysicalQuantity.FORCE);

    PhysicalQuantityValues knownValues = new PhysicalQuantityValues();
    knownValues.setValue(PhysicalQuantity.BENDING, 15d);

    // act
    PhysicalQuantityValues result = sut.getRelatedQuantityValues(knownValues);

    // assert
    assertThat(result.getContainedQuantities()).containsOnly(
        PhysicalQuantity.FORCE, PhysicalQuantity.VELOCITY);
    assertThat(result.getValue(PhysicalQuantity.FORCE)).isCloseTo(30d, within(1E-8));
    assertThat(result.getValue(PhysicalQuantity.VELOCITY)).isCloseTo(60d, within(1E-8));
  }
}
