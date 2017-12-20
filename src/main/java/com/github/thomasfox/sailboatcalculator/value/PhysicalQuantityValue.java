package com.github.thomasfox.sailboatcalculator.value;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class PhysicalQuantityValue
{
  @NonNull
  private final PhysicalQuantity physicalQuantity;

  @Setter
  private double value;

  public PhysicalQuantityValue(PhysicalQuantityValue toCopy)
  {
    this.physicalQuantity = toCopy.physicalQuantity;
    this.value = toCopy.value;
  }

  @Override
  public String toString()
  {
    return physicalQuantity + "=" + value;
  }

  /**
   * Returns a deep copy of this object.
   * Subclasses <b>must</b> override this method and return a deep copy
   * of the subclass object,
   */
  @Override
  public PhysicalQuantityValue clone()
  {
    return new PhysicalQuantityValue(this);
  }
}
