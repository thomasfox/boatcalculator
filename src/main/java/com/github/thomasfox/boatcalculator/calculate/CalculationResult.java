package com.github.thomasfox.boatcalculator.calculate;

import lombok.Getter;

@Getter
public class CalculationResult
{
  private final CompareWithOldResult compareWithOldResult;

  private final boolean isTrial;

  public CalculationResult(double newResult, Double oldResult, boolean isTrial)
  {
    this.compareWithOldResult = new CompareWithOldResult(newResult, oldResult);
    this.isTrial = isTrial;
  }

  public double getDifference()
  {
    return compareWithOldResult.getDifference();
  }

  public double getValue()
  {
    return compareWithOldResult.getNewResult();
  }

  public boolean relativeDifferenceIsBelowThreshold()
  {
    return compareWithOldResult.relativeDifferenceIsBelowThreshold();
  }

  @Override
  public String toString()
  {
    return compareWithOldResult.toString();
  }
}
