package com.github.thomasfox.boatcalculator.calculate.strategy;

import java.util.HashSet;
import java.util.Set;

import com.github.thomasfox.boatcalculator.calculate.CompareWithOldResult;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.calculate.impl.LateralForceCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.LiftCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.LiftCoefficientFromLiftCoefficient3DCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.TorqueCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.TotalDragCalculator;
import com.github.thomasfox.boatcalculator.interpolate.QuantityRelationsCalculator;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;
import com.github.thomasfox.boatcalculator.valueset.ValuesAndCalculationRules;
import com.github.thomasfox.boatcalculator.valueset.impl.BoatGlobalValues;
import com.github.thomasfox.boatcalculator.valueset.impl.Crew;
import com.github.thomasfox.boatcalculator.valueset.impl.DaggerboardOrKeel;
import com.github.thomasfox.boatcalculator.valueset.impl.LeverSailDaggerboard;
import com.github.thomasfox.boatcalculator.valueset.impl.Rigg;
import com.github.thomasfox.boatcalculator.valueset.impl.TrampolineLeewardWing;
import com.github.thomasfox.boatcalculator.valueset.impl.TrampolineWindwardWing;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Determines the Crew Lever and the windward heel angle for a moth
 */
@ToString
@Slf4j
public class MothRideoutHeelAngleStrategy implements StepComputationStrategy
{
  private static final int ITERATION_CUTOFF = 50;

  private static final double LIFT_COEFFICIENT_3D_MAX_VALUE = 1.55d;

  private final QuantityRelationsCalculator quantityRelationsCalculator = new QuantityRelationsCalculator();

  private final int stepsToWait = 0;

