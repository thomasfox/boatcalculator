package com.github.thomasfox.boatcalculator.calculate;

import lombok.Getter;

public class CompareWithOldResult
{
  public static final double RELATIVE_DIFFERENCE_THRESHOLD = 1e-4;

  @Getter
  private final Double difference;

  @Getter
  private final Double relativeDifference;

  @Getter
  private final double newResult;

  @Getter
  private final Double oldResult;

  public CompareWithOldResult(double newResult, Double oldResult)
  {
    this.newResult = newResult;
    this.oldResult = oldResult;
    if (oldResult == null)
    {
      this.difference = null;
      this.relativeDifference = null;
      return;
    }
    difference = newResult - oldResult;
    if (difference == 0d)
    {
      relativeDifference = 0d;
    }
    else
    {
      double maxAbsoluteValue = Math.max(Math.abs(newResult), Math.abs(oldResult));
      double absoluteDifference = Math.abs(difference);
      relativeDifference = absoluteDifference / maxAbsoluteValue;
    }
  }

  public boolean relativeDifferenceIsBelowThreshold()
  {
    return relativeDifference != null
        && (relativeDifference.equals(Double.valueOf(Double.NaN))
            || relativeDifference < RELATIVE_DIFFERENCE_THRESHOLD);
  }

  @Override
  public String toString()
  {
    return "[difference=" + difference + ", relativeDifference=" + relativeDifference + "]";
  }
}
