package com.github.thomasfox.sailboatcalculator.valueset;

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
import com.github.thomasfox.sailboatcalculator.value.CalculatedPhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.value.CalculatedPhysicalQuantityValues;
import com.github.thomasfox.sailboatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.value.PhysicalQuantityValueWithSetId;
import com.github.thomasfox.sailboatcalculator.value.PhysicalQuantityValues;
import com.github.thomasfox.sailboatcalculator.value.PhysicalQuantityValuesWithSetIdPerValue;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * The default implementation of <code>ValueSet</code>.
 */
@Data
@RequiredArgsConstructor
public class SimpleValueSet implements ValueSet
{
  @NonNull
  private final String id;

  @NonNull
  private final String displayName;

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
    this.displayName = toCopy.getDisplayName();
    this.toInput.addAll(toCopy.toInput);
    this.hiddenOutputs.addAll(toCopy.hiddenOutputs);
    this.fixedValues.setValuesFailOnOverwrite(toCopy.fixedValues);
    this.startValues.setValuesFailOnOverwrite(toCopy.startValues);
    this.calculatedValues.setValuesFailOnOverwrite(toCopy.calculatedValues);
    this.quantityRelations.addAll(toCopy.getQuantityRelations());
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
      PhysicalQuantityValue calculatedValue,
      String calculatedBy,
      PhysicalQuantityValueWithSetId... calculatedFrom)
  {
    fixedValues.checkQuantityNotSetForWrite(calculatedValue.getPhysicalQuantity());
    startValues.checkQuantityNotSetForWrite(calculatedValue.getPhysicalQuantity());
    calculatedValues.setValueNoOverwrite(calculatedValue, calculatedBy, calculatedFrom);
  }

  @Override
  public void setCalculatedValueNoOverwrite(
      PhysicalQuantityValue calculatedValue,
      String calculatedBy,
      PhysicalQuantityValuesWithSetIdPerValue calculatedFrom)
  {
    fixedValues.checkQuantityNotSetForWrite(calculatedValue.getPhysicalQuantity());
    startValues.checkQuantityNotSetForWrite(calculatedValue.getPhysicalQuantity());
    calculatedValues.setValueNoOverwrite(calculatedValue, calculatedBy, calculatedFrom);
  }

  public void setCalculatedValue(
      PhysicalQuantityValue calculatedValue,
      String calculatedBy,
      PhysicalQuantityValueWithSetId... calculatedFrom)
  {
    calculatedValues.setValue(calculatedValue, calculatedBy, calculatedFrom);
  }

  @Override
  public PhysicalQuantityValue getFixedValue(PhysicalQuantity physicalQuantity)
  {
    return fixedValues.getPhysicalQuantityValue(physicalQuantity);
  }

  @Override
  public PhysicalQuantityValue getStartValue(PhysicalQuantity physicalQuantity)
  {
    return startValues.getPhysicalQuantityValue(physicalQuantity);
  }

  @Override
  public CalculatedPhysicalQuantityValue getCalculatedValue(PhysicalQuantity physicalQuantity)
  {
    return calculatedValues.getPhysicalQuantityValue(physicalQuantity);
  }

  @Override
  public void addToInput(PhysicalQuantity toAdd)
  {
    toInput.add(toAdd);
  }

  public boolean removeToInput(PhysicalQuantity toRemove)
  {
    return toInput.remove(toRemove);
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
  public boolean calculateSinglePass(AllValues allValues, PhysicalQuantity wantedQuantity)
  {
    CombinedCalculator combinedCalculator = new CombinedCalculator(quantityRelations);

    boolean changed = combinedCalculator.calculate(this, wantedQuantity);
    return changed;
  }

  @Override
  public void moveCalculatedValuesToStartValues()
  {
    startValues.setValues(calculatedValues);
    calculatedValues.clear();
  }

  @Override
  public SimpleValueSet clone()
  {
    return new SimpleValueSet(this);
  }
}
