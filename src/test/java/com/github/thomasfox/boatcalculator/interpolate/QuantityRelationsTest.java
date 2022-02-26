package com.github.thomasfox.boatcalculator.interpolate;

import static  org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.value.CalculatedPhysicalQuantityValues;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValues;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.valueset.SimpleValueSet;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

public class QuantityRelationsTest
{
  private QuantityRelation sut;

  @Rule
  public ExpectedException expectedExeption = ExpectedException.none();

  public void setUp()
  {
    sut = QuantityRelation.builder().build();
  }

  @Test
  public void testConstructor_ok()
  {
    // arrange
    PhysicalQuantityValues fixedValues = new PhysicalQuantityValues();
    fixedValues.setValue(PhysicalQuantity.MASS, 20d);

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
    sut = new QuantityRelation("myName", fixedValues, relatedValues, PhysicalQuantity.FORCE);

    // assert
    assertThat(sut.getName()).isEqualTo("myName");

    assertThat(sut.getFixedQuantities().getAsList()).containsOnly(
        new SimplePhysicalQuantityValue(PhysicalQuantity.MASS, 20d));
    assertThat(sut.getFixedQuantities()).isNotSameAs(fixedValues);

    assertThat(sut.getRelatedQuantityValues()).hasSize(2);
    assertThat(sut.getRelatedQuantityValues().get(0).getAsList()).hasSize(2);
    assertThat(sut.getRelatedQuantityValues().get(0).getAsList()).containsOnly(
        new SimplePhysicalQuantityValue(PhysicalQuantity.FORCE, 30d),
        new SimplePhysicalQuantityValue(PhysicalQuantity.BENDING, 10d));
    assertThat(sut.getRelatedQuantityValues().get(1).getAsList()).containsOnly(
        new SimplePhysicalQuantityValue(PhysicalQuantity.FORCE, 60d),
        new SimplePhysicalQuantityValue(PhysicalQuantity.BENDING, 20d));

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
    relatedValuesEntry.setValue(PhysicalQuantity.MASS, 20d);
    relatedValues.add(relatedValuesEntry);

    // assert
    expectedExeption.expect(IllegalArgumentException.class);

    // act
    sut = new QuantityRelation("myName", new PhysicalQuantityValues(), relatedValues, PhysicalQuantity.FORCE);
  }

  @Test
  public void testGetRelatedQuantityValues()
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
    sut = new QuantityRelation("myName", new PhysicalQuantityValues(), relatedValues, PhysicalQuantity.FORCE);

