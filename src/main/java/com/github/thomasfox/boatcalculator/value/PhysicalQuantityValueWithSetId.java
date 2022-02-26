package com.github.thomasfox.boatcalculator.value;

public interface PhysicalQuantityValueWithSetId extends PhysicalQuantityValue
{
  /**
   * Returns the id of the ValueSet in which this PhysicalQuantityValue lives.
   *
   * @return the set id, not null.
   */
  String getSetId();

  PhysicalQuantityValue getPhysicalQuantityValue();


  default PhysicalQuantityInSet getPhysicalQuantityInSet()
  {
    return new PhysicalQuantityInSet(getPhysicalQuantity(), getSetId());
  }
}
