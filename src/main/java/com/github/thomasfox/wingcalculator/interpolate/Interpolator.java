package com.github.thomasfox.wingcalculator.interpolate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Interpolates a quantity from some known points of a function.
 * The nearest points are connected by straight lines.
 */
public class Interpolator
{
  public double interpolate(double xValue, LinkedHashMap<Double, Double> knownValues)
  {
    Map.Entry<Double, Double> lastValue = null;
    for (Map.Entry<Double, Double> currentValue : knownValues.entrySet())
    {
      if (currentValue.getKey() == null)
      {
        throw new InterpolatorException("The known values contain null as key");
      }
      if (lastValue != null && currentValue.getKey() <= lastValue.getKey())
      {
        throw new InterpolatorException("The known values x values are not strictly monotonous");
      }
      if (currentValue.getValue() == null)
      {
        throw new InterpolatorException("The known values contain null as value");
      }
      if (xValue == currentValue.getKey())
      {
        double result = currentValue.getValue();
        return result;
      }
      if (xValue < currentValue.getKey())
      {
        if (lastValue == null)
        {
          throw new InterpolatorException("The provided xValue, " + xValue
              + ", is below the interpolation interval which lower bound is " + currentValue.getKey());
        }
        double relativeWeightOfCurrentValue = (xValue - lastValue.getKey()) / (currentValue.getKey() - lastValue.getKey());
        double result = relativeWeightOfCurrentValue * currentValue.getValue() + (1d - relativeWeightOfCurrentValue) * lastValue.getValue();
        return result;
      }
      lastValue = currentValue;
    }
    throw new InterpolatorException("The provided xValue, " + xValue
        + ", is above the interpolation interval which upper bound is " + lastValue.getKey());
  }
}
