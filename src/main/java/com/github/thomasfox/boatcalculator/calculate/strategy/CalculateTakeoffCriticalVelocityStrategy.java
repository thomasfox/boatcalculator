package com.github.thomasfox.boatcalculator.calculate.strategy;

import java.util.HashSet;
import java.util.Set;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.valueset.ValuesAndCalculationRules;
import com.github.thomasfox.boatcalculator.valueset.impl.BoatGlobalValues;
import com.github.thomasfox.boatcalculator.valueset.impl.Hull;
import com.github.thomasfox.boatcalculator.valueset.impl.Rigg;
import com.github.thomasfox.boatcalculator.valueset.impl.Takeoff;

import lombok.AllArgsConstructor;

public class CalculateTakeoffCriticalVelocityStrategy implements ComputationStrategy
{
  private static final double WIND_SPEED_STEP = 2d;

  private static final double WIND_SPEED_CUTOFF = 15.05d;

  private static final double WIND_SPEED_RESOLUTION = 0.01;

  private static final double BOAT_SPEED_COARSE_STEP = 0.25d;

  private static final double BOAT_SPEED_FINE_STEP = 0.1d;

  private static final double BOAT_SPEED_CUTOFF = 15.05d;

  private static PhysicalQuantityInSet TAKEOFF_BOAT_SPEED
      = new PhysicalQuantityInSet(PhysicalQuantity.VELOCITY, Takeoff.ID);

  private static PhysicalQuantityInSet TAKEOFF_WIND_SPEED
      = new PhysicalQuantityInSet(PhysicalQuantity.WIND_SPEED, Takeoff.ID);

   private static PhysicalQuantityInSet POINTING_ANGLE
       = new PhysicalQuantityInSet(PhysicalQuantity.POINTING_ANGLE, BoatGlobalValues.ID);

   private static PhysicalQuantityInSet WIND_SPEED
       = new PhysicalQuantityInSet(PhysicalQuantity.WIND_SPEED, BoatGlobalValues.ID);

   private static PhysicalQuantityInSet BOAT_SPEED
       = new PhysicalQuantityInSet(PhysicalQuantity.VELOCITY, BoatGlobalValues.ID);

