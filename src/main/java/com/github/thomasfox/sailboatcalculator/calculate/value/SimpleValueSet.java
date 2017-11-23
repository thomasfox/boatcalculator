package com.github.thomasfox.sailboatcalculator.calculate.value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.github.thomasfox.sailboatcalculator.calculate.CombinedCalculator;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.QuantityNotPresentException;
import com.github.thomasfox.sailboatcalculator.interpolate.QuantityRelations;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * A set of physical Quantities.
 */
@Data
@RequiredArgsConstructor
public class SimpleValueSet implements ValueSet
{
  @NonNull
  private final String id;

  @NonNull
  private final String name;

  private final Set<PhysicalQuantity> toInput = new LinkedHashSet<>();

  private final Set<PhysicalQuantity> hiddenOutputs = new HashSet<>();

  /** Physical constants. */
  private final PhysicalQuantityValues fixedValues = new PhysicalQuantityValues();

  /** Values with which the calculation starts. */
  private final PhysicalQuantityValues startValues = new PhysicalQuantityValues();

  /** Values calculated from fixed values, start values and other calculated values. */
  private final CalculatedPhysicalQuantityValues calculatedValues = new CalculatedPhysicalQuantityValues();

  private final List<QuantityRelations> quantityRelations = new ArrayList<>();

  public SimpleValueSet(SimpleValueSet toCopy)
  {
    this.id = toCopy.getId();
    this.name = toCopy.getName();
    this.toInput.addAll(toCopy.toInput);
    this.hiddenOutputs.addAll(toCopy.hiddenOutputs);
    this.fixedValues.setValuesFailOnOverwrite(toCopy.fixedValues);
    this.startValues.setValuesFailOnOverwrite(toCopy.startValues);
    this.calculatedValues.setValuesFailOnOverwrite(toCopy.calculatedValues);
    this.quantityRelations.addAll(toCopy.getQuantityRelations());
  }

  @Override
  public PhysicalQuantityValues getKnownValues()
  {
    PhysicalQuantityValues result = new PhysicalQuantityValues(startValues);
    result.setValues(calculatedValues);
    result.setValues(fixedValues);
    return result;
  }

  @Override
  public PhysicalQuantityValueWithSetId[] getKnownValuesAsArray(Collection<PhysicalQuantity> toGet)
  {
    PhysicalQuantityValueWithSetId[] result = new PhysicalQuantityValueWithSetId[toGet.size()];
    int i = 0;
    for (PhysicalQuantity physicalQuantity : toGet)
    {
      PhysicalQuantityValue knownQuantity = getKnownValue(physicalQuantity);
      if (knownQuantity == null)
      {
        throw new QuantityNotPresentException(physicalQuantity);
      }
      result[i] = new PhysicalQuantityValueWithSetId(knownQuantity.getPhysicalQuantity(), knownQuantity.getValue(), id);
      ++i;
    }
    return result;
  }

  @Override
  public boolean isValueKnown(PhysicalQuantity toCheck)
  {
    return getKnownValue(toCheck) != null;
  }

  @Override
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

  @Override
  public PhysicalQuantityValues getKnownValues(Collection<PhysicalQuantity> quantitiesToRead)
  {
    PhysicalQuantityValues result = new PhysicalQuantityValues();
    for (PhysicalQuantity quantityToRead : quantitiesToRead)
    {
      PhysicalQuantityValue knownValue = getKnownValue(quantityToRead);
      if (knownValue == null)
      {
        throw new QuantityNotPresentException(quantityToRead);
      }
      result.setValueNoOverwrite(knownValue);
    }
    return result;
  }

  public void setFixedValueNoOverwrite(PhysicalQuantity physicalQuantity, double value)
  {
    startValues.checkQuantityNotSetForWrite(physicalQuantity);
    calculatedValues.checkQuantityNotSetForWrite(physicalQuantity);
    fixedValues.setValueNoOverwrite(physicalQuantity, value);
  }

  @Override
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

  @Override
  public void setStartValueNoOverwrite(PhysicalQuantityValue toSet)
  {
    fixedValues.checkQuantityNotSetForWrite(toSet.getPhysicalQuantity());
    calculatedValues.checkQuantityNotSetForWrite(toSet.getPhysicalQuantity());
    startValues.setValueNoOverwrite(toSet);
  }

  public void setStartValue(PhysicalQuantity physicalQuantity, double value)
  {
    startValues.setValue(physicalQuantity, value);
  }

  @Override
  public void setCalculatedValueNoOverwrite(
      PhysicalQuantity physicalQuantity,
      double value,
      String calculatedBy,
      PhysicalQuantityValueWithSetId... calculatedFrom)
  {
    fixedValues.checkQuantityNotSetForWrite(physicalQuantity);
    startValues.checkQuantityNotSetForWrite(physicalQuantity);
    calculatedValues.setValueNoOverwrite(physicalQuantity, value, calculatedBy, calculatedFrom);
  }

  @Override
  public void setCalculatedValueNoOverwrite(
      PhysicalQuantity physicalQuantity,
      double value,
      String calculatedBy,
      PhysicalQuantityValuesWithSetIdPerValue calculatedFrom)
  {
    fixedValues.checkQuantityNotSetForWrite(physicalQuantity);
    startValues.checkQuantityNotSetForWrite(physicalQuantity);
    calculatedValues.setValueNoOverwrite(physicalQuantity, value, calculatedBy, calculatedFrom);
  }

  public void setCalculatedValue(
      PhysicalQuantity physicalQuantity,
      double value,
      String calculatedBy,
      PhysicalQuantityValueWithSetId... calculatedFrom)
  {
    calculatedValues.setValue(physicalQuantity, value, calculatedBy, calculatedFrom);
  }

  @Override
  public Double getFixedValue(PhysicalQuantity physicalQuantity)
  {
    return fixedValues.getValue(physicalQuantity);
  }

  @Override
  public Double getStartValue(PhysicalQuantity physicalQuantity)
  {
    return startValues.getValue(physicalQuantity);
  }

  public Double getCalculatedValue(PhysicalQuantity physicalQuantity)
  {
    return calculatedValues.getValue(physicalQuantity);
  }

  @Override
  public void addToInput(PhysicalQuantity toAdd)
  {
    toInput.add(toAdd);
  }

  @Override
  public void addHiddenOutput(PhysicalQuantity toAdd)
  {
    hiddenOutputs.add(toAdd);
  }

  @Override
  public void clearCalculatedValues()
  {
    calculatedValues.clear();
  }

  public Double clearCalculatedValue(PhysicalQuantity toClear)
  {
    return calculatedValues.remove(toClear);
  }

  @Override
  public void clearStartValues()
  {
    startValues.clear();
  }

  @Override
  public boolean calculateSinglePass(AllValues allValues)
  {
    CombinedCalculator combinedCalculator = new CombinedCalculator(quantityRelations);

    boolean changed = combinedCalculator.calculate(this);
    return changed;
  }

  @Override
  public void moveCalculatedValuesToStartValues()
  {
    startValues.setValues(calculatedValues);
    calculatedValues.clear();
  }
}
