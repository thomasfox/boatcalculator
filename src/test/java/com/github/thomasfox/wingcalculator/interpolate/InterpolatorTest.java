package com.github.thomasfox.wingcalculator.interpolate;

import static  org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.LinkedHashMap;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class InterpolatorTest
{
  private final Interpolator sut = new Interpolator();

  @Rule
  public ExpectedException expectedExeption = ExpectedException.none();

  @Test
  public void testInterpolate()
  {
    // arrange
    LinkedHashMap<Double, Double> knownValues = getDefaultKnownValues();

    // act & assert
    double result = sut.interpolate(0.723d, knownValues);
    assertThat(result).isCloseTo(0.723d, within(0.0000000001d));

    result = sut.interpolate(1.5d, knownValues);
    assertThat(result).isCloseTo(2d, within(0.0000000001d));

    result = sut.interpolate(2.6666666666666d, knownValues);
    assertThat(result).isCloseTo(1d, within(0.0000000001d));
  }

  @Test
  public void testInterpolate_lowerBound()
  {
    // arrange
    LinkedHashMap<Double, Double> knownValues = getDefaultKnownValues();

    // act & assert
    double result = sut.interpolate(0d, knownValues);
    assertThat(result).isCloseTo(0d, within(0.0000000001d));
  }

  @Test
  public void testInterpolate_belowLowerBound()
  {
    // arrange
    LinkedHashMap<Double, Double> knownValues = getDefaultKnownValues();

    // assert
    expectedExeption.expect(InterpolatorException.class);
    expectedExeption.expectMessage("is below the interpolation interval which lower bound is");

    // act
    sut.interpolate(-0.000001d, knownValues);
  }

  @Test
  public void testInterpolate_upperBound()
  {
    // arrange
    LinkedHashMap<Double, Double> knownValues = getDefaultKnownValues();

    // act & assert
    double result = sut.interpolate(3d, knownValues);
    assertThat(result).isCloseTo(0d, within(0.0000000001d));
  }

  @Test
  public void testInterpolate_aboveUpperBound()
  {
    // arrange
    LinkedHashMap<Double, Double> knownValues = getDefaultKnownValues();

    // assert
    expectedExeption.expect(InterpolatorException.class);
    expectedExeption.expectMessage("is above the interpolation interval which upper bound is");

    // act
    sut.interpolate(3.000001d, knownValues);
  }

  private LinkedHashMap<Double, Double> getDefaultKnownValues()
  {
    LinkedHashMap<Double, Double> knownValues = new LinkedHashMap<>();
    knownValues.put(0d, 0d);
    knownValues.put(1d, 1d);
    knownValues.put(2d, 3d);
    knownValues.put(3d, 0d);
    return knownValues;
  }
}
