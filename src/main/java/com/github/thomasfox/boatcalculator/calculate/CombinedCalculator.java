package com.github.thomasfox.boatcalculator.calculate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.MDC;

import com.github.thomasfox.boatcalculator.calculate.impl.AngleOfAttackFromAngleToHorizontalCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.ApparentWindDirectionCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.ApparentWindSpeedCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.AreaInMediumCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.AreaLoadFixedMiddleBendingCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.BrakingForceCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.CrosssectionAreaCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.DrivingForceCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.BrakingForceForBentWingCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.FroudeNumberCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.InducedDragCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.InducedDragCoefficientCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.LateralForceCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.LeverFromWeightCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.LiftCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.LiftCoefficient3DCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.LiftCoefficient3DFromLiftCoefficientCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.LiftCoefficientFromLiftCoefficient3DCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.LiftDividedByTotalDragCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.ParasiticDragCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.ProfileDragCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.ReynoldsNumberCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.SailingAngleCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.SecondMomentOfAreaCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.SemiwingAspectRatioCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.SurfacePiercingDragCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.SurfacePiercingDragCoefficientCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.ThicknessCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.TorqueCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.TorqueForBentWingCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.TotalDragCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.TotalDragCoefficientCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.VMGCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.WaveMakingDragCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.WaveMakingDragCoefficientCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.WeightFromMassCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.WingChordFromAreaAndSpanInMediumCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.WingChordFromSecondMomentOfAreaCalculator;
import com.github.thomasfox.boatcalculator.interpolate.QuantityRelation;
import com.github.thomasfox.boatcalculator.interpolate.QuantityRelationsCalculator;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CombinedCalculator
{
  private final List<Calculator> calculators = new ArrayList<>();

  private final QuantityRelationsCalculator quantityRelationsCalculator = new QuantityRelationsCalculator();

  public CombinedCalculator()
  {
    calculators.add(new AngleOfAttackFromAngleToHorizontalCalculator());
    calculators.add(new AreaInMediumCalculator());
    calculators.add(new SemiwingAspectRatioCalculator());
    calculators.add(new WingChordFromAreaAndSpanInMediumCalculator());
    calculators.add(new ReynoldsNumberCalculator());
    calculators.add(new InducedDragCoefficientCalculator());
    calculators.add(new InducedDragCalculator());
    calculators.add(new ParasiticDragCalculator());
    calculators.add(new AreaLoadFixedMiddleBendingCalculator());
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
  }

  public void setQuantityRelations(List<QuantityRelation> quantityRelationsList)
  {
    quantityRelationsCalculator.setQuantityRelations(quantityRelationsList);
  }

  public Set<String> calculate(ValueSet valueSet, PhysicalQuantity wantedQuantity, int step)
  {
    Set<String> changed = new HashSet<>();
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
