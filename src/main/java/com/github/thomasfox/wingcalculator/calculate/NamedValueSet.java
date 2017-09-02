package com.github.thomasfox.wingcalculator.calculate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.github.thomasfox.wingcalculator.calculate.strategy.QuantityEquality;
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

  private final Set<QuantityEquality> quantityEqualities = new HashSet<>();

  private final List<QuantityRelations> quantityRelations = new ArrayList<>();

  @NonNull
  private final String name;

  public PhysicalQuantityValues getKnownValues()
  {
    PhysicalQuantityValues result = new PhysicalQuantityValues(fixedValues);
    result.setValuesFailOnOverwrite(startValues);
    result.setValuesFailOnOverwrite(calculatedValues);
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
    result = startValues.getPhysicalQuantityValue(toGet);
    if (result != null)
    {
      return result;
    }
    result = calculatedValues.getPhysicalQuantityValue(toGet);
    return result;
  }


  public void setFixedValueNoOverwrite(PhysicalQuantityValue toSet)
  {
    fixedValues.setValueNoOverwrite(toSet);
  }

  public void setStartValueNoOverwrite(PhysicalQuantity physicalQuantity, double value)
  {
    startValues.setValueNoOverwrite(physicalQuantity, value);
  }

  public void setCalculatedValueNoOverwrite(PhysicalQuantity physicalQuantity, double value)
  {
    calculatedValues.setValueNoOverwrite(physicalQuantity, value);
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

  public boolean fillEqualities()
  {
    boolean changed = false;
    for (QuantityEquality quantityEquality : quantityEqualities)
    {
      changed = changed || quantityEquality.setValue(this);
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
}
