package com.github.thomasfox.wingcalculator.calculate;

import static  org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class NamedValueSetTest
{

  private NamedValueSet sut;

  @Before
  public void setUp()
  {
    sut = new NamedValueSet("namedValueSetName");
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
}