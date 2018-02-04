package com.github.thomasfox.sailboatcalculator.iterate;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterates in evenly distributed steps over an interval.
 * The first point <code>scanStart</code>, the last point <code>scanEnd</code>
 * and the number of steps are needed, then the iterator returns evenly spaced
 * values distributed about the given interval,
 * starting with <code>scanStart</code> and the last point being <code>scanEnd</code>.
 */
public class DoubleIntervalIterator implements Iterator<Double>
{
  private final double scanStart;

  private final double scanEnd;

  private final int numberOfScanSteps;

  private int scanStep = 0;

  public DoubleIntervalIterator(double scanStart, double scanEnd, int numberOfScanSteps)
  {
    if (scanStart == scanEnd)
    {
      if (numberOfScanSteps <= 0)
      {
        throw new IllegalArgumentException("NumberOfScanSteps is " + numberOfScanSteps
            + " but must be greater than 0 for scanStart == scanEnd ");
      }
    }
    else
    {
      if (numberOfScanSteps <= 1)
      {
        throw new IllegalArgumentException("NumberOfScanSteps is " + numberOfScanSteps
            + " but must be greater than 1 for scanStart != scanEnd");
      }
    }
    this.scanStart = scanStart;
    this.scanEnd = scanEnd;
    this.numberOfScanSteps = numberOfScanSteps;
  }

  @Override
  public boolean hasNext()
  {
    return scanStep < numberOfScanSteps;
  }

  @Override
  public Double next()
  {
    if (scanStep >= numberOfScanSteps)
    {
      throw new NoSuchElementException();
    }
    if (numberOfScanSteps == 1)
    {
      scanStep++;
      return scanStart;
    }
    return scanStart + (scanEnd - scanStart) * (scanStep++ / (numberOfScanSteps - 1d));
  }
}
