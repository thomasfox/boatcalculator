package com.github.thomasfox.boatcalculator.foil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
public class Penalty
{
  @Getter
  private Double totalDrag;

  @Getter
  private Double totalBending;

  private Double penaltyValue;

  @Getter
  private double innerChord;

  @Getter
  private double outerChord;

  @Getter
  private double span;

  public double getPenalty()
  {
    if (penaltyValue != null)
    {
      return penaltyValue;
    }
    if (totalDrag == null)
    {
      penaltyValue = Double.MAX_VALUE;
      return penaltyValue;
    }
    if (totalBending == null)
    {
      penaltyValue = Double.MAX_VALUE;
      return penaltyValue;
    }
    penaltyValue = totalDrag / FoilOptimizer.TARGET_DRAG;
    if (totalBending > FoilOptimizer.ACCEPTED_BENDING)
    {
      penaltyValue += 5 * (totalBending - FoilOptimizer.ACCEPTED_BENDING)/FoilOptimizer.ACCEPTED_BENDING;
    }
    return penaltyValue;
  }

  public Penalty getSmallerPenalty(Penalty other)
  {
    if (other == null)
    {
      return this;
    }
    if (other.getPenalty() <= this.getPenalty())
    {
      return other;
    }
    return this;
  }

  public boolean bendingTooLarge()
  {
    return totalBending != null && totalBending > FoilOptimizer.ACCEPTED_BENDING;
  }
}
