package com.github.thomasfox.boatcalculator.valueset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.thomasfox.boatcalculator.calculate.Calculator;
import com.github.thomasfox.boatcalculator.calculate.CombinedCalculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.calculate.strategy.ComputationFromConvergedResultStrategy;
import com.github.thomasfox.boatcalculator.calculate.strategy.ComputationStrategy;
import com.github.thomasfox.boatcalculator.calculate.strategy.StepComputationStrategy;
import com.github.thomasfox.boatcalculator.interpolate.QuantityRelation;
import com.github.thomasfox.boatcalculator.value.CalculatedPhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.valueset.impl.BoatGlobalValues;
import com.github.thomasfox.boatcalculator.valueset.impl.Crew;
import com.github.thomasfox.boatcalculator.valueset.impl.Rigg;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Contains all valueSets for a boat, and the calculation strategies
 * which relate quantities in different valueSets.
 */
@NoArgsConstructor
@Slf4j
public class ValuesAndCalculationRules
{
  private final List<ValueSet> valueSets = new ArrayList<>();

  private final List<StepComputationStrategy> stepComputationStrategies = new ArrayList<>();

  private final List<ComputationFromConvergedResultStrategy> computationFromConvergedResultStrategies
      = new ArrayList<>();

  public ValuesAndCalculationRules(Set<ValueSet> valueSets)
  {
    this.valueSets.addAll(valueSets);
  }

  public ValuesAndCalculationRules(ValuesAndCalculationRules toCopy)
  {
    for (ValueSet valueSetToCopy : toCopy.valueSets)
    {
      this.valueSets.add(valueSetToCopy.clone());
    }
    this.stepComputationStrategies.addAll(toCopy.getStepComputationStrategies());
    this.computationFromConvergedResultStrategies.addAll(
        toCopy.getComputationFromConvergedResultStrategies());
  }

  public void add(ValueSet toAdd)
  {
    ValueSet withSameId = getValueSet(toAdd.getId());
    if (withSameId != null)
    {
      throw new IllegalArgumentException("valueSet with id " + withSameId
          + " exists already");
    }
    this.valueSets.add(toAdd);
  }

  public List<ValueSet> getValueSets()
  {
    return Collections.unmodifiableList(valueSets);
  }

  public boolean remove(ValueSet toRemove)
  {
    return valueSets.remove(toRemove);
  }

  public ValueSet getValueSet(String id)
  {
    return valueSets.stream()
        .filter(n -> n.getId().equals(id))
        .findAny().orElse(null);
  }

  public ValueSet getValueSetNonNull(String id)
  {
    return valueSets.stream()
        .filter(n -> n.getId().equals(id))
        .findAny().orElseThrow(() -> new IllegalStateException(
            "No valueSet with id " + id + " exists, existing valueSets are "
                + valueSets.stream().map(ValueSet::getId).collect(Collectors.toList())));
  }

  public PhysicalQuantityValue getKnownPhysicalQuantityValue(PhysicalQuantityInSet toResolve)
  {
    if (toResolve == null)
    {
      return null;
    }
    ValueSet sourceSet = getValueSetNonNull(toResolve.getSetId());
    PhysicalQuantityValue knownValue = sourceSet.getKnownQuantityValue(toResolve.getPhysicalQuantity());
    return knownValue;
  }

  public Double getKnownValue(PhysicalQuantityInSet toResolve)
  {
    PhysicalQuantityValue knownValue = getKnownPhysicalQuantityValue(toResolve);
    if (knownValue == null)
    {
      return null;
    }
    return knownValue.getValue();
  }

  public String getName(PhysicalQuantityInSet toBeNamed)
  {
    return getSetName(toBeNamed) + ":" + toBeNamed.getPhysicalQuantity().getDisplayName();
  }

  public String getSetName(PhysicalQuantityInSet toBeNamed)
  {
    return getValueSetNonNull(toBeNamed.getSetId()).getDisplayName();
  }

  public boolean isValueKnown(PhysicalQuantityInSet toCheck)
  {
    Double value = getKnownValue(toCheck);
    return value != null;
  }

  public void setStartValueNoOverwrite(
      PhysicalQuantityInSet target,
      double value)
  {
    ValueSet targetSet = getValueSetNonNull(target.getSetId());
    targetSet.setStartValueNoOverwrite(
        new SimplePhysicalQuantityValue(target.getPhysicalQuantity(), value));
  }

