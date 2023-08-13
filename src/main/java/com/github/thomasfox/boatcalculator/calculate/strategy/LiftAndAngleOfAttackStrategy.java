package com.github.thomasfox.boatcalculator.calculate.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.github.thomasfox.boatcalculator.calculate.CompareWithOldResult;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.calculate.impl.LiftCalculator;
import com.github.thomasfox.boatcalculator.calculate.impl.LiftCoefficient3DFromLiftCoefficientCalculator;
import com.github.thomasfox.boatcalculator.interpolate.QuantityRelationsCalculator;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;
import com.github.thomasfox.boatcalculator.valueset.ValuesAndCalculationRules;
import com.github.thomasfox.boatcalculator.valueset.impl.Hull;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Lift of a foil is calculated by limiting it by an associated weight.
 * If the lift exceeds the weight, the angle of attack is reduced.
 * If the lift is smaller than the weight, the angle of attack is increased
 * to a limit.
 * If the limit for the angle of attack is reached, the remaining weight
 * is set for the hull.
 *
 * So this strategy sets the hull weight,
 * plus lift, lift coefficient3D, lift coefficient and angle of attack of the
 * associated lifting foils.
 */
@Getter
@ToString
@Slf4j
public class LiftAndAngleOfAttackStrategy implements StepComputationStrategy
{
  private static final int ITERATION_CUTOFF = 50;

  @NonNull
  private final PhysicalQuantityInSet[] weightSources;

  @NonNull
  private final List<ValueSet> cosineOfHeelAngleLiftValueSets;

  @NonNull
  private final List<ValueSet> sineOfHeelAngleLiftValueSets;

  private final List<ValueSet> minusSineOfHeelAngleLiftValueSets;

  @NonNull
  private final PhysicalQuantityInSet heelAngle;

  @NonNull
  private final PhysicalQuantityInSet maxAngleOfAttack;

  private final LiftCoefficient3DFromLiftCoefficientCalculator liftCoefficient3DFromLiftCoefficientCalculator
      = new LiftCoefficient3DFromLiftCoefficientCalculator();

  private final LiftCalculator liftCalculator = new LiftCalculator();

  private final QuantityRelationsCalculator quantityRelationsCalculator = new QuantityRelationsCalculator();

  /**
   *
   * @param weightSources all PhysicalQuantityInSets which add to the global weight force.
   * @param cosineOfHeelAngleLiftValueSets all value sets which lift counterbalances weight
   *        proportional to the cosine of the windward heel angle.
   *        These value sets are affected by the angle of attach which is set by this strategy.
   * @param sineOfHeelAngleLiftValueSets all value sets which lift counterbalances weight
   *        proportional to the sine of the windward heel angle.
   *        These value sets are NOT affected by the angle of attach which is set by this strategy.
   * @param minusSineOfHeelAngleLiftValueSets all value sets which lift adds to the weight
   *        proportional to the sine of the windward heel angle.
   *        These value sets are NOT affected by the angle of attach which is set by this strategy.
   * @param maxAngleOfAttack source of the the maximum possible angle of attack
   * @param heelAngle source of the the windward heel angle.
   */
  public LiftAndAngleOfAttackStrategy(
      @NonNull PhysicalQuantityInSet[] weightSources,
      @NonNull ValueSet[] cosineOfHeelAngleLiftValueSets,
      @NonNull ValueSet[] sineOfHeelAngleLiftValueSets,
      @NonNull ValueSet[] minusSineOfHeelAngleLiftValueSets,
      @NonNull PhysicalQuantityInSet maxAngleOfAttack,
      @NonNull PhysicalQuantityInSet heelAngle)
  {
    this.weightSources = weightSources;
    this.cosineOfHeelAngleLiftValueSets = Arrays.asList(cosineOfHeelAngleLiftValueSets);
    this.sineOfHeelAngleLiftValueSets = Arrays.asList(sineOfHeelAngleLiftValueSets);
    this.minusSineOfHeelAngleLiftValueSets = Arrays.asList(minusSineOfHeelAngleLiftValueSets);
    this.heelAngle = heelAngle;
    this.maxAngleOfAttack = maxAngleOfAttack;
    checkUnitsOfWeightSources();
  }

