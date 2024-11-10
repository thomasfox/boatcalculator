package com.github.thomasfox.boatcalculator.calculate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.thomasfox.boatcalculator.calculate.impl.*;
import com.github.thomasfox.boatcalculator.interpolate.FoilPolarCalculator;
import org.slf4j.MDC;

import com.github.thomasfox.boatcalculator.interpolate.QuantityRelationsCalculator;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CombinedCalculator
{
  private final List<Calculator> calculators = new ArrayList<>();

  private final QuantityRelationsCalculator quantityRelationsCalculator = new QuantityRelationsCalculator();

  FoilPolarCalculator foilPolarCalculator = new FoilPolarCalculator();

  public CombinedCalculator()
  {
    calculators.add(new AngleOfAttackFromAngleToHorizontalCalculator());
    calculators.add(new AreaInMediumCalculator());
    calculators.add(new AreaInMediumTrapezoidalWingCalculator());
    calculators.add(new SemiwingAspectRatioCalculator());
    calculators.add(new WingChordFromAreaAndSpanInMediumCalculator());
    calculators.add(new ReynoldsNumberCalculator());
    calculators.add(new InducedDragCoefficientCalculator());
    calculators.add(new InducedDragCalculator());
    calculators.add(new ParasiticDragCalculator());
    // calculators.add(new AreaLoadFixedMiddleBendingCalculator()); TODO must be readded
    calculators.add(new AreaLoadFixedMiddleTrapezoidalWingBendingCalculator());
    calculators.add(new SecondMomentOfAreaCalculator());
    calculators.add(new LiftCoefficient3DCalculator());
    calculators.add(new LiftCoefficient3DFromLiftCoefficientCalculator());
    calculators.add(new LiftCoefficientFromLiftCoefficient3DCalculator());
    calculators.add(new CrosssectionAreaCalculator());
    calculators.add(new ThicknessCalculator());
    calculators.add(new WingChordFromSecondMomentOfAreaCalculator());
    calculators.add(new ProfileDragCalculator());
    calculators.add(new ApparentWindDirectionCalculator());
    calculators.add(new ApparentWindSpeedCalculator());
    calculators.add(new LiftCalculator());
    calculators.add(new TotalDragCoefficientCalculator());
    calculators.add(new TotalDragCalculator());
    calculators.add(new LiftDividedByTotalDragCalculator());
    calculators.add(new LateralForceCalculator());
    calculators.add(new DrivingForceCalculator());
    calculators.add(new TorqueCalculator());
    calculators.add(new LeverFromWeightCalculator());
    calculators.add(new VMGCalculator());
    calculators.add(new SailingAngleCalculator());
    calculators.add(new BrakingForceCalculator());
    calculators.add(new FroudeNumberCalculator());
    calculators.add(new WaveMakingDragCoefficientCalculator());
    calculators.add(new WaveMakingDragCalculator());
    calculators.add(new WeightFromMassCalculator());
    calculators.add(new SurfacePiercingDragCalculator());
    calculators.add(new SurfacePiercingDragCoefficientCalculator());
    calculators.add(new BrakingForceForBentWingCalculator());
    calculators.add(new TorqueForBentWingCalculator());
    calculators.add(new WingSpanFromFixedAreaLoadAndAreaCalculator());
    calculators.add(new ChordFromReynoldsNumberCalculator());
    calculators.add(new HalfwingSpanCalculator());
  }

  public Set<String> calculate(ValueSet valueSet, PhysicalQuantity wantedQuantity, int step)
  {
    try
    {
      MDC.put("valueSet", valueSet.getId());
      Set<String> changedOverall = new HashSet<>();
      Set<String> changedInCurrentIteration = new HashSet<>();
      int cutoff = 100;
      do
      {
        changedInCurrentIteration.clear();
        try
        {
          changedInCurrentIteration.addAll(applyCalculators(valueSet));
          log.debug("changedInCurrentIteration is " + changedInCurrentIteration + " after applying calculators");
          changedInCurrentIteration.addAll(quantityRelationsCalculator.applyQuantityRelations(valueSet));
          log.debug("changedInCurrentIteration is " + changedInCurrentIteration + " after applying quantity Relations");

          foilPolarCalculator.replaceFoilPolar(valueSet);
        }
        finally
        {
          changedOverall.addAll(changedInCurrentIteration);
          cutoff--;
        }
      }
      while (!changedInCurrentIteration.isEmpty() && cutoff > 0 && !valueSet.isValueKnown(wantedQuantity));
      return changedOverall;
    }
    finally
    {
      MDC.remove("valueSet");
    }
  }

  public Set<String> applyCalculators(ValueSet valueSet)
  {
    boolean changedInCurrentIteration;
    Set<String> changedOverall = new HashSet<>();
    do
    {
      changedInCurrentIteration = false;
      for (Calculator calculator: calculators)
      {
        if (calculator.apply(valueSet))
        {
          changedInCurrentIteration = true;
          changedOverall.add(valueSet.getId() + ":" + calculator.getOutputQuantity());
        }
      }
    }
    while (changedInCurrentIteration == true);
    return changedOverall;
  }

  public List<Calculator> getCalculatorsWithOutput(PhysicalQuantity physicalQuantity)
  {
    return calculators.stream().filter(c -> c.getOutputQuantity().equals(physicalQuantity)).collect(Collectors.toList());
  }
}