  public void setCalculatedValueNoOverwrite(
      PhysicalQuantityInSet target,
      double value,
      String calculatedBy,
      boolean trialValue,
      PhysicalQuantityValueWithSetId... calculatedFrom)
  {
    ValueSet targetSet = getValueSetNonNull(target.getSetId());
    targetSet.setCalculatedValueNoOverwrite(
        new SimplePhysicalQuantityValue(target.getPhysicalQuantity(), value),
        calculatedBy,
        trialValue,
        calculatedFrom);
  }

  public String getNameOfSetWithId(String setId)
  {
    return valueSets.stream()
        .filter(n -> n.getId().equals(setId))
        .map(s -> s.getDisplayName())
        .findAny().orElseThrow(() -> new IllegalStateException(
            "No valueSet with id " + setId + " exists, existing valueSets are "
                + valueSets.stream().map(ValueSet::getId).collect(Collectors.toList())));
  }

  public void add(StepComputationStrategy computationStrategy)
  {
    stepComputationStrategies.add(computationStrategy);
  }

  public List<StepComputationStrategy> getStepComputationStrategies()
  {
    return Collections.unmodifiableList(stepComputationStrategies);
  }

  public boolean remove(StepComputationStrategy computationStrategy)
  {
    return stepComputationStrategies.remove(computationStrategy);
  }

  public void add(ComputationFromConvergedResultStrategy computationStrategy)
  {
    computationFromConvergedResultStrategies.add(computationStrategy);
  }

  public List<ComputationFromConvergedResultStrategy> getComputationFromConvergedResultStrategies()
  {
    return Collections.unmodifiableList(computationFromConvergedResultStrategies);
  }

  public boolean remove(ComputationFromConvergedResultStrategy computationStrategy)
  {
    return computationFromConvergedResultStrategies.remove(computationStrategy);
  }

  public List<ComputationStrategy> getAllComputationStrategies()
  {
    List<ComputationStrategy> result = new ArrayList<>();
    result.addAll(stepComputationStrategies);
    result.addAll(computationFromConvergedResultStrategies);
    return Collections.unmodifiableList(result);
  }


  /**
   * Calculate unknown quantities from the known quantities
   * and the calculation rules. The calculated quantities are stored as
   * calculated quantities in the value sets.
   *
   * @param wanted the quantity to calculate, or null to calculate all
   *        quantities than can be calculated.
   *
   * @return true if the calculation converged, false otherwise
   */
  public boolean calculate(PhysicalQuantityInSet wanted, int steps)
  {
    if (computationFromConvergedResultStrategies.isEmpty())
    {
      return calculateUntilConvergence(wanted, steps);
    }

    List<ComputationFromConvergedResultStrategy> strategiesToApply
        = new ArrayList<>(computationFromConvergedResultStrategies);
    return applyComputationFromConvergedResultStrategies(strategiesToApply, new ArrayList<>(), wanted, steps);
  }

  private boolean applyComputationFromConvergedResultStrategies(
      List<ComputationFromConvergedResultStrategy> toApply,
      List<ComputationFromConvergedResultStrategy> toSetStartValues,
      PhysicalQuantityInSet wanted,
      int steps)
  {
    if (toApply.isEmpty())
    {
      return true;
    }
    ComputationFromConvergedResultStrategy computationStrategy = toApply.remove(0);
    toSetStartValues.add(computationStrategy);
    int step = 0;
    boolean furtherCalculationIsNeeded;
    computationStrategy.reset();
    do
    {
      clearCalculatedValues();
      for (ComputationFromConvergedResultStrategy toSetStartValuesStrategy : toSetStartValues)
      {
        toSetStartValuesStrategy.setStartValues(this);
      }
      calculateUntilConvergence(wanted, steps);
      furtherCalculationIsNeeded = computationStrategy.stepAfterConvergence(this);
      if (!applyComputationFromConvergedResultStrategies(toApply, toSetStartValues, wanted, steps))
      {
        return false;
      }
      step++;
    }
    while (furtherCalculationIsNeeded && step < steps && !isValueKnown(wanted));
    return !furtherCalculationIsNeeded;
  }