  private void checkUnitsOfWeightSources()
  {
    String targetUnit = PhysicalQuantity.WEIGHT.getUnit();
    for (PhysicalQuantityInSet source : weightSources)
    {
      if (!Objects.equals(source.getPhysicalQuantity().getUnit(), targetUnit))
      {
        throw new IllegalArgumentException(
            "Source " + source.getPhysicalQuantity().getDescription()
              + " has wrong unit, must be equal to " + targetUnit);
      }
    }
  }

  private String getCalculatedByDescription(ValuesAndCalculationRules allValues)
  {
    StringBuilder result = new StringBuilder("LiftAndAngleOfAttackStrategy: ");
    for (PhysicalQuantityInSet source : weightSources)
    {
      if (result.length() > 0 )
      {
        result.append(",");
      }
      result.append(allValues.getNameOfSetWithId(source.getSetId()))
          .append(":")
          .append(source.getPhysicalQuantity().getDisplayName());
    }
    return result.toString();
  }

  private PhysicalQuantityValueWithSetId[] getWeightSourceValuesWithSetId(ValuesAndCalculationRules allValues)
  {
    PhysicalQuantityValueWithSetId[] result = new PhysicalQuantityValueWithSetId[weightSources.length];
    int i = 0;
    for (PhysicalQuantityInSet source : weightSources)
    {
      result[i] = new SimplePhysicalQuantityValueWithSetId(
          allValues.getKnownPhysicalQuantityValue(source),
          source.getSetId());
      ++i;
    }
    return result;
  }

  @Override
  public boolean step(ValuesAndCalculationRules allValues)
  {
    if (!allWeightSourceValuesAreKnown(allValues))
    {
      return false;
    }
    double weight = getSumOfWeightSourcesValues(allValues);

    Double maxAngleOfAttackValue = allValues.getKnownValue(maxAngleOfAttack);
    if (maxAngleOfAttackValue == null)
    {
      return false;
    }
    Double heelAngleValue = allValues.getKnownValue(heelAngle);
    if (heelAngleValue == null)
    {
      return false;
    }
    double cosineOfHeelAngle = Math.cos(heelAngleValue * Math.PI / 180);
    double sineOfHeelAngle = Math.sin(heelAngleValue * Math.PI / 180);

    double alreadyCalculatedVerticalLift = 0;
    Double trialAngleOfAttack = null;
    List<ValueSet> modifiableLiftValueSets = new ArrayList<>();
    List<ValueSet> allLiftValueSets = new ArrayList<>();
    allLiftValueSets.addAll(cosineOfHeelAngleLiftValueSets);
    allLiftValueSets.addAll(sineOfHeelAngleLiftValueSets);
    allLiftValueSets.addAll(minusSineOfHeelAngleLiftValueSets);
    for (ValueSet liftValueSet : allLiftValueSets)
    {
      PhysicalQuantityValue lift
          = liftValueSet.getKnownQuantityValue(PhysicalQuantity.LIFT);
      PhysicalQuantityValue angleOfAttack
              = liftValueSet.getKnownQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK);
      if (cosineOfHeelAngleLiftValueSets.contains(liftValueSet))
      {
        if ((angleOfAttack != null && !angleOfAttack.isTrial())
            || (lift != null && !lift.isTrial()))
        {
          if (lift == null)
          {
            return false;
          }
          alreadyCalculatedVerticalLift += lift.getValue() * cosineOfHeelAngle;
          continue;
        }
      }
      else
      {
        if (sineOfHeelAngle < 0.00001)
        {
          continue;
        }
        if (lift == null)
        {
          return false;
        }
        if (sineOfHeelAngleLiftValueSets.contains(liftValueSet))
        {
          alreadyCalculatedVerticalLift += lift.getValue() * sineOfHeelAngle;
        }
        else
        {
          alreadyCalculatedVerticalLift -= lift.getValue() * sineOfHeelAngle;
        }
        continue;
      }
      // now we know cosineOfHeelAngleLiftValueSets.contains(liftValueSet) and angleOfAttack.isTrial() == true
      if (angleOfAttack != null)
      {
        if (!angleOfAttack.isTrial())
        {
          continue;
        }
        if (trialAngleOfAttack == null)
        {
          trialAngleOfAttack = angleOfAttack.getValue();
        }
      }
      PhysicalQuantityValue lift3D
          = liftValueSet.getKnownQuantityValue(PhysicalQuantity.LIFT);
      if (lift3D != null && !lift3D.isTrial())
      {
        continue;
      }
      modifiableLiftValueSets.add(liftValueSet);
    }

