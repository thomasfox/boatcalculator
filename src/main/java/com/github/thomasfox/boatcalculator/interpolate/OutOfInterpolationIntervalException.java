package com.github.thomasfox.boatcalculator.interpolate;

import lombok.Getter;

public class OutOfInterpolationIntervalException extends InterpolatorException
{
  private static final long serialVersionUID = 1L;

  @Getter
  private final boolean aboveInterval;

  public OutOfInterpolationIntervalException(String message, boolean aboveInterval)
  {
    super(message);
    this.aboveInterval = aboveInterval;
  }
}