  private boolean calculateUntilConvergence(PhysicalQuantityInSet wanted, int steps)
  {
    // allow computation strategies to reset their internal state
    computationStrategiesStep();

    if (wanted != null)
    {
      ValueSet valueSet = getValueSet(wanted.getSetId());
      {
        valueSet.calculateSinglePass(this, wanted.getPhysicalQuantity(), -1);
      }
      if (valueSet.isValueKnown(wanted.getPhysicalQuantity()))
      {
        return true;
      }
    }
    int step = 0;
    Set<String> changedInStep = new HashSet<>();
    do
    {
      changedInStep.clear();
      for (ValueSet valueSet : valueSets)
      {
        PhysicalQuantity wantedPhysicalQuantity = null;
        if (wanted != null && wanted.getSetId().equals(valueSet.getId()))
        {
          wantedPhysicalQuantity = wanted.getPhysicalQuantity();
        }
        Set<String> partChanged = valueSet.calculateSinglePass(this, wantedPhysicalQuantity, step);
        changedInStep.addAll(partChanged);
      }
      log.debug("changed is " + changedInStep + " after calculating in valueSets in step " + step);
      Set<String> changedByComputationStrategies = computationStrategiesStep();
      changedInStep.addAll(changedByComputationStrategies);
      log.debug("changedInStep is "
          + (changedInStep.isEmpty() ? "empty" : "not empty")
          + " after applying computation strategies in step " + step);

      step++;
    }
    while (!changedInStep.isEmpty() && step < steps && !isValueKnown(wanted));
    System.out.println("Calculation completed in " + step + " steps");

    if (step >= steps)
    {
      printLongestComputationPaths();
      System.out.println("- Wind speed: "
          + getKnownPhysicalQuantityValue(new PhysicalQuantityInSet(PhysicalQuantity.WIND_SPEED, BoatGlobalValues.ID)));
      System.out.println("- Boat velocity: "
          + getKnownPhysicalQuantityValue(new PhysicalQuantityInSet(PhysicalQuantity.VELOCITY, BoatGlobalValues.ID)));
      System.out.println("- Crew lever: "
          + getKnownPhysicalQuantityValue(new PhysicalQuantityInSet(PhysicalQuantity.LEVER_WEIGHT, Crew.ID)));
      System.out.println("- Rigg: "
          + getKnownPhysicalQuantityValue(new PhysicalQuantityInSet(PhysicalQuantity.LIFT_COEFFICIENT_3D, Rigg.ID)));
      System.out.println("- Drift angle: "
          + getKnownPhysicalQuantityValue(new PhysicalQuantityInSet(PhysicalQuantity.DRIFT_ANGLE, BoatGlobalValues.ID)));

      System.out.println("- changed parts: " + changedInStep);
      return false;
    }
    return true;
  }

  private Set<String> computationStrategiesStep()
  {
    Set<String> changed = new HashSet<>();
    for (StepComputationStrategy computationStrategy : stepComputationStrategies)
    {
      boolean changedinStrategy = computationStrategy.step(this);

      if (changedinStrategy)
      {
        changed.add(computationStrategy.getClass().getSimpleName());
      }
    }
    return changed;
  }

  public void clearCalculatedValues()
  {
    for (ValueSet set : valueSets)
    {
      set.clearCalculatedValues();
    }
  }

  public void moveCalculatedValuesToStartValues()
  {
    for (ValueSet set : valueSets)
    {
      set.moveCalculatedValuesToStartValues();
    }
  }

  public void printLongestComputationPaths()
  {
    Set<PhysicalQuantityValueWithSetId> excluded = new HashSet<>();
    for (ValueSet set : valueSets)
    {
      for (CalculatedPhysicalQuantityValue calculatedValue : set.getCalculatedValues().getAsList())
      {
        for (PhysicalQuantityValueWithSetId sourceQuantity : calculatedValue.getCalculatedFromAsList())
        {
          excluded.add(sourceQuantity);
        }
      }
    }

    for (ValueSet set : valueSets)
    {
      for (CalculatedPhysicalQuantityValue calculatedValue : set.getCalculatedValues().getAsList())
      {
        if (excluded.contains(calculatedValue)) // TODO check
        {
          continue;
        }
        log.info("Calculation path for " + set.getId() + ":" + calculatedValue.getPhysicalQuantity().getDisplayName());
        printComputationPath(calculatedValue, "  ", 10);
      }
    }
  }