    if (trialAngleOfAttack == null)
    {
      trialAngleOfAttack = maxAngleOfAttackValue;
    }

    double factor = 2;
    Double lastTrialValueDifference = null;

    for (int i = 0; i < ITERATION_CUTOFF; i++)
    {
      double modifiableLift = 0;
      for (ValueSet liftValueSet : modifiableLiftValueSets)
      {
        liftValueSet.setCalculatedValueNoOverwrite(
            new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, trialAngleOfAttack),
            getCalculatedByDescription(allValues),
            true,
            getWeightSourceValuesWithSetId(allValues));
        quantityRelationsCalculator.setQuantityRelations(liftValueSet.getQuantityRelations());
        quantityRelationsCalculator.applyQuantityRelations(liftValueSet);
        if (liftValueSet.getKnownQuantityValue(PhysicalQuantity.LIFT_COEFFICIENT) != null)
        {
          liftCoefficient3DFromLiftCoefficientCalculator.apply(liftValueSet);
          liftCalculator.apply(liftValueSet);
        }
        PhysicalQuantityValue lift = liftValueSet.getKnownQuantityValue(PhysicalQuantity.LIFT);
        if (lift == null)
        {
          return false;
        }
        modifiableLift += lift.getValue();
      }

      double totalLift = modifiableLift * cosineOfHeelAngle + alreadyCalculatedVerticalLift;
      CompareWithOldResult liftWeightDifference
          = new CompareWithOldResult(totalLift, weight);
      boolean converged = liftWeightDifference.relativeDifferenceIsBelowThreshold();
      if (converged
          || (totalLift < weight && trialAngleOfAttack.equals(maxAngleOfAttackValue)))
      {
        double hullLift = weight - totalLift;
        if (hullLift < 0
            || (converged && trialAngleOfAttack < maxAngleOfAttackValue))
        {
          hullLift = 0;
        }
        allValues.setCalculatedValueNoOverwrite(
            new PhysicalQuantityInSet(PhysicalQuantity.LIFT, Hull.ID),
            hullLift,
            getCalculatedByDescription(allValues),
            true,
            getWeightSourceValuesWithSetId(allValues));

        return false;
      }

      double trialValueDifference = -factor * (totalLift - weight) / weight;
      if (lastTrialValueDifference != null)
      {
        if (trialValueDifference * lastTrialValueDifference < 0d // they have different sign
          && Math.abs(trialValueDifference) > Math.abs(lastTrialValueDifference) * 0.5)
        {
          trialValueDifference = trialValueDifference * 0.5;
          factor *= 0.5;
        }
      }
      lastTrialValueDifference = trialValueDifference;
      trialAngleOfAttack += trialValueDifference;
      if (trialAngleOfAttack > maxAngleOfAttackValue)
      {
        trialAngleOfAttack = maxAngleOfAttackValue;
      }
    }
    log.warn("No convergence after " + ITERATION_CUTOFF + " iterations");
    return true;
  }

  private boolean allWeightSourceValuesAreKnown(ValuesAndCalculationRules allValues)
  {
    return Arrays.stream(weightSources).allMatch(allValues::isValueKnown);
  }

  private double getSumOfWeightSourcesValues(ValuesAndCalculationRules allValues)
  {
    double result = 0d;
    for (PhysicalQuantityInSet source : weightSources)
    {
      result += allValues.getKnownValue(source);
    }
    return result;
  }

  @Override
  public Set<PhysicalQuantityInSet> getOutputs()
  {
    Set<PhysicalQuantityInSet> result = new HashSet<>();
    for (ValueSet liftValueSet : cosineOfHeelAngleLiftValueSets)
    {
      String setId = liftValueSet.getId();
      result.add(new PhysicalQuantityInSet(PhysicalQuantity.ANGLE_OF_ATTACK, setId));
      result.add(new PhysicalQuantityInSet(PhysicalQuantity.LIFT, setId));
    }
    return result;
  }

  @Override
  public Set<PhysicalQuantityInSet> getInputs()
  {
    return new HashSet<>(Arrays.asList(weightSources));
  }
}
