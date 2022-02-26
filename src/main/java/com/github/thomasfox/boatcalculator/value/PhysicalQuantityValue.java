package com.github.thomasfox.boatcalculator.value;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;

public interface PhysicalQuantityValue extends Cloneable
{
  PhysicalQuantity getPhysicalQuantity();

  double getValue();

  /**
   * Returns a deep copy of this object.
   * Subclasses <b>must</b> override this method and return a deep copy
   * of the subclass object,
   */
  public boolean isTrial();

  public Object clone();
}