  public void printComputationPath(CalculatedPhysicalQuantityValue value, String indent, int maxDepth)
  {
    if (maxDepth < 0)
    {
      return;
    }
    for (PhysicalQuantityValueWithSetId sourceQuantity : value.getCalculatedFromAsList())
    {
      ValueSet set = getValueSetNonNull(sourceQuantity.getSetId());
      CalculatedPhysicalQuantityValue calculatedFrom
          = set.getCalculatedValues().getPhysicalQuantityValue(sourceQuantity.getPhysicalQuantity());
      if (calculatedFrom != null)
      {
        log.info(indent + sourceQuantity.getSetId() + ":" + sourceQuantity.getPhysicalQuantity().getDisplayName());
        printComputationPath(calculatedFrom, indent + "  ", maxDepth - 1);
      }
    }
  }

  public void logState()
  {
    for (ValueSet valueSet : getValueSets())
    {
      for (PhysicalQuantityValue value : valueSet.getStartValues().getAsList())
      {
        log.info("  Start value: "+ valueSet.getId() + ":" + value.getPhysicalQuantity().getDisplayName() + "=" + value.getValue());
      }
    }
    for (ValueSet valueSet : getValueSets())
    {
      for (PhysicalQuantityValue value : valueSet.getCalculatedValues().getAsList())
      {
        log.info("  Calculated value: "+ valueSet.getId() + ":" + value.getPhysicalQuantity().getDisplayName() + "=" + value.getValue());
      }
    }
  }

  public CalculationTreeNode getCalculationTree(PhysicalQuantityInSet root, int cutoff)
  {
    CalculationTreeNode rootNode = new CalculationTreeNode(new PhysicalQuantityInSet(root.getPhysicalQuantity(), root.getSetId()));
    rootNode.setKnownValue(getKnownValue(rootNode.getCalculatedQuantity()));
    fillCalculationTree(rootNode, cutoff, new HashSet<>());
    return rootNode;
  }