    ValueSet valueSet = new SimpleValueSet("valueSetId", "valueSetName");
    valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(PhysicalQuantity.BENDING, 15d));

    // act
    CalculatedPhysicalQuantityValues result = sut.getRelatedQuantityValues(valueSet);

    // assert
    assertThat(result.getContainedQuantities()).containsOnly(
        PhysicalQuantity.FORCE, PhysicalQuantity.VELOCITY);
    assertThat(result.getValue(PhysicalQuantity.FORCE)).isCloseTo(30d, within(1E-8));
    assertThat(result.getValue(PhysicalQuantity.VELOCITY)).isCloseTo(60d, within(1E-8));
  }

  @Test
  public void testGetRelatedQuantityValues_outsideScope()
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
    sut = new QuantityRelation("myName", new PhysicalQuantityValues(), relatedValues, PhysicalQuantity.FORCE);

    ValueSet valueSet = new SimpleValueSet("valueSetId", "valueSetName");
    valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(PhysicalQuantity.BENDING, 15d));

    // act
    CalculatedPhysicalQuantityValues result = sut.getRelatedQuantityValues(valueSet);

    // assert
    assertThat(result.getContainedQuantities()).isEmpty();
  }

  @Test
  public void testGetRelatedQuantityValues_noValueKnown()
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
    sut = new QuantityRelation("myName", new PhysicalQuantityValues(), relatedValues, PhysicalQuantity.FORCE);

    ValueSet valueSet = new SimpleValueSet("valueSetId", "valueSetName");
    valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, 15d));

    // act
    CalculatedPhysicalQuantityValues result = sut.getRelatedQuantityValues(valueSet);

    // assert
    assertThat(result.getContainedQuantities()).isEmpty();
  }

  @Test
  public void testGetAvailableQuantities()
  {
    // arrange
    List<PhysicalQuantityValues> relatedValues = new ArrayList<>();
    PhysicalQuantityValues relatedValuesEntry = new PhysicalQuantityValues();
    relatedValuesEntry.setValue(PhysicalQuantity.FORCE, 0d);
    relatedValuesEntry.setValue(PhysicalQuantity.BENDING, 0d);
    relatedValuesEntry.setValue(PhysicalQuantity.VELOCITY, 0d);
    relatedValuesEntry.setValue(PhysicalQuantity.ANGLE_OF_ATTACK, 0d);
    relatedValues.add(relatedValuesEntry);
    sut = new QuantityRelation("myName", new PhysicalQuantityValues(), relatedValues, PhysicalQuantity.FORCE);

    ValueSet valueSet = new SimpleValueSet("valueSetId", "valueSetName");
    valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(PhysicalQuantity.BENDING, 15d));
    valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, 0d));
    valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(PhysicalQuantity.FLOW_DIRECTION, 0d));
    valueSet.setCalculatedValue(new SimplePhysicalQuantityValue(PhysicalQuantity.FORCE, 50000d), "me", true);

    // act
    Set<PhysicalQuantity> result = sut.getAvailableQuantities(valueSet, PhysicalQuantity.ANGLE_OF_ATTACK);

    // assert
    assertThat(result).containsOnly(
        PhysicalQuantity.FORCE, PhysicalQuantity.VELOCITY);
  }

  @Test
  public void testGetAvailableQuantities_providedQuantityIsTrial()
  {
    // arrange
    List<PhysicalQuantityValues> relatedValues = new ArrayList<>();
    PhysicalQuantityValues relatedValuesEntry = new PhysicalQuantityValues();
    relatedValuesEntry.setValue(PhysicalQuantity.FORCE, 0d);
    relatedValuesEntry.setValue(PhysicalQuantity.BENDING, 0d);
    relatedValuesEntry.setValue(PhysicalQuantity.VELOCITY, 0d);
    relatedValuesEntry.setValue(PhysicalQuantity.ANGLE_OF_ATTACK, 0d);
    relatedValues.add(relatedValuesEntry);
    sut = new QuantityRelation("myName", new PhysicalQuantityValues(), relatedValues, PhysicalQuantity.FORCE);

    ValueSet valueSet = new SimpleValueSet("valueSetId", "valueSetName");
    valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(PhysicalQuantity.BENDING, 15d));
    valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(PhysicalQuantity.FLOW_DIRECTION, 0d));
    valueSet.setCalculatedValue(new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, 50000d), "me", true);
    valueSet.setCalculatedValue(new SimplePhysicalQuantityValue(PhysicalQuantity.FORCE, 50000d), "me", true);

    // act
    Set<PhysicalQuantity> result = sut.getAvailableQuantities(valueSet, PhysicalQuantity.ANGLE_OF_ATTACK);

    // assert
    assertThat(result).containsOnly(
        PhysicalQuantity.FORCE, PhysicalQuantity.VELOCITY);
  }


  @Test
  public void testGetAvailableQuantities_noKnownValue()
  {
    // arrange
    List<PhysicalQuantityValues> relatedValues = new ArrayList<>();
    PhysicalQuantityValues relatedValuesEntry = new PhysicalQuantityValues();
    relatedValuesEntry.setValue(PhysicalQuantity.FORCE, 0d);
    relatedValuesEntry.setValue(PhysicalQuantity.BENDING, 0d);
    relatedValuesEntry.setValue(PhysicalQuantity.VELOCITY, 0d);
    relatedValuesEntry.setValue(PhysicalQuantity.ANGLE_OF_ATTACK, 0d);
    relatedValues.add(relatedValuesEntry);
    sut = new QuantityRelation("myName", new PhysicalQuantityValues(), relatedValues, PhysicalQuantity.FORCE);

    ValueSet valueSet = new SimpleValueSet("valueSetId", "valueSetName");
    valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(PhysicalQuantity.FLOW_DIRECTION, 0d));

    // act
    Set<PhysicalQuantity> result = sut.getAvailableQuantities(valueSet, PhysicalQuantity.ANGLE_OF_ATTACK);

    // assert
    assertThat(result).containsOnly(
        PhysicalQuantity.FORCE, PhysicalQuantity.BENDING, PhysicalQuantity.VELOCITY);
  }
}
