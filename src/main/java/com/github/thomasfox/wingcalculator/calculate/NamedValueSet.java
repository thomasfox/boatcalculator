package com.github.thomasfox.wingcalculator.calculate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.github.thomasfox.wingcalculator.calculate.strategy.ComputationStrategy;
import com.github.thomasfox.wingcalculator.interpolate.QuantityRelations;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * A set of physical Quantities which also has a name.
 */
@Data
@RequiredArgsConstructor
public class NamedValueSet
{
  protected final Set<PhysicalQuantity> toInput = new LinkedHashSet<>();

  private final PhysicalQuantityValues fixedValues = new PhysicalQuantityValues();

  private final PhysicalQuantityValues startValues = new PhysicalQuantityValues();

  private final PhysicalQuantityValues calculatedValues = new PhysicalQuantityValues();

  private final Set<ComputationStrategy> computationStrategies = new HashSet<>();

  private final List<QuantityRelations> quantityRelations = new ArrayList<>();

  @NonNull
  private final String name;

  public PhysicalQuantityValues getKnownValues()
  {
    PhysicalQuantityValues result = new PhysicalQuantityValues(startValues);
    result.setValues(calculatedValues);
    result.setValues(fixedValues);
    return result;
  }

  public boolean isValueKnown(PhysicalQuantity toCheck)
  {
    return getKnownValue(toCheck) != null;
  }

  public PhysicalQuantityValue getKnownValue(PhysicalQuantity toGet)
  {
    PhysicalQuantityValue result = fixedValues.getPhysicalQuantityValue(toGet);
    if (result != null)
    {
      return result;
    }
    result = calculatedValues.getPhysicalQuantityValue(toGet);
    if (result != null)
    {
      return result;
    }
    result = startValues.getPhysicalQuantityValue(toGet);
    return result;
  }


  public void setFixedValueNoOverwrite(PhysicalQuantity physicalQuantity, double value)
  {
    startValues.checkQuantityNotSetForWrite(physicalQuantity);
    calculatedValues.checkQuantityNotSetForWrite(physicalQuantity);
    fixedValues.setValueNoOverwrite(physicalQuantity, value);
  }

  public void setFixedValueNoOverwrite(PhysicalQuantityValue toSet)
  {
    startValues.checkQuantityNotSetForWrite(toSet.getPhysicalQuantity());
    calculatedValues.checkQuantityNotSetForWrite(toSet.getPhysicalQuantity());
    fixedValues.setValueNoOverwrite(toSet);
  }

  public void setStartValueNoOverwrite(PhysicalQuantity physicalQuantity, double value)
  {
    fixedValues.checkQuantityNotSetForWrite(physicalQuantity);
    calculatedValues.checkQuantityNotSetForWrite(physicalQuantity);
    startValues.setValueNoOverwrite(physicalQuantity, value);
  }

  public void setStartValue(PhysicalQuantity physicalQuantity, double value)
  {
    startValues.setValue(physicalQuantity, value);
  }

  public void setCalculatedValueNoOverwrite(PhysicalQuantity physicalQuantity, double value)
  {
    fixedValues.checkQuantityNotSetForWrite(physicalQuantity);
    startValues.checkQuantityNotSetForWrite(physicalQuantity);
    calculatedValues.setValueNoOverwrite(physicalQuantity, value);
  }

  public void setCalculatedValue(PhysicalQuantity physicalQuantity, double value)
  {
    calculatedValues.setValue(physicalQuantity, value);
  }

  public Double getFixedValue(PhysicalQuantity physicalQuantity)
  {
    return fixedValues.getValue(physicalQuantity);
  }

  public Double getStartValue(PhysicalQuantity physicalQuantity)
  {
    return startValues.getValue(physicalQuantity);
  }

  public void addToInput(PhysicalQuantity toAdd)
  {
    toInput.add(toAdd);
  }

  public void addComputationStrategy(ComputationStrategy computationStrategy)
  {
    computationStrategies.add(computationStrategy);
  }

  public boolean applyComputationStrategies(ComputationStrategy strategyToOmit)
  {
    boolean changed = false;
    for (ComputationStrategy computationStrategy : computationStrategies)
    {
      if (computationStrategy != strategyToOmit)
      {
        changed = changed || computationStrategy.setValue(this);
      }
    }
    return changed;
  }

  public void clearStartAndCalculatedValues()
  {
    startValues.clear();
    calculatedValues.clear();
  }

  public void clearCalculatedValues()
  {
    calculatedValues.clear();
  }

  public Double clearCalculatedValue(PhysicalQuantity toClear)
  {
    return calculatedValues.remove(toClear);
  }

  public void clearStartValues()
  {
    startValues.clear();
  }

  public boolean calculateSinglePass(ComputationStrategy strategyToOmit)
  {
    CombinedCalculator combinedCalculator = new CombinedCalculator(quantityRelations);

    boolean changedByCalculator = combinedCalculator.calculate(this);
    boolean changedByComputationStrategies = applyComputationStrategies(strategyToOmit);
    return changedByCalculator || changedByComputationStrategies;
  }
}
