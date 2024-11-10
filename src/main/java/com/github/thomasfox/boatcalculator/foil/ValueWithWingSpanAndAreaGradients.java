package com.github.thomasfox.boatcalculator.foil;

import lombok.Data;

@Data
public class ValueWithWingSpanAndAreaGradients
{
  private double value;

  private double gradientWithRespectToWingSpan;

  private double gradientWithRespectToArea;

  public double valueForIncreaseOfWingSpan(double increaseInWingSpan)
  {
    return value + gradientWithRespectToWingSpan * increaseInWingSpan;
  }
}