  @Override
  public boolean step(ValuesAndCalculationRules allValues)
  {
    ValueSet globalValues = allValues.getValueSetNonNull(BoatGlobalValues.ID);
    ValueSet rigg = allValues.getValueSetNonNull(Rigg.ID);
    ValueSet crew = allValues.getValueSetNonNull(Crew.ID);
    ValueSet leverSailDaggerboard = allValues.getValueSetNonNull(LeverSailDaggerboard.ID);
    ValueSet trampolineWing1 = allValues.getValueSetNonNull(TrampolineLeewardWing.ID);
    ValueSet trampolineWing2 = allValues.getValueSetNonNull(TrampolineWindwardWing.ID);

    PhysicalQuantityValue liftCoefficient3D = rigg.getKnownQuantityValue(PhysicalQuantity.LIFT_COEFFICIENT_3D);
    PhysicalQuantityValue crewWeight = crew.getKnownQuantityValue(PhysicalQuantity.WEIGHT);
    PhysicalQuantityValue crewLeverWeight = crew.getKnownQuantityValue(PhysicalQuantity.LEVER_WEIGHT);
    PhysicalQuantityValue maxCrewLeverWeight = crew.getKnownQuantityValue(PhysicalQuantity.MAX_LEVER_WEIGHT);
    PhysicalQuantityValue leverSailDggerboardLever = leverSailDaggerboard.getKnownQuantityValue(PhysicalQuantity.LEVER_BETWEEN_FORCES);
    PhysicalQuantityValue maxWindwardHeelAngle = globalValues.getKnownQuantityValue(PhysicalQuantity.MAX_WINDWARD_HEEL_ANGLE);
    PhysicalQuantityValue windwardHeelAngle = globalValues.getKnownQuantityValue(PhysicalQuantity.WINDWARD_HEEL_ANGLE);
    PhysicalQuantityValue trampolineWing1Torque = trampolineWing1.getKnownQuantityValue(PhysicalQuantity.TORQUE_BETWEEN_FORCES);
    PhysicalQuantityValue trampolineWing2Torque = trampolineWing2.getKnownQuantityValue(PhysicalQuantity.TORQUE_BETWEEN_FORCES);
    if (maxWindwardHeelAngle == null
        || crewWeight == null
        || maxCrewLeverWeight == null
        || leverSailDggerboardLever == null
        || trampolineWing1Torque == null
        || trampolineWing2Torque == null
        || (liftCoefficient3D != null && !liftCoefficient3D.isTrial())
        || (windwardHeelAngle != null && !windwardHeelAngle.isTrial())
        || (crewLeverWeight != null && !crewLeverWeight.isTrial()))
    {
      if (windwardHeelAngle == null)
      {
        windwardHeelAngle = new SimplePhysicalQuantityValue(
            PhysicalQuantity.WINDWARD_HEEL_ANGLE,
            0d);
        globalValues.setCalculatedValueNoOverwrite(
            windwardHeelAngle,
            getClass().getSimpleName() + " trial Value",
            true);

      }
      return false;
    }

    for (int i = 0; i < ITERATION_CUTOFF; i++)
    {
      if  (liftCoefficient3D == null)
      {
        liftCoefficient3D = new SimplePhysicalQuantityValue(
            PhysicalQuantity.LIFT_COEFFICIENT_3D,
            LIFT_COEFFICIENT_3D_MAX_VALUE);
        rigg.setCalculatedValueNoOverwrite(
            liftCoefficient3D,
            getClass().getSimpleName() + " trial Value",
            true);
      }
      quantityRelationsCalculator.applyQuantityRelations(rigg);
      new LiftCalculator().apply(rigg);
      // the following is needed because otherwise LiftCoefficient3DFromLiftCoefficientCalculator
      // overwrites liftCoefficient3D again with an outdated value
      new LiftCoefficientFromLiftCoefficient3DCalculator().apply(rigg);
      new TotalDragCalculator().apply(rigg);
      new LateralForceCalculator().apply(rigg);

      new QuantityEquality(
          PhysicalQuantity.LATERAL_FORCE, Rigg.ID,
          PhysicalQuantity.FORCE, LeverSailDaggerboard.ID).step(allValues);

      new TorqueCalculator().apply(leverSailDaggerboard);

      PhysicalQuantityValue sailTorque = leverSailDaggerboard.getKnownQuantityValue(PhysicalQuantity.TORQUE_BETWEEN_FORCES);
      if (sailTorque == null)
      {
        return false;
      }
      double sailAndTrampolineTorque = sailTorque.getValue()
          + trampolineWing1Torque.getValue()
          + trampolineWing2Torque.getValue();
      double crewLever = sailAndTrampolineTorque / crewWeight.getValue();
      if (crewLever < maxCrewLeverWeight.getValue() && LIFT_COEFFICIENT_3D_MAX_VALUE == liftCoefficient3D.getValue())
      {
        allValues.setCalculatedValueNoOverwrite(
            new PhysicalQuantityInSet(PhysicalQuantity.LEVER_WEIGHT, crew.getId()),
            crewLever,
            MothRideoutHeelAngleStrategy.class.getSimpleName(),
            true,
            new SimplePhysicalQuantityValueWithSetId(sailTorque, leverSailDaggerboard.getId()),
            new SimplePhysicalQuantityValueWithSetId(trampolineWing1Torque, trampolineWing1.getId()),
            new SimplePhysicalQuantityValueWithSetId(trampolineWing2Torque, trampolineWing2.getId()),
            new SimplePhysicalQuantityValueWithSetId(crewWeight, crew.getId()));
        allValues.setCalculatedValueNoOverwrite(
            new PhysicalQuantityInSet(PhysicalQuantity.WINDWARD_HEEL_ANGLE, BoatGlobalValues.ID),
            0d,
            MothRideoutHeelAngleStrategy.class.getSimpleName(),
            true,
            new SimplePhysicalQuantityValueWithSetId(sailTorque, leverSailDaggerboard.getId()),
            new SimplePhysicalQuantityValueWithSetId(trampolineWing1Torque, trampolineWing1.getId()),
            new SimplePhysicalQuantityValueWithSetId(trampolineWing2Torque, trampolineWing2.getId()),
            new SimplePhysicalQuantityValueWithSetId(crewWeight, crew.getId()));
        return false;
      }
      else
      {
        PhysicalQuantityValue crewCenterOfEffort = crew.getKnownQuantityValue(PhysicalQuantity.CENTER_OF_EFFORT_HEIGHT);
        PhysicalQuantityValue boatCenterOfEffortHeight
            = globalValues.getKnownQuantityValue(PhysicalQuantity.CENTER_OF_EFFORT_HEIGHT);
        PhysicalQuantityValue boatWeight
            = globalValues.getKnownQuantityValue(PhysicalQuantity.WEIGHT);
        PhysicalQuantityValue daggerboardCenterOfEffortHeight
            = allValues.getValueSetNonNull(DaggerboardOrKeel.ID).getKnownQuantityValue(PhysicalQuantity.CENTER_OF_EFFORT_HEIGHT);
        if (crewCenterOfEffort == null
            || daggerboardCenterOfEffortHeight == null
            || boatCenterOfEffortHeight == null
            || boatWeight == null)
        {
          return false;
        }
        double crewVerticalLever = crewCenterOfEffort.getValue() + daggerboardCenterOfEffortHeight.getValue();
        double boatVerticalLever = boatCenterOfEffortHeight.getValue() + daggerboardCenterOfEffortHeight.getValue();
        double verticalTorque = crewVerticalLever * crewWeight.getValue() + boatVerticalLever * boatWeight.getValue();
        double horizontalTorque = crewWeight.getValue() * maxCrewLeverWeight.getValue();
        double totalTorque = Math.sqrt(verticalTorque*verticalTorque + horizontalTorque*horizontalTorque);

        double maxAngleRad = maxWindwardHeelAngle.getValue()*Math.PI/180;
        double maxPossibleTorque = horizontalTorque*Math.cos(maxAngleRad)+verticalTorque*Math.sin(maxAngleRad);

        CompareWithOldResult torqueDifference
            = new CompareWithOldResult(sailAndTrampolineTorque, maxPossibleTorque);
        boolean converged = torqueDifference.relativeDifferenceIsBelowThreshold();
        if (converged)
        {
          allValues.setCalculatedValueNoOverwrite(
              new PhysicalQuantityInSet(PhysicalQuantity.LEVER_WEIGHT, crew.getId()),
              crewLever,
              MothRideoutHeelAngleStrategy.class.getSimpleName(),
              true,
              new SimplePhysicalQuantityValueWithSetId(sailTorque, leverSailDaggerboard.getId()),
              new SimplePhysicalQuantityValueWithSetId(trampolineWing1Torque, trampolineWing1.getId()),
              new SimplePhysicalQuantityValueWithSetId(trampolineWing2Torque, trampolineWing2.getId()),
              new SimplePhysicalQuantityValueWithSetId(crewWeight, crew.getId()));
          allValues.setCalculatedValueNoOverwrite(
              new PhysicalQuantityInSet(PhysicalQuantity.WINDWARD_HEEL_ANGLE, BoatGlobalValues.ID),
              maxWindwardHeelAngle.getValue(),
              MothRideoutHeelAngleStrategy.class.getSimpleName(),
              true,
              new SimplePhysicalQuantityValueWithSetId(sailTorque, leverSailDaggerboard.getId()),
              new SimplePhysicalQuantityValueWithSetId(trampolineWing1Torque, trampolineWing1.getId()),
              new SimplePhysicalQuantityValueWithSetId(trampolineWing2Torque, trampolineWing2.getId()),
              new SimplePhysicalQuantityValueWithSetId(crewWeight, crew.getId()));
          return false;
        }

        if (sailAndTrampolineTorque < totalTorque && LIFT_COEFFICIENT_3D_MAX_VALUE == liftCoefficient3D.getValue())
        {
          // there is a heel angle where a force balance is reached
          // horizontalTorque*sin(lambda)+verticalTorque*cos(lambda) = totalTorque*cos(lambda-phaseShift)
          double phaseShiftRad = Math.atan(verticalTorque/horizontalTorque);
          double lambdaMinusPhaseShift = Math.acos(sailAndTrampolineTorque/totalTorque);
          double heelAngleRad = (lambdaMinusPhaseShift + phaseShiftRad);
          if (heelAngleRad > phaseShiftRad)
          {
            heelAngleRad = 2 * phaseShiftRad - heelAngleRad;
          }
          double heelAngle = heelAngleRad * 180 / Math.PI;
          if (heelAngle < maxWindwardHeelAngle.getValue())
          {
            allValues.setCalculatedValueNoOverwrite(
                new PhysicalQuantityInSet(PhysicalQuantity.LEVER_WEIGHT, crew.getId()),
                crewLever,
                MothRideoutHeelAngleStrategy.class.getSimpleName(),
                true,
                new SimplePhysicalQuantityValueWithSetId(sailTorque, leverSailDaggerboard.getId()),
                new SimplePhysicalQuantityValueWithSetId(trampolineWing1Torque, trampolineWing1.getId()),
                new SimplePhysicalQuantityValueWithSetId(trampolineWing2Torque, trampolineWing2.getId()),
                new SimplePhysicalQuantityValueWithSetId(crewWeight, crew.getId()));
            allValues.setCalculatedValueNoOverwrite(
                new PhysicalQuantityInSet(PhysicalQuantity.WINDWARD_HEEL_ANGLE, BoatGlobalValues.ID),
                heelAngle,
                MothRideoutHeelAngleStrategy.class.getSimpleName(),
                true,
                new SimplePhysicalQuantityValueWithSetId(sailTorque, leverSailDaggerboard.getId()),
                new SimplePhysicalQuantityValueWithSetId(trampolineWing1Torque, trampolineWing1.getId()),
                new SimplePhysicalQuantityValueWithSetId(trampolineWing2Torque, trampolineWing2.getId()),
                new SimplePhysicalQuantityValueWithSetId(crewWeight, crew.getId()));
            return false;
          }
        }
        double newLiftCoefficient3D = liftCoefficient3D.getValue() * maxPossibleTorque / sailAndTrampolineTorque;
        if (newLiftCoefficient3D > LIFT_COEFFICIENT_3D_MAX_VALUE)
        {
          newLiftCoefficient3D = LIFT_COEFFICIENT_3D_MAX_VALUE;
        }
        liftCoefficient3D = new SimplePhysicalQuantityValue(
            PhysicalQuantity.LIFT_COEFFICIENT_3D,
            newLiftCoefficient3D);
        rigg.setCalculatedValueNoOverwrite(
            liftCoefficient3D,
            getClass().getSimpleName() + " trial Value",
            true);

      }

    }
    log.warn("No convergence after " + ITERATION_CUTOFF + " iterations");
    return true;
  }

  @Override
  public Set<PhysicalQuantityInSet> getOutputs()
  {
    Set<PhysicalQuantityInSet> result = new HashSet<>();
    result.add(new PhysicalQuantityInSet(PhysicalQuantity.LEVER_WEIGHT, Crew.ID));
    result.add(new PhysicalQuantityInSet(PhysicalQuantity.LIFT_COEFFICIENT_3D, Rigg.ID));
    result.add(new PhysicalQuantityInSet(PhysicalQuantity.WINDWARD_HEEL_ANGLE, BoatGlobalValues.ID));
    return result;
  }

  @Override
  public Set<PhysicalQuantityInSet> getInputs()
  {
    Set<PhysicalQuantityInSet> result = new HashSet<>();
    return result;
  }

}
