package com.github.thomasfox.sailboatcalculator.interpolate;

import java.util.List;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Data;

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
    TwoValues<XYPoint> enclosingPoints = getEnclosing(xValue, knownValues, XYPoint::getX);
    if (enclosingPoints.value1 == enclosingPoints.value2)
    {
      return enclosingPoints.value1.getY();
    }

    double relativeWeightOfCurrentValue = (xValue - enclosingPoints.value2.getX()) / (enclosingPoints.value1.getX() - enclosingPoints.value2.getX());
    double result = relativeWeightOfCurrentValue * enclosingPoints.value1.getY() + (1d - relativeWeightOfCurrentValue) * enclosingPoints.value2.getY();
    return result;
  }

  /**
   * Gets, from a list of discrete values, the first two neighbouring values
   * which mapped representatives enclose a given number.
   * If a mapped representative exactly equals the given number,
   * then the matching value is returned twice.
   *
   * @param toEnclose the value to be enclosed between two numbers.
   * @param availableValues the available values for enclosement.
   * @param mapper the mapper which maps the avaliable number to a double,
   *        which then is used to enclose the toEnclose value.
   *
   * @return the first neighbouring value which, when mapped by the mapper,
   *         enclose the given number.
   */
  public <T> TwoValues<T> getEnclosing(double toEnclose, List<T> availableValues, Function<T, Double> mapper)
  {
    if (availableValues == null)
    {
      throw new InterpolatorException("The list of available values is null");
    }
    if (availableValues.isEmpty())
    {
      throw new InterpolatorException("The list of available values is empty");
    }

    T lastValue = null;
    Double mappedLastValue = null;
    Double minEncounteredValue = mapper.apply(availableValues.get(0));
    Double maxEncounteredValue = minEncounteredValue;

    for (T currentValue : availableValues)
    {
      Double mappedCurrentValue = mapper.apply(currentValue);
      if (mappedCurrentValue == null)
      {
        throw new InterpolatorException("The list of avaliable values contains null");
      }
      if (toEnclose == mappedCurrentValue)
      {
        return new TwoValues<T>(currentValue, currentValue);
      }

      minEncounteredValue = Math.min(mappedCurrentValue, minEncounteredValue);
      maxEncounteredValue = Math.max(mappedCurrentValue, maxEncounteredValue);

      if (lastValue == null)
      {
        lastValue = currentValue;
        mappedLastValue = mappedCurrentValue;
        continue;
      }

      Double currentMinX = Math.min(mappedCurrentValue, mappedLastValue);
      Double currentMaxX = Math.max(mappedCurrentValue, mappedLastValue);
      if (toEnclose < currentMinX || toEnclose > currentMaxX)
      {
        lastValue = currentValue;
        mappedLastValue = mappedCurrentValue;
        continue;
      }
      return new TwoValues<T>(currentValue, lastValue);
    }
    throw new InterpolatorException("The provided value, " + toEnclose
        + ", does not match the interpolation interval ["
        + minEncounteredValue + "," + maxEncounteredValue + "]");
  }

  @Data
  @AllArgsConstructor
  public static class TwoValues<T>
  {
    final T value1;
    final T value2;
  }
}
