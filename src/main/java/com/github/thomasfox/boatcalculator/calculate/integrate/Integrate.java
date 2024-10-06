package com.github.thomasfox.boatcalculator.calculate.integrate;

public abstract class Integrate
{
  protected int steps = 1000;

  public abstract double y(double x);

  public double integrate (double fromX, double toX)
  {
    if (fromX >= toX)
    {
      throw new IllegalArgumentException("fromX " + fromX + " must be less that toX " + toX);
    }
    double xSpan = toX - fromX;
    double step = (xSpan / steps);
    double x = fromX + (step / 2);
    double sum = 0;
    for (int i = 0; i < steps; i++)
    {
      sum += y(x);
      x += step;
    }
    return sum * xSpan / steps;
  }
}
