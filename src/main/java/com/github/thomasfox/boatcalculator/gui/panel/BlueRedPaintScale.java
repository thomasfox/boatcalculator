package com.github.thomasfox.boatcalculator.gui.panel;

import java.awt.Color;
import java.awt.Paint;

import org.jfree.chart.renderer.PaintScale;

public class BlueRedPaintScale implements PaintScale
{
  /** maximum absolute value which can be displayed */
  private final double maximumAbsoluteValue;

  /**
   * Creates a new paint scale for values in the specified range.
   *
   * @param lowerBound the lower bound.
   * @param upperBound the upper bound.
   *
   * @throws IllegalArgumentException if <code>lowerBound</code> is not
   *       less than <code>upperBound</code>, or <code>alpha</code> is not in
   *       the range 0 to 255.
   *
   */
  public BlueRedPaintScale(double maximumAbsoluteValue)
  {
    if (maximumAbsoluteValue <= 0) {
        throw new IllegalArgumentException(
                "Requires maximumAbsoluteValue > 0");
    }
    this.maximumAbsoluteValue = maximumAbsoluteValue;
  }

  /**
   * Returns the lower bound.
   *
   * @return The lower bound.
   */
  @Override
  public double getLowerBound()
  {
    return -maximumAbsoluteValue;
  }

  /**
   * Returns the upper bound.
   *
   * @return The upper bound.
   */
  @Override
  public double getUpperBound()
  {
    return maximumAbsoluteValue;
  }

  /**
   * Returns a paint for the specified value.
   *
   * @param value  the value .
   *
   * @return A paint for the specified value.
   */
  @Override
  public Paint getPaint(double value)
  {
    if (Math.abs(value) > maximumAbsoluteValue)
    {
      return new Color(127, 127, 127);
    }
    int intensity = (int) (value / maximumAbsoluteValue * 255);
    if (value < 0)
    {
      return new Color(0, 0, -intensity);
    }
    return new Color(intensity, 0, 0);
  }
}
