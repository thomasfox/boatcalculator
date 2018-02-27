package com.github.thomasfox.boatcalculator.interpolate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class SimpleXYPoint implements XYPoint
{
  private final double x;

  private final double y;
}
