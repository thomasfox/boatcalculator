package com.github.thomasfox.sailboatcalculator.valueset.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.QuantityNotPresentException;
import com.github.thomasfox.sailboatcalculator.interpolate.QuantityRelations;
import com.github.thomasfox.sailboatcalculator.value.CalculatedPhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.value.CalculatedPhysicalQuantityValues;
import com.github.thomasfox.sailboatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.value.PhysicalQuantityValueWithSetId;
import com.github.thomasfox.sailboatcalculator.value.PhysicalQuantityValues;
import com.github.thomasfox.sailboatcalculator.value.PhysicalQuantityValuesWithSetIdPerValue;
import com.github.thomasfox.sailboatcalculator.valueset.AllValues;
import com.github.thomasfox.sailboatcalculator.valueset.HasProfile;
import com.github.thomasfox.sailboatcalculator.valueset.ValueSet;

import lombok.NonNull;

public class DoubleWing implements ValueSet, HasProfile
{
  @NonNull
  private final ValueSet singleWing;

  @NonNull
  private final String id;

  @NonNull
  private final String name;

  private final String profileName;

  public DoubleWing(Wing singleWing, String id, String name)
  {
    this.singleWing = singleWing;
    this.id = id;
    this.name = name;
    this.profileName = singleWing.getProfileName();
  }

  private DoubleWing(ValueSet singleWing, String id, String name, String profileName)
  {
    this.singleWing = singleWing;
    this.id = id;
    this.name = name;
    this.profileName = profileName;
  }

  @Override
  public String getId()
  {
    return id;
  }

  @Override
  public String getDisplayName()
  {
    return name;
  }

  @Override
  public boolean isValueKnown(PhysicalQuantity toCheck)
  {
    return toCheck.getAdditive() != null && singleWing.isValueKnown(toCheck);
  }

  @Override
  public PhysicalQuantityValue getKnownValue(PhysicalQuantity toGet)
  {
    PhysicalQuantityValue singleComponentValue = singleWing.getKnownValue(toGet);
    return convertFromSingleComponentValue(singleComponentValue);
  }

