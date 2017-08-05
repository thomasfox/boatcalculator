package com.github.thomasfox.wingcalculator.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.thomasfox.wingcalculator.interpolate.Interpolator;
import com.github.thomasfox.wingcalculator.interpolate.SimpleXYPoint;
import com.github.thomasfox.wingcalculator.interpolate.XYPoint;

import lombok.NonNull;

public class Profile
{
  private final int CALCULATION_STEPS = 50;

  private final String name;

  private final List<XYPoint> points;

  public Profile(@NonNull String name, @NonNull List<XYPoint> points)
  {
    this.name = name;
    this.points = points;
    if (points.size() < 2)
    {
      throw new IllegalArgumentException("A profile needs at least two points");
    }

    double minX = points.get(0).getX();
    double maxX = points.get(0).getX();
    for (int i = 1; i < points.size(); ++i)
    {
      minX = Math.min(minX, points.get(i).getX());
      maxX = Math.max(maxX, points.get(i).getX());
    }
    if (minX > 0 || minX < 0)
    {
      throw new IllegalArgumentException("Minimal X must be 0");
    }
    if (maxX > 1 || maxX < 1)
    {
      throw new IllegalArgumentException("Maximal X must be 1");
    }
  }

  public double getThickness()
  {
    double minY = points.get(0).getY();
    double maxY = points.get(0).getY();
    for (int i = 1; i < points.size(); ++i)
    {
      minY = Math.min(minY, points.get(i).getY());
      maxY = Math.max(maxY, points.get(i).getY());
    }
    return maxY - minY;
  }

  private double getY(double x, List<XYPoint> points)
  {
    if (x < 0 || x > 1)
    {
      throw new IllegalArgumentException("X must be in interval[0,1] but is " + x);
    }
    return new Interpolator().interpolate(x, points);
  }

  public double getUpperY(double x)
  {
    double y1 = getY(x, points);
    List<XYPoint> reverseOrderOfPoints = new ArrayList<>(points);
    Collections.reverse(reverseOrderOfPoints);
    double y2 = getY(x, reverseOrderOfPoints);
    return Math.max(y1, y2);
  }

  public double getLowerY(double x)
  {
    double y1 = getY(x, points);
    List<XYPoint> reverseOrderOfPoints = new ArrayList<>(points);
    Collections.reverse(reverseOrderOfPoints);
    double y2 = getY(x, reverseOrderOfPoints);
    return Math.min(y1, y2);
  }

  /**
   * Gibt den geometrischen Schwerpunkt des Profils zur�ck.
   *
   * @return den geometrischen Schwerpunkt, nicht null.
   */
  public XYPoint getBalancePoint()
  {
    double resultX = 0;
    double resultY = 0;
    double area = 0;
    double xStep = 1d / CALCULATION_STEPS;
    for (double x = xStep / 2; x < 1; x += xStep)
    {
      double minY = getLowerY(x);
      double maxY = getUpperY(x);
      double thickness = maxY - minY;
      double centerY = (minY + maxY) / 2;
      area += thickness * xStep;
      resultX += x * thickness * xStep;
      resultY += centerY * thickness * xStep;
    }
    return new SimpleXYPoint(resultX / area, resultY / area);
  }

  /**
   * Gibt das Tr�gheitsmoment f�r die Biegesteifigkeit f�r ein Profil der L�nge 1 zur�ck.
   *
   * @return das Tr�gheitsmoment, nicht null.
   */
  public double getSecondMomentOfArea()
  {
    double balanceY = getBalancePoint().getY();
    double result = 0;
    double xStep = 1d / CALCULATION_STEPS;
    for (double x = xStep / 2; x < 1; x += xStep)
    {
      double minY = getLowerY(x);
      double maxY = getUpperY(x);
      double distance1FromCenter = (balanceY - minY);
      double distance2FromCenter = (maxY - balanceY);
      // Stammfunktion von <^2 ist 1/3 x^3, xStep und 1/3 werden unten hinzugef�gt
      result += (distance1FromCenter * distance1FromCenter * distance1FromCenter)
          + (distance2FromCenter *distance2FromCenter * distance2FromCenter);
    }
    return xStep * result / 3;
  }
}
