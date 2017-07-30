package com.github.thomasfox.wingcalculator.interpolate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SimpleXYPoint implements XYPoint
{
  private final double x;

  private final double y;
}
