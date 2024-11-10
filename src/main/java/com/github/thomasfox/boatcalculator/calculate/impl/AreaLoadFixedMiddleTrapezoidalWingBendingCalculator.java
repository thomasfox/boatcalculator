package com.github.thomasfox.boatcalculator.calculate.impl;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.calculate.integrate.Integrate;
import com.github.thomasfox.boatcalculator.interpolate.QuantityRelation;
import com.github.thomasfox.boatcalculator.value.CalculatedPhysicalQuantityValues;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValues;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Gibt die Durchbiegung eines Flügels, der in der Mitte fest gelagert ist an den Enden frei ist, zurück.
 * Der Flügel ist über die gesamte Fläche gleichmäßig belastet.
 */
public class AreaLoadFixedMiddleTrapezoidalWingBendingCalculator extends Calculator
{
  private final QuantityRelation correctionFactors;

  public AreaLoadFixedMiddleTrapezoidalWingBendingCalculator()
  {
    super(PhysicalQuantity.BENDING,
        PhysicalQuantity.WING_SPAN_IN_MEDIUM,
        PhysicalQuantity.WING_INNER_CHORD,
        PhysicalQuantity.WING_OUTER_CHORD,
        PhysicalQuantity.LIFT,
        PhysicalQuantity.MODULUS_OF_ELASTICITY,
        PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA);

    List<PhysicalQuantityValues> relatedQuantities = new ArrayList<>();
    for (int i=0; i <= 500; i++)
    {
      double x = i / 100d;
      double correction = calculateFactorForTrapezoidalWing(x);
      PhysicalQuantityValues point = new PhysicalQuantityValues();
      point.setValue(PhysicalQuantity.WING_INNER_OUTER_CHORD_RATIO, x);
      point.setValue(PhysicalQuantity.TRAPEZOIDAL_WING_BENDING_CORRECTION_FACTOR, correction);
      relatedQuantities.add(point);
    }
    correctionFactors = new QuantityRelation(
        "correctionFactor",
        new PhysicalQuantityValues(),
        relatedQuantities,
        PhysicalQuantity.WING_INNER_OUTER_CHORD_RATIO);
  }

  @Override
  protected double calculateWithoutChecks(ValueSet valueSet)
  {
    double wingWidth = valueSet.getKnownQuantityValue(PhysicalQuantity.WING_SPAN_IN_MEDIUM).getValue();
    double beamLength = wingWidth / 2;
    double force = valueSet.getKnownQuantityValue(PhysicalQuantity.LIFT).getValue();
    double bendingForce = force / 2;
    double modulusOfElasicity = valueSet.getKnownQuantityValue(PhysicalQuantity.MODULUS_OF_ELASTICITY).getValue();
    double normalizedSecondMomentOfArea = valueSet.getKnownQuantityValue(PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA).getValue();
    double wingInnerChord = valueSet.getKnownQuantityValue(PhysicalQuantity.WING_INNER_CHORD).getValue();
    double wingOuterChord = valueSet.getKnownQuantityValue(PhysicalQuantity.WING_OUTER_CHORD).getValue();
    double chordRatio = wingOuterChord / wingInnerChord;

    //double correctionFactor = calculateFactorForTrapezoidalWing(chordRatio);
    CalculatedPhysicalQuantityValues correctionFactorValues = correctionFactors.getRelatedQuantityValues(
        new SimplePhysicalQuantityValue(PhysicalQuantity.WING_INNER_OUTER_CHORD_RATIO, chordRatio));
    double correctionFactor;
    try {
      correctionFactor = correctionFactorValues.getValue(PhysicalQuantity.TRAPEZOIDAL_WING_BENDING_CORRECTION_FACTOR);
    }
    catch (RuntimeException e)
    {
      System.out.println(e);
      throw e;
    }
    return bendingForce*beamLength*beamLength*beamLength*correctionFactor
        /(8*modulusOfElasicity*normalizedSecondMomentOfArea*wingInnerChord*wingInnerChord*wingInnerChord*wingInnerChord);
  }

  protected double calculateFactorForTrapezoidalWing(double chordRatio)
  {
    return new IntegrateCorrectionFunction(chordRatio).integrate(0, 1) * 8 / (3 * (1 + chordRatio));
  }

  private static class IntegrateCorrectionFunction extends Integrate
  {
    private final double chordRatio;

    private IntegrateCorrectionFunction(double chordRatio)
    {
      this.chordRatio = chordRatio;
    }

    @Override
    public double y(double x)
    {
      double denominatorFactor = (1-(1-chordRatio)*x);
      double firstDenominator = denominatorFactor*denominatorFactor*denominatorFactor;
      double nominatorFactor = 1 - x;
      return nominatorFactor * nominatorFactor * nominatorFactor
          *(1 / firstDenominator
            + 2 * chordRatio * 1 / (firstDenominator * denominatorFactor));
    }
  }
}