  @Override
  public PhysicalQuantityValues getKnownValues(Collection<PhysicalQuantity> quantitiesToRead)
  {
    return getAsPhysicalQuantityValuesValues(quantitiesToRead, this::getKnownValue);
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
  public PhysicalQuantityValues getFixedValues()
  {
    return getAsPhysicalQuantityValuesValues(singleWing.getFixedValues().getContainedQuantities(), this::getFixedValue);
  }

  @Override
  public PhysicalQuantityValue getFixedValue(PhysicalQuantity physicalQuantity)
  {
    PhysicalQuantityValue singleComponentValue = singleWing.getFixedValue(physicalQuantity);
    return convertFromSingleComponentValue(singleComponentValue);
  }

  @Override
  public void setFixedValueNoOverwrite(PhysicalQuantityValue toSet)
  {
    PhysicalQuantityValue singleComponentValue = convertToSingleComponentValue(toSet);
    singleWing.setFixedValueNoOverwrite(singleComponentValue);
  }

  @Override
  public PhysicalQuantityValues getStartValues()
  {
    return getAsPhysicalQuantityValuesValues(singleWing.getStartValues().getContainedQuantities(), this::getStartValue);
  }

  @Override
  public PhysicalQuantityValue getStartValue(PhysicalQuantity physicalQuantity)
  {
    PhysicalQuantityValue singleComponentValue = singleWing.getStartValue(physicalQuantity);
    return convertFromSingleComponentValue(singleComponentValue);
  }

  @Override
  public void setStartValueNoOverwrite(PhysicalQuantityValue toSet)
  {
    PhysicalQuantityValue singleComponentValue = convertToSingleComponentValue(toSet);
    singleWing.setStartValueNoOverwrite(singleComponentValue);
  }

  @Override
  public CalculatedPhysicalQuantityValues getCalculatedValues()
  {
     PhysicalQuantityValues physicalQuantityValues = getAsPhysicalQuantityValuesValues(
         singleWing.getCalculatedValues().getContainedQuantities(),
         this::getCalculatedValue);

     CalculatedPhysicalQuantityValues result = new CalculatedPhysicalQuantityValues();
     for (PhysicalQuantityValue value : physicalQuantityValues.getAsList())
     {
       result.setValue((CalculatedPhysicalQuantityValue) value);
     }
    return result;
  }

  @Override
  public CalculatedPhysicalQuantityValue getCalculatedValue(PhysicalQuantity physicalQuantity)
  {
    CalculatedPhysicalQuantityValue singleComponentValue = singleWing.getCalculatedValue(physicalQuantity);
    PhysicalQuantityValue doubleComponentValue = convertFromSingleComponentValue(singleComponentValue);
    return new CalculatedPhysicalQuantityValue(doubleComponentValue, singleComponentValue.getCalculatedBy(), singleComponentValue.getCalculatedFrom());
  }

  @Override
  public void setCalculatedValueNoOverwrite(
      PhysicalQuantityValue calculatedValue,
      String calculatedBy,
      PhysicalQuantityValueWithSetId... calculatedFrom)
  {
    PhysicalQuantityValue singleComponentValue = convertToSingleComponentValue(calculatedValue);
    singleWing.setCalculatedValueNoOverwrite(singleComponentValue, calculatedBy, calculatedFrom);
  }

  @Override
  public void setCalculatedValueNoOverwrite(
      PhysicalQuantityValue calculatedValue,
      String calculatedBy,
      PhysicalQuantityValuesWithSetIdPerValue calculatedFrom)
  {
    PhysicalQuantityValue singleComponentValue = convertToSingleComponentValue(calculatedValue);
    singleWing.setCalculatedValueNoOverwrite(singleComponentValue, calculatedBy, calculatedFrom);
  }

  @Override
  public void addToInput(PhysicalQuantity toAdd)
  {
    singleWing.addToInput(toAdd);
  }

  @Override
  public Set<PhysicalQuantity> getToInput()
  {
    return singleWing.getToInput();
  }

  @Override
  public void addHiddenOutput(PhysicalQuantity toAdd)
  {
    singleWing.addHiddenOutput(toAdd);
  }

  @Override
  public Set<PhysicalQuantity> getHiddenOutputs()
  {
    return singleWing.getHiddenOutputs();
  }

  @Override
  public void moveCalculatedValuesToStartValues()
  {
    singleWing.moveCalculatedValuesToStartValues();
  }

  @Override
  public void clearCalculatedValues()
  {
    singleWing.clearCalculatedValues();
  }

  @Override
  public void clearStartValues()
  {
    singleWing.clearStartValues();
  }

  @Override
  public boolean calculateSinglePass(AllValues allValues,
      PhysicalQuantity wantedQuantity)
  {
    return singleWing.calculateSinglePass(allValues, wantedQuantity);
  }

  @Override
  public List<QuantityRelations> getQuantityRelations()
  {
    // TODO this is unclean
    return singleWing.getQuantityRelations();
  }

  private PhysicalQuantityValue convertFromSingleComponentValue(
      PhysicalQuantityValue singleComponentValue)
  {
    if (singleComponentValue == null)
    {
      return null;
    }
    Boolean additive = singleComponentValue.getPhysicalQuantity().getAdditive();
    if (additive == null)
    {
      return null;
    }
    else if (Boolean.TRUE.equals(additive))
    {
      return new PhysicalQuantityValue(singleComponentValue.getPhysicalQuantity(), 2*singleComponentValue.getValue());
    }
    else
    {
      return singleComponentValue;
    }
  }

  private PhysicalQuantityValue convertToSingleComponentValue(
      PhysicalQuantityValue doubleComponentValue)
  {
    if (doubleComponentValue == null)
    {
      return null;
    }
    Boolean additive = doubleComponentValue.getPhysicalQuantity().getAdditive();
    if (additive == null)
    {
      return null;
    }
    else if (Boolean.TRUE.equals(additive))
    {
      return new PhysicalQuantityValue(doubleComponentValue.getPhysicalQuantity(), 0.5d*doubleComponentValue.getValue());
    }
    else
    {
      return doubleComponentValue;
    }
  }

  public PhysicalQuantityValues getAsPhysicalQuantityValuesValues(
      Collection<PhysicalQuantity> quantitiesToRead,
      Function<PhysicalQuantity, PhysicalQuantityValue> retrieveSingleValueBy)
  {
    PhysicalQuantityValues result = new PhysicalQuantityValues();
    for (PhysicalQuantity quantityToRead : quantitiesToRead)
    {
      PhysicalQuantityValue doubleValue = retrieveSingleValueBy.apply(quantityToRead);
      if (doubleValue == null)
      {
        throw new QuantityNotPresentException(quantityToRead);
      }
      result.setValueNoOverwrite(doubleValue);
    }
    return result;
  }

  @Override
  public DoubleWing clone()
  {
    return new DoubleWing(singleWing.clone(), id, name, profileName);
  }

  @Override
  public String getProfileName()
  {
    return profileName;
  }
}
