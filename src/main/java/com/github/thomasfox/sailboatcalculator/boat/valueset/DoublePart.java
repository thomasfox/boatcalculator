package com.github.thomasfox.sailboatcalculator.boat.valueset;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.QuantityNotPresentException;
import com.github.thomasfox.sailboatcalculator.calculate.value.AllValues;
import com.github.thomasfox.sailboatcalculator.calculate.value.CalculatedPhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.calculate.value.CalculatedPhysicalQuantityValues;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValueWithSetId;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValues;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValuesWithSetIdPerValue;
import com.github.thomasfox.sailboatcalculator.calculate.value.ValueSet;
import com.github.thomasfox.sailboatcalculator.interpolate.QuantityRelations;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class DoublePart implements ValueSet
{
  private @NonNull
  final ValueSet singleComponent;

  private @NonNull
  final String id;

  private @NonNull
  final String name;

  @Override
  public String getId()
  {
    return id;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public boolean isValueKnown(PhysicalQuantity toCheck)
  {
    return singleComponent.isValueKnown(toCheck);
  }

  @Override
  public PhysicalQuantityValue getKnownValue(PhysicalQuantity toGet)
  {
    PhysicalQuantityValue singleComponentValue = singleComponent.getKnownValue(toGet);
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
    return getAsPhysicalQuantityValuesValues(singleComponent.getFixedValues().getContainedQuantities(), this::getFixedValue);
  }

  @Override
  public PhysicalQuantityValue getFixedValue(PhysicalQuantity physicalQuantity)
  {
    PhysicalQuantityValue singleComponentValue = singleComponent.getFixedValue(physicalQuantity);
    return convertFromSingleComponentValue(singleComponentValue);
  }

  @Override
  public void setFixedValueNoOverwrite(PhysicalQuantityValue toSet)
  {
    PhysicalQuantityValue singleComponentValue = convertToSingleComponentValue(toSet);
    singleComponent.setFixedValueNoOverwrite(singleComponentValue);
  }

  @Override
  public PhysicalQuantityValues getStartValues()
  {
    return getAsPhysicalQuantityValuesValues(singleComponent.getStartValues().getContainedQuantities(), this::getStartValue);
  }

  @Override
  public PhysicalQuantityValue getStartValue(PhysicalQuantity physicalQuantity)
  {
    PhysicalQuantityValue singleComponentValue = singleComponent.getStartValue(physicalQuantity);
    return convertFromSingleComponentValue(singleComponentValue);
  }

  @Override
  public void setStartValueNoOverwrite(PhysicalQuantityValue toSet)
  {
    PhysicalQuantityValue singleComponentValue = convertToSingleComponentValue(toSet);
    singleComponent.setStartValueNoOverwrite(singleComponentValue);
  }

  @Override
  public CalculatedPhysicalQuantityValues getCalculatedValues()
  {
     PhysicalQuantityValues physicalQuantityValues = getAsPhysicalQuantityValuesValues(
         singleComponent.getCalculatedValues().getContainedQuantities(),
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
    CalculatedPhysicalQuantityValue singleComponentValue = singleComponent.getCalculatedValue(physicalQuantity);
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
    singleComponent.setCalculatedValueNoOverwrite(singleComponentValue, calculatedBy, calculatedFrom);
  }

  @Override
  public void setCalculatedValueNoOverwrite(
      PhysicalQuantityValue calculatedValue,
      String calculatedBy,
      PhysicalQuantityValuesWithSetIdPerValue calculatedFrom)
  {
    PhysicalQuantityValue singleComponentValue = convertToSingleComponentValue(calculatedValue);
    singleComponent.setCalculatedValueNoOverwrite(singleComponentValue, calculatedBy, calculatedFrom);
  }

  @Override
  public void addToInput(PhysicalQuantity toAdd)
  {
    singleComponent.addToInput(toAdd);
  }

  @Override
  public Set<PhysicalQuantity> getToInput()
  {
    return singleComponent.getToInput();
  }

  @Override
  public void addHiddenOutput(PhysicalQuantity toAdd)
  {
    singleComponent.addHiddenOutput(toAdd);
  }

  @Override
  public Set<PhysicalQuantity> getHiddenOutputs()
  {
    return singleComponent.getHiddenOutputs();
  }

  @Override
  public void moveCalculatedValuesToStartValues()
  {
    singleComponent.moveCalculatedValuesToStartValues();
  }

  @Override
  public void clearCalculatedValues()
  {
    singleComponent.clearCalculatedValues();
  }

  @Override
  public void clearStartValues()
  {
    singleComponent.clearStartValues();
  }

  @Override
  public boolean calculateSinglePass(AllValues allValues,
      PhysicalQuantity wantedQuantity)
  {
    return singleComponent.calculateSinglePass(allValues, wantedQuantity);
  }

  @Override
  public List<QuantityRelations> getQuantityRelations()
  {
    // TODO this is unclean
    return singleComponent.getQuantityRelations();
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
}
