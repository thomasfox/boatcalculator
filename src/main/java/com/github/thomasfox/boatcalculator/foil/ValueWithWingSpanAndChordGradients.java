package com.github.thomasfox.boatcalculator.foil;

import lombok.Data;

@Data
public class ValueWithWingSpanAndChordGradients
{
  private double value;

  private double valueWithOtherWingSpan;

  private double gradientWithRespectToInnerChord;

  private double gradientWithRespectToOuterChord;

  public void addToValue(double toAdd)
  {
    value += toAdd;
  }

  public void addToValueWithOtherWingSpan(double toAdd)
  {
    valueWithOtherWingSpan += toAdd;
  }

  public void addToGradientWithRespectToInnerChord(double toAdd)
  {
    gradientWithRespectToInnerChord += toAdd;
  }

  public void addToGradientWithRespectToOuterChord(double toAdd)
  {
    gradientWithRespectToOuterChord += toAdd;
  }
}