  public void fillCalculationTree(CalculationTreeNode toFill, int cutoff, Set<PhysicalQuantityInSet> alreadyRequiredInTree)
  {
    if (cutoff <=0)
    {
      return;
    }
    if (toFill.getKnownValue() != null)
    {
      return;
    }
    PhysicalQuantityInSet soughtValue = toFill.getCalculatedQuantity();
    Set<PhysicalQuantityInSet> requiredForInputNodes = new HashSet<>(alreadyRequiredInTree);
    requiredForInputNodes.add(soughtValue);
    for (ValueSet valueSet : getValueSets())
    {
      CombinedCalculator combinedCalculator = new CombinedCalculator();
      if (valueSet.getId().equals(soughtValue.getSetId()))
      {
        List<Calculator> calculatorsWithOutput = combinedCalculator.getCalculatorsWithOutput(soughtValue.getPhysicalQuantity());
        for (Calculator calculator : calculatorsWithOutput)
        {
          CalculationPath calculationPath = new CalculationPath(calculator.getClass().getName());
          boolean oneOfQuantitiesAlreadyRequired = false;
          for (PhysicalQuantity inputQuantity : calculator.getInputQuantities())
          {
            if (alreadyRequiredInTree.contains(new PhysicalQuantityInSet(inputQuantity, soughtValue.getSetId())))
            {
              oneOfQuantitiesAlreadyRequired = true;
              break;
            }
            CalculationTreeNode inputNode = new CalculationTreeNode(new PhysicalQuantityInSet(inputQuantity, soughtValue.getSetId()));
            inputNode.setKnownValue(getKnownValue(inputNode.getCalculatedQuantity()));
            calculationPath.addRequiredInput(inputNode);
            fillCalculationTree(inputNode, cutoff - 1, requiredForInputNodes);
          }
          if (!oneOfQuantitiesAlreadyRequired)
          {
            toFill.addPossibleCalculationPathForCalculatedQuantity(calculationPath);
          }
        }
        for (QuantityRelation quantityRelations : valueSet.getQuantityRelations())
        {
          if (quantityRelations.getRelatedQuantities().contains(soughtValue.getPhysicalQuantity()))
          {
            // one of the related quantities is the sought value,
            // any of the other related quantities determines the sought value

            boolean relatedQuantityAlreadyRequired = false;
            for (PhysicalQuantityInSet alreadyRequiredQuantity : alreadyRequiredInTree)
            {
              if (alreadyRequiredQuantity.getSetId().equals(valueSet.getId())
                  && quantityRelations.getRelatedQuantities().contains(alreadyRequiredQuantity.getPhysicalQuantity()))
              {
                relatedQuantityAlreadyRequired = true;
                break;
              }
            }
            if (relatedQuantityAlreadyRequired)
            {
              break;
            }

            for (PhysicalQuantity inputQuantity : quantityRelations.getRelatedQuantities())
            {
              if (inputQuantity.equals(soughtValue.getPhysicalQuantity())
                  || alreadyRequiredInTree.contains(new PhysicalQuantityInSet(inputQuantity, soughtValue.getSetId())))
              {
                continue;
              }
              CalculationTreeNode inputNode = new CalculationTreeNode(new PhysicalQuantityInSet(inputQuantity, soughtValue.getSetId()));
              inputNode.setKnownValue(getKnownValue(inputNode.getCalculatedQuantity()));
              CalculationPath calculationPath = new CalculationPath(quantityRelations.getName())
                  .addRequiredInput(inputNode);
              fillCalculationTree(inputNode, cutoff - 1, requiredForInputNodes);
              for (PhysicalQuantityValue fixedPhysicalQuantity : quantityRelations.getFixedQuantities().getAsList())
              {
                CalculationTreeNode fixedQuantityInputNode = new CalculationTreeNode(
                    new PhysicalQuantityInSet(fixedPhysicalQuantity.getPhysicalQuantity(), soughtValue.getSetId()));
                fixedQuantityInputNode.setKnownValue(getKnownValue(fixedQuantityInputNode.getCalculatedQuantity()));
                calculationPath.addRequiredInput(fixedQuantityInputNode);
                fillCalculationTree(fixedQuantityInputNode, cutoff - 1, requiredForInputNodes);
              }
              // there may be several quantity relations with the same set of parameters
              // add only one of them
              if (!toFill.getPossibleCalculationPathForCalculatedQuantity().contains(calculationPath))
              {
                toFill.addPossibleCalculationPathForCalculatedQuantity(calculationPath);
              }
            }
          }
        }
      }
    }
    for (ComputationStrategy computationStrategy : getAllComputationStrategies())
    {
      if (computationStrategy.getOutputs().contains(soughtValue))
      {
        CalculationPath calculationPath = new CalculationPath(computationStrategy.getClass().getName());
        for (PhysicalQuantityInSet inputQuantity : computationStrategy.getInputs())
        {
          CalculationTreeNode inputNode = new CalculationTreeNode(inputQuantity);
          inputNode.setKnownValue(getKnownValue(inputQuantity));
          calculationPath.addRequiredInput(inputNode);
          fillCalculationTree(inputNode, cutoff - 1, requiredForInputNodes);
        }
        toFill.addPossibleCalculationPathForCalculatedQuantity(calculationPath);
      }
    }
  }

  public void printCalculationTree(CalculationTreeNode rootNode)
  {
    printCalculationTreeNode(rootNode,0);
  }

  public void printCalculationTreeNode(CalculationTreeNode node, int indent)
  {
    if (node.getKnownValue() != null)
    {
      System.out.println(getIndentString(indent) + node.getCalculatedQuantity() + " : " + node.getKnownValue());
    }
    else
    {
      System.out.println(getIndentString(indent) + node.getCalculatedQuantity());
      for (CalculationPath calculationPath : node.getPossibleCalculationPathForCalculatedQuantity())
      {
        System.out.println(getIndentString(indent + 2) + calculationPath.getName() + " [");
        for (CalculationTreeNode input : calculationPath.getRequiredInputs())
        {
          printCalculationTreeNode(input, indent + 4);
        }
        System.out.println(getIndentString(indent + 2) + "]");
      }
    }
  }

  public String getIndentString(int indent)
  {
    StringBuilder result = new StringBuilder(indent);
    for (int i = 0; i < indent; i++)
    {
      result.append(' ');
    }
    return result.toString();
  }
}
