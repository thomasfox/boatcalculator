package com.github.thomasfox.boatcalculator.calculate.impl;

import org.assertj.core.data.Offset;
import org.junit.Test;

import static  org.assertj.core.api.Assertions.assertThat;

public class AreaLoadFixedMiddleTrapezoidalWingBendingCalculatorTest
{
  @Test
  public void testCalculateFactorForTrapezoidalWing_rectangularWing()
  {
    assertThat(new AreaLoadFixedMiddleTrapezoidalWingBendingCalculator().calculateFactorForTrapezoidalWing(1))
        .isCloseTo(1, Offset.offset(0.001));
  }

  @Test
  public void testCalculateFactorForTrapezoidalWing_triangularWing()
  {
    assertThat(new AreaLoadFixedMiddleTrapezoidalWingBendingCalculator().calculateFactorForTrapezoidalWing(0))
        .isCloseTo(8d/3, Offset.offset(0.001));
  }

  @Test
  public void testCalculateFactorForTrapezoidalWing_halfOuterChord()
  {
    // expect approximate result from WingCalculator
    assertThat(new AreaLoadFixedMiddleTrapezoidalWingBendingCalculator().calculateFactorForTrapezoidalWing(0.5))
        .isCloseTo(1.4d, Offset.offset(0.01));
  }

  @Test
  public void print()
  {
    for (int i=0; i <= 100; i++)
    {
      double x = i / 100d;
      double areaFactor = 0.5d + 0.5d * x;
      double correction = new AreaLoadFixedMiddleTrapezoidalWingBendingCalculator().calculateFactorForTrapezoidalWing(x);
      System.out.println(x + " : " + correction + "(" + correction*areaFactor*areaFactor*areaFactor*areaFactor + ")");
    }
  }
}