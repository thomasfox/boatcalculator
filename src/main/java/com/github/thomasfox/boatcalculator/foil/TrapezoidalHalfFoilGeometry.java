package com.github.thomasfox.boatcalculator.foil;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;
import lombok.Getter;

import java.util.Objects;

public class TrapezoidalHalfFoilGeometry implements HalfFoilGeometry
{
  @Getter
  private double halfwingSpan;

  @Getter
  private double innerChord;

  @Getter
  private double outerChord;

  public static TrapezoidalHalfFoilGeometry fromValueSet(ValueSet valueSet)
  {
    if (!valueSet.isValueKnown(PhysicalQuantity.HALFWING_SPAN)
        || !valueSet.isValueKnown(PhysicalQuantity.WING_INNER_CHORD)
        || !valueSet.isValueKnown(PhysicalQuantity.WING_OUTER_CHORD))
    {
      return null;
    }
    TrapezoidalHalfFoilGeometry result = new TrapezoidalHalfFoilGeometry();
    result.halfwingSpan = valueSet.getKnownQuantityValue(PhysicalQuantity.HALFWING_SPAN).getValue();
    result.innerChord = valueSet.getKnownQuantityValue(PhysicalQuantity.WING_INNER_CHORD).getValue();
    result.outerChord = valueSet.getKnownQuantityValue(PhysicalQuantity.WING_OUTER_CHORD).getValue();
    return result;
  }

  public static TrapezoidalHalfFoilGeometry fromModifiableWingParameters(ModifiableWingParameters modifiableWingParameters)
  {
    TrapezoidalHalfFoilGeometry result = new TrapezoidalHalfFoilGeometry();
    result.halfwingSpan = modifiableWingParameters.getWingSpan().getValue() / 2d;
    result.innerChord = modifiableWingParameters.getInnerChord().getValue();
    result.outerChord = modifiableWingParameters.getOuterChord().getValue();
    return result;
  }

  private TrapezoidalHalfFoilGeometry()
  {
  }

  @Override
  public double getChord(double position)
  {
    if (position < 0)
    {
      throw new IllegalArgumentException("position is " + position + " but should be >= 0");
    }
    if (position > halfwingSpan)
    {
      throw new IllegalArgumentException("position is " + position + " but should be <=" + halfwingSpan);
    }
    return innerChord + (outerChord - innerChord) * position / halfwingSpan;
  }

  @Override
  public double getArea()
  {
    return (outerChord + innerChord) * halfwingSpan / 2;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TrapezoidalHalfFoilGeometry that = (TrapezoidalHalfFoilGeometry) o;
    return Double.compare(halfwingSpan, that.halfwingSpan) == 0
        && Double.compare(innerChord, that.innerChord) == 0
        && Double.compare(outerChord, that.outerChord) == 0;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(halfwingSpan, innerChord, outerChord);
  }
}
