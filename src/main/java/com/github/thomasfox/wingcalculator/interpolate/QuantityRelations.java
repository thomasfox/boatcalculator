package com.github.thomasfox.wingcalculator.interpolate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * Describes relations between multiple physical quantity along the line of
 * "if quantity a has value x, the quantity b has value y and quantity c has value z"
 */
@Builder
@Data
public class QuantityRelations
{
  @NonNull
  private String name;

  @NonNull
  private Map<PhysicalQuantity, Double> fixedQuantities = new HashMap<>();

  @NonNull
  private List<PhysicalQuantity> relatedQuantities = new ArrayList<>();

  @NonNull
  private List<Map<PhysicalQuantity, Double>> relatedQuantityValues = new ArrayList<>();

  /**
   * The PhysicalQuantity of which the other Quantities are functions.
   * May be null.
   */
  private final PhysicalQuantity keyQuantity;

  public Double interpolateValueFrom(
      PhysicalQuantity wantedQuantity,
      PhysicalQuantity providedQuantity,
      Double providedValue)
  {
    List<XYPoint> interpolationPoints = new ArrayList<>();
    for (Map<PhysicalQuantity, Double> relatedValues : relatedQuantityValues)
    {
      Double xValue = relatedValues.get(providedQuantity);
      if (xValue == null)
      {
        throw new InterpolatorException("Quantity " + providedQuantity + " not found");
      }
      Double yValue = relatedValues.get(wantedQuantity);
      if (yValue == null)
      {
        throw new InterpolatorException("Quantity " + wantedQuantity + " not found");
      }
      interpolationPoints.add(new SimpleXYPoint(xValue, yValue));
    }
    return new Interpolator().interpolate(providedValue, interpolationPoints);
  }
}
