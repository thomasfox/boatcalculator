package com.github.thomasfox.boatcalculator.foil;

import com.github.thomasfox.boatcalculator.interpolate.QuantityRelation;
import lombok.Getter;
import lombok.ToString;

@ToString(of={"weight", "reynoldsNumber"})
public class WeightedReynoldsNumberAndPolar extends WeightedReynoldsNumber
{
  @Getter
  private final QuantityRelation polar;

  public WeightedReynoldsNumberAndPolar(double weight, double reynoldsNumber, QuantityRelation polar)
  {
    super(weight, reynoldsNumber);
    this.polar = polar;
  }
}
