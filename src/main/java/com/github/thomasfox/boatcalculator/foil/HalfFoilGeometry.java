package com.github.thomasfox.boatcalculator.foil;

public interface HalfFoilGeometry
{
  double getHalfwingSpan();

  /**
   * returns the chord at a given position within the span;
   * @param position the span position, greater or equal than zero and less or equal than the halfwing span
   * @return the chord at the given position
   */
  double getChord(double position);

  double getArea();
}