   private static PhysicalQuantityInSet BOAT_DRAG
       = new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, BoatGlobalValues.ID);

   private static PhysicalQuantityInSet RIGG_DRIVING_FORCE
       = new PhysicalQuantityInSet(PhysicalQuantity.DRIVING_FORCE, Rigg.ID);

   private static PhysicalQuantityInSet HULL_DRAG
       = new PhysicalQuantityInSet(PhysicalQuantity.TOTAL_DRAG, Hull.ID);

  public boolean calculateAndSetValue(ValuesAndCalculationRules allValues)
  {
    if (allValues.getValueSet(Takeoff.ID) == null)
    {
      return false;
    }
    if (allValues.isValueKnown(TAKEOFF_BOAT_SPEED))
    {
      return false;
    }
    if (allValues.isValueKnown(TAKEOFF_WIND_SPEED))
    {
      return false;
    }

    if (!allValues.isValueKnown(POINTING_ANGLE))
    {
      return false;
    }

    Boolean takeoffAtLastWindVelocity = null;
    double lastWindSpeed = 0d;
    for (double windSpeed = WIND_SPEED_STEP; windSpeed < WIND_SPEED_CUTOFF; windSpeed += WIND_SPEED_STEP)
    {
      BoatSpeedCalculationResult calculationResult = calculateBoatSpeed(allValues, windSpeed, BOAT_SPEED_COARSE_STEP / 2.5);
      if (Boolean.TRUE.equals(calculationResult.takeoff))
      {
        if (Boolean.FALSE.equals(takeoffAtLastWindVelocity))
        {
          WindSpeedCalculationResult takeoff = refineTakeoffWindSpeed(lastWindSpeed, windSpeed, allValues);
          System.out.println("Takeoff at wind speed " + takeoff.windSpeed + " and boat speed " + takeoff.boatSpeed);
          allValues.setCalculatedValueNoOverwrite(TAKEOFF_BOAT_SPEED, takeoff.boatSpeed, "takeoffCalculator", false);
          allValues.setCalculatedValueNoOverwrite(TAKEOFF_WIND_SPEED, takeoff.windSpeed, "takeoffCalculator", false);
          return true;
        }
        else
        {
          System.out.println("Takeoff calculation failed");
          return false;
        }
      }
      takeoffAtLastWindVelocity = calculationResult.takeoff;
      lastWindSpeed = windSpeed;
    }
    return false;
  }

  private WindSpeedCalculationResult refineTakeoffWindSpeed(
      double lowerWindSpeedWithoutTakeoff,
      double upperWindSpeedWithTakeoff,
      ValuesAndCalculationRules allValues)
  {
    double middleWindSpeed = (upperWindSpeedWithTakeoff + lowerWindSpeedWithoutTakeoff) / 2;
    if (upperWindSpeedWithTakeoff - lowerWindSpeedWithoutTakeoff < WIND_SPEED_RESOLUTION)
    {
      BoatSpeedCalculationResult calculationResult = calculateBoatSpeed(allValues, middleWindSpeed, BOAT_SPEED_FINE_STEP);
      return new WindSpeedCalculationResult(middleWindSpeed, calculationResult.boatSpeed);
    }
    BoatSpeedCalculationResult calculationResult = calculateBoatSpeed(allValues, middleWindSpeed, BOAT_SPEED_COARSE_STEP);
    if (calculationResult.takeoff == null)
    {
      return null;
    }
    else if (calculationResult.takeoff)
    {
      return refineTakeoffWindSpeed(lowerWindSpeedWithoutTakeoff, middleWindSpeed, allValues);
    }
    else
    {
      return refineTakeoffWindSpeed(middleWindSpeed, upperWindSpeedWithTakeoff, allValues);
    }
  }

  private BoatSpeedCalculationResult calculateBoatSpeed(ValuesAndCalculationRules allValues, double windSpeed, double speedStep)
  {
    System.out.println("wind speed " + windSpeed);
    Boolean takeoff = null;
    boolean lastCalculationSuccessful = false;
    double boatSpeed;
    for (boatSpeed = speedStep; boatSpeed < BOAT_SPEED_CUTOFF; boatSpeed += speedStep)
    {
//      System.out.println("boat speed " + boatSpeed);
      ValuesAndCalculationRules allValuesForCalculation = new ValuesAndCalculationRules(allValues);
      allValuesForCalculation.moveCalculatedValuesToStartValues();
      allValuesForCalculation.getValueSet(BoatGlobalValues.ID).getStartValues().remove(PhysicalQuantity.WIND_SPEED);
      allValuesForCalculation.getValueSet(BoatGlobalValues.ID).getStartValues().remove(PhysicalQuantity.VELOCITY);
      allValuesForCalculation.setStartValueNoOverwrite(WIND_SPEED, windSpeed);
      allValuesForCalculation.setStartValueNoOverwrite(BOAT_SPEED, boatSpeed);
      allValuesForCalculation.calculate(BOAT_DRAG, 1); // TODO is one step correct here
      if (!allValuesForCalculation.isValueKnown(BOAT_DRAG))
      {
        // try next value, perhaps velocity is too low for normal operation
        takeoff = null;
        lastCalculationSuccessful = false;
        continue;
      }
      if (!allValuesForCalculation.isValueKnown(RIGG_DRIVING_FORCE))
      {
        allValuesForCalculation.calculate(RIGG_DRIVING_FORCE, 1); // TODO is one step correct here
      }
      if (!allValuesForCalculation.isValueKnown(RIGG_DRIVING_FORCE))
      {
        // try next value, perhaps velocity is too low for normal operation
        takeoff = null;
        lastCalculationSuccessful = false;
        continue;
      }
      double accelerationForce = allValuesForCalculation.getKnownValue(RIGG_DRIVING_FORCE)
          - allValuesForCalculation.getKnownValue(BOAT_DRAG);
      if (accelerationForce > 0)
      {
        // boat too slow, we can still get faster
        Double hullDrag = allValuesForCalculation.getKnownValue(HULL_DRAG);
        if (lastCalculationSuccessful && hullDrag != null && hullDrag < 0.01)
        {
          takeoff = true;
          break;
        }
        lastCalculationSuccessful = true;
        continue;
      }
      // now we have reached the lowest velocity where we cannot accelerate more
      if (!lastCalculationSuccessful)
      {
        // could not calculate force equilibrium
        takeoff = null;
        break;
      }
      Double hullDrag = allValuesForCalculation.getKnownValue(HULL_DRAG);
      if (hullDrag == null)
      {
        takeoff = null;
        lastCalculationSuccessful = false;
        break;
      }
      takeoff = hullDrag < 0.01;
      break;
    }
    return new BoatSpeedCalculationResult(boatSpeed, takeoff);
  }

  @Override
  public Set<PhysicalQuantityInSet> getOutputs()
  {
    Set<PhysicalQuantityInSet> result = new HashSet<>();
    result.add(TAKEOFF_BOAT_SPEED);
    return result;
  }

  @Override
  public Set<PhysicalQuantityInSet> getInputs()
  {
    Set<PhysicalQuantityInSet> result = new HashSet<>();
    result.add(POINTING_ANGLE); // not sure whether this makes sense here
    return result;
  }

   @AllArgsConstructor
   private static class BoatSpeedCalculationResult
   {
     double boatSpeed;

     Boolean takeoff;
   }


   @AllArgsConstructor
   private static class WindSpeedCalculationResult
   {
     double windSpeed;

     double boatSpeed;
   }

   @Override
  public boolean step(ValuesAndCalculationRules allValues)
   {
     // TODO implement
     return true;
   }
}
