package com.github.thomasfox.boatcalculator.iterate;

import static  org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.Assertions.within;

import java.util.NoSuchElementException;

import org.junit.Test;

import com.github.thomasfox.boatcalculator.iterate.DoubleIntervalIterator;

public class DoubleIntervalIteratorTest
{
  @Test
  public void testIteration()
  {
    DoubleIntervalIterator sut = new DoubleIntervalIterator(0d, 2d, 3);
    assertThat(sut.hasNext()).isTrue();
    double next = sut.next();
    assertThat(next).isCloseTo(0d, within(1e-10d));
    assertThat(sut.hasNext()).isTrue();
    next = sut.next();
    assertThat(next).isCloseTo(1d, within(1e-10d));
    assertThat(sut.hasNext()).isTrue();
    next = sut.next();
    assertThat(next).isCloseTo(2d, within(1e-10d));
    assertThat(sut.hasNext()).isFalse();
    try
    {
      sut.next();
      fail("Exception expected");
    }
    catch (NoSuchElementException e)
    {
      // expected
    }
  }

  @Test
  public void testSingleStep()
  {
    DoubleIntervalIterator sut = new DoubleIntervalIterator(0d, 0d, 1);
    assertThat(sut.hasNext()).isTrue();
    double next = sut.next();
    assertThat(next).isCloseTo(0d, within(1e-10d));
    assertThat(sut.hasNext()).isFalse();
    try
    {
      sut.next();
      fail("Exception expected");
    }
    catch (NoSuchElementException e)
    {
      // expected
    }
  }
}
