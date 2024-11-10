package com.github.thomasfox.boatcalculator.foil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@AllArgsConstructor
@ToString
public class WeightedReynoldsNumber
{
  /** Weight between 0 and 1, sum of weights is 1. */
  private double weight;

  private double reynoldsNumber;

  public void addWeight(double toAdd)
  {
    this.weight += toAdd;
  }
}

