package com.github.thomasfox.wingcalculator.interpolate;

import java.util.List;

/**
 * Interpolates a quantity from some known points of a relation between x and y.
 * The known points are connected by straight lines.
 * The first intersection between where a line matches the given x value
 * is returned.
 */
public class Interpolator
{
  /**
   * Calculates a matching y value for the given x value.
   *
   * @param xValue the x value for which the y value should be calculated.
   * @param knownValues the known relation points.
   *       The key of a point is the x coordiante,
   *       the value of a point is the y coordinate.
   *
   * @return the interpolated function (y) value for the x value.
   *
   * @throws InterpolatorException if an error occurs while interpolating.
   */
  public double interpolate(double xValue, List<XYPoint> knownValues)
  {
    if (knownValues == null)
    {
      throw new InterpolatorException("The list of known values is null");
    }
    if (knownValues.isEmpty())
    {
      throw new InterpolatorException("The list of known values is empty");
    }

    XYPoint lastValue = null;
    double minEncounteredX = knownValues.get(0).getX();
    double maxEncounteredX = minEncounteredX;

    for (XYPoint currentValue : knownValues)
    {
      if (currentValue == null)
      {
        throw new InterpolatorException("The list of known values contains null");
      }
      if (xValue == currentValue.getX())
      {
        return currentValue.getY();
      }

      minEncounteredX = Math.min(currentValue.getX(), minEncounteredX);
      maxEncounteredX = Math.max(currentValue.getX(), maxEncounteredX);

      if (lastValue == null)
      {
        lastValue = currentValue;
        continue;
      }

      Double currentMinX = Math.min(currentValue.getX(), lastValue.getX());
      Double currentMaxX = Math.max(currentValue.getX(), lastValue.getX());
      if (xValue < currentMinX || xValue > currentMaxX)
      {
        lastValue = currentValue;
        continue;
      }

      double relativeWeightOfCurrentValue = (xValue - lastValue.getX()) / (currentValue.getX() - lastValue.getX());
      double result = relativeWeightOfCurrentValue * currentValue.getY() + (1d - relativeWeightOfCurrentValue) * lastValue.getY();
      return result;
    }
    throw new InterpolatorException("The provided xValue, " + xValue
        + ", does not match the interpolation interval ["
        + minEncounteredX + "," + maxEncounteredX + "]");
  }
}
