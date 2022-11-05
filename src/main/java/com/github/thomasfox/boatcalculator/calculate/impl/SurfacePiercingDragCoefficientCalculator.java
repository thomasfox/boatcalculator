package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

/**
 * Hoerner, Fluid-dynamic drag, equation 10-31
 * Beaver/Zseleczky, Full Scale Measurements on a Hydrofoil International Moth
 * Coefficient based on thickness is reported from .24 to 0.38, we use 0.3 as a middle ground
 */
public class SurfacePiercingDragCoefficientCalculator extends Calculator
{
  public SurfacePiercingDragCoefficientCalculator()
  {
    super(PhysicalQuantity.SURFACE_PIERCING_DRAG_COEFFICIENT,
        PhysicalQuantity.AREA_IN_MEDIUM,
        PhysicalQuantity.WING_THICKNESS);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double coefficientBasedOnThickness = 0.3d;
    double area = valueSet.getKnownQuantityValue(PhysicalQuantity.AREA_IN_MEDIUM).getValue();
    double wingThickness = valueSet.getKnownQuantityValue(PhysicalQuantity.WING_THICKNESS).getValue();
    double surfacePiercingDragCoefficient
        = coefficientBasedOnThickness * wingThickness * wingThickness / area ;
    return surfacePiercingDragCoefficient;
  }
}
