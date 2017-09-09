package com.github.thomasfox.sailboatcalculator.calculate;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.github.thomasfox.sailboatcalculator.interpolate.QuantityRelations;

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
  @NonNull
  private final String id;

  @NonNull
  private final String name;

  protected final Set<PhysicalQuantity> toInput = new LinkedHashSet<>();

  /**
   * Physical constants.
   */
  private final PhysicalQuantityValues fixedValues = new PhysicalQuantityValues();

  private final PhysicalQuantityValues startValues = new PhysicalQuantityValues();

  private final PhysicalQuantityValues calculatedValues = new PhysicalQuantityValues();

  private final List<QuantityRelations> quantityRelations = new ArrayList<>();

  public NamedValueSet(NamedValueSet toCopy)
  {
    this.id = toCopy.getId();
    this.name = toCopy.getName();
    this.toInput.addAll(toCopy.toInput);
    this.fixedValues.setValuesFailOnOverwrite(toCopy.fixedValues);
    this.startValues.setValuesFailOnOverwrite(toCopy.startValues);
    this.calculatedValues.setValuesFailOnOverwrite(toCopy.calculatedValues);
    this.quantityRelations.addAll(toCopy.getQuantityRelations());
  }

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

  public boolean calculateSinglePass(AllValues allValues)
  {
    CombinedCalculator combinedCalculator = new CombinedCalculator(quantityRelations);

    boolean changed = combinedCalculator.calculate(this);
    return changed;
  }

  public void moveCalculatedValuesToStartValues()
  {
    startValues.setValues(calculatedValues);
    calculatedValues.clear();
  }
}
