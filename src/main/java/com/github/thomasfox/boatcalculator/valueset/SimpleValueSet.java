package com.github.thomasfox.boatcalculator.valueset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.github.thomasfox.boatcalculator.calculate.CombinedCalculator;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.calculate.QuantityNotPresentException;
import com.github.thomasfox.boatcalculator.interpolate.QuantityRelation;
import com.github.thomasfox.boatcalculator.value.CalculatedPhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.CalculatedPhysicalQuantityValues;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValues;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValuesWithSetIdPerValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValueWithSetId;

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

  private final List<QuantityRelation> quantityRelations = new ArrayList<>();

  private String profileName;

  private CombinedCalculator combinedCalculator = new CombinedCalculator();

  public SimpleValueSet(SimpleValueSet toCopy)
  {
    this.id = toCopy.getId();
    this.displayName = toCopy.getDisplayName();
    this.combinedCalculator = toCopy.combinedCalculator;
    this.toInput.addAll(toCopy.toInput);
    this.hiddenOutputs.addAll(toCopy.hiddenOutputs);
    this.fixedValues.setValuesFailOnOverwrite(toCopy.fixedValues);
    this.startValues.setValuesFailOnOverwrite(toCopy.startValues);
    this.calculatedValues.setValuesFailOnOverwrite(toCopy.calculatedValues);
    this.quantityRelations.addAll(toCopy.getQuantityRelations());
    this.profileName = toCopy.profileName;
  }

  @Override
  public PhysicalQuantityValueWithSetId[] getKnownValuesAsArray(Collection<PhysicalQuantity> toGet)
  {
    PhysicalQuantityValueWithSetId[] result = new PhysicalQuantityValueWithSetId[toGet.size()];
    int i = 0;
    for (PhysicalQuantity physicalQuantity : toGet)
    {
      PhysicalQuantityValue knownQuantity = getKnownQuantityValue(physicalQuantity);
      if (knownQuantity == null)
      {
        throw new QuantityNotPresentException(physicalQuantity);
      }
      result[i] = new SimplePhysicalQuantityValueWithSetId(knownQuantity, id);
      ++i;
    }
    return result;
  }

  @Override
  public boolean isValueKnown(PhysicalQuantity toCheck)
  {
    return getKnownQuantityValue(toCheck) != null;
  }

  @Override
  public PhysicalQuantityValue getKnownQuantityValue(PhysicalQuantity toGet)
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
      PhysicalQuantityValue knownValue = getKnownQuantityValue(quantityToRead);
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
      boolean trialValue,
      PhysicalQuantityValueWithSetId... calculatedFrom)
  {
    fixedValues.checkQuantityNotSetForWrite(calculatedValue.getPhysicalQuantity());
    startValues.checkQuantityNotSetForWrite(calculatedValue.getPhysicalQuantity());
    calculatedValues.setValueNoOverwrite(calculatedValue, calculatedBy, trialValue, calculatedFrom);
  }

  @Override
  public void setCalculatedValueNoOverwrite(
      PhysicalQuantityValue calculatedValue,
      String calculatedBy,
      boolean trialValue,
      PhysicalQuantityValuesWithSetIdPerValue calculatedFrom)
  {
    fixedValues.checkQuantityNotSetForWrite(calculatedValue.getPhysicalQuantity());
    startValues.checkQuantityNotSetForWrite(calculatedValue.getPhysicalQuantity());
    calculatedValues.setValueNoOverwrite(calculatedValue, calculatedBy, trialValue, calculatedFrom);
  }

  @Override
  public void setCalculatedValue(
      PhysicalQuantityValue calculatedValue,
      String calculatedBy,
      boolean trialValue,
      PhysicalQuantityValueWithSetId... calculatedFrom)
  {
    calculatedValues.setValue(calculatedValue, calculatedBy, trialValue, calculatedFrom);
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
  public Set<String> calculateSinglePass(ValuesAndCalculationRules allValues, PhysicalQuantity wantedQuantity, int step)
  {
    combinedCalculator.setQuantityRelations(quantityRelations);

    Set<String> changed = combinedCalculator.calculate(this, wantedQuantity, step);
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

  @Override
  public String getProfileName()
  {
    return profileName;
  }

  @Override
  public void setProfileName(String profileName)
  {
    this.profileName = profileName;
  }
}
