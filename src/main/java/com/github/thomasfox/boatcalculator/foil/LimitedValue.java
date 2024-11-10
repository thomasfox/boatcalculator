package com.github.thomasfox.boatcalculator.foil;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LimitedValue
{
  private double value;

  private double lowerLimit;

  private double upperLimit;
}
