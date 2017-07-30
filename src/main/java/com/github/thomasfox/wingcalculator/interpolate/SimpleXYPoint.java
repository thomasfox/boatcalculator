package com.github.thomasfox.wingcalculator.interpolate;

public class SimpleXYPoint implements XYPoint
{
  private final double x;

  private final double y;

  public SimpleXYPoint(double x, double y)
  {
    this.x = x;
    this.y = y;
  }

  @Override
  public double getX()
  {
    return x;
  }

  @Override
  public double getY()
  {
    return y;
  }
}
