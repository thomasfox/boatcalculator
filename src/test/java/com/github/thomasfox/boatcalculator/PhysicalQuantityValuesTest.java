package com.github.thomasfox.boatcalculator;

import static  org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValues;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;

public class PhysicalQuantityValuesTest
{
  private PhysicalQuantityValues sut;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp()
  {
    sut = new PhysicalQuantityValues();
  }

  @Test
  public void testSetValueNoOverwrite_ok()
  {
    // act
    sut.setValueNoOverwrite(PhysicalQuantity.MASS, 20d);

    // assert
    assertThat(sut.getAsList()).containsExactly(new SimplePhysicalQuantityValue(PhysicalQuantity.MASS, 20d));
  }

  @Test
  public void testSetValueNoOverwrite_fail()
  {
    // arrange
    sut.setValueNoOverwrite(PhysicalQuantity.MASS, 20d);

    // assert
    exception.expect(Exception.class);

    // act
    sut.setValueNoOverwrite(PhysicalQuantity.MASS, 20d);
  }

  @Test
  public void testSetValueNoOverwriteWithObject_ok()
  {
    // act
    sut.setValueNoOverwrite(new SimplePhysicalQuantityValue(PhysicalQuantity.MASS, 20d));

    // assert
    assertThat(sut.getAsList()).containsExactly(new SimplePhysicalQuantityValue(PhysicalQuantity.MASS, 20d));
  }

  @Test
  public void testSetValueNoOverwriteWithObject_fail()
  {
    // arrange
    sut.setValueNoOverwrite(PhysicalQuantity.MASS, 20d);

    // assert
    exception.expect(Exception.class);

    // act
    sut.setValueNoOverwrite(new SimplePhysicalQuantityValue(PhysicalQuantity.MASS, 20d));
  }

  @Test
  public void testGetContainedQuantities()
  {
    // arrange
    sut.setValueNoOverwrite(PhysicalQuantity.MASS, 20d);
    sut.setValueNoOverwrite(PhysicalQuantity.FORCE, -50d);

    // act
    Set<PhysicalQuantity> containedQuantities = sut.getContainedQuantities();

    // assert
    assertThat(containedQuantities).containsOnly(PhysicalQuantity.MASS, PhysicalQuantity.FORCE);
  }

  @Test
  public void testGetValue_containedQuantity()
  {
    // arrange
    sut.setValueNoOverwrite(PhysicalQuantity.MASS, 20d);
    sut.setValueNoOverwrite(PhysicalQuantity.FORCE, -50d);

    // act
    Double weight = sut.getValue(PhysicalQuantity.MASS);

    // assert
    assertThat(weight).isEqualTo(20d);
  }

  @Test
  public void testGetValue_unknownQuantity()
  {
    // arrange
    sut.setValueNoOverwrite(PhysicalQuantity.MASS, 20d);
    sut.setValueNoOverwrite(PhysicalQuantity.FORCE, -50d);

    // act
    Double bending = sut.getValue(PhysicalQuantity.BENDING);

    // assert
    assertThat(bending).isNull();
  }
}
