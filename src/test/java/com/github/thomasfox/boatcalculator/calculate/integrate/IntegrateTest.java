package com.github.thomasfox.boatcalculator.calculate.integrate;

import org.assertj.core.data.Offset;
import org.junit.Test;

import static  org.assertj.core.api.Assertions.assertThat;

public class IntegrateTest
{
  @Test
  public void integrate_constant()
  {
    Integrate integrate = new Integrate()
    {
      @Override
      public double y(double x)
      {
        return 1;
      }
    };
    assertThat(integrate.integrate(0, 2)).isCloseTo(2, Offset.offset(0.0001));
    assertThat(integrate.integrate(-0.1, 0)).isCloseTo(0.1, Offset.offset(0.0001));
  }

  @Test
  public void integrate_quadratic()
  {
    Integrate integrate = new Integrate()
    {
      @Override
      public double y(double x)
      {
        return x * x;
      }
    };
    assertThat(integrate.integrate(0, 2)).isCloseTo(8d/3, Offset.offset(0.0001));
    assertThat(integrate.integrate(-1, 1)).isCloseTo(2d/3, Offset.offset(0.0001));
  }
}