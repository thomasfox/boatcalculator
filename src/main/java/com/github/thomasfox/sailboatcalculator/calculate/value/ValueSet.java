package com.github.thomasfox.sailboatcalculator.calculate.value;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.interpolate.QuantityRelations;

/**
 * A set of physical Quantities.
 */
public interface ValueSet
{
  public String getId();

  public String getName();

  public boolean isValueKnown(PhysicalQuantity toCheck);

  public PhysicalQuantityValue getKnownValue(PhysicalQuantity toGet);

  public PhysicalQuantityValues getKnownValues(Collection<PhysicalQuantity> quantitiesToRead);

  public PhysicalQuantityValueWithSetId[] getKnownValuesAsArray(Collection<PhysicalQuantity> toGet);

  public PhysicalQuantityValues getFixedValues();

  public PhysicalQuantityValue getFixedValue(PhysicalQuantity physicalQuantity);

  public void setFixedValueNoOverwrite(PhysicalQuantityValue toSet);

  public PhysicalQuantityValues getStartValues();

  public PhysicalQuantityValue getStartValue(PhysicalQuantity physicalQuantity);

  public void setStartValueNoOverwrite(PhysicalQuantityValue toSet);

  public CalculatedPhysicalQuantityValues getCalculatedValues();

  public CalculatedPhysicalQuantityValue getCalculatedValue(PhysicalQuantity physicalQuantity);

  public void setCalculatedValueNoOverwrite(
      PhysicalQuantityValue calculatedValue,
      String calculatedBy,
      PhysicalQuantityValueWithSetId... calculatedFrom);

  public void setCalculatedValueNoOverwrite(
      PhysicalQuantityValue calculatedValue,
      String calculatedBy,
      PhysicalQuantityValuesWithSetIdPerValue calculatedFrom);

  public void addToInput(PhysicalQuantity toAdd);

  public Set<PhysicalQuantity> getToInput();

  public void addHiddenOutput(PhysicalQuantity toAdd);

  public Set<PhysicalQuantity> getHiddenOutputs();

  public void moveCalculatedValuesToStartValues();

  public void clearCalculatedValues();

  public void clearStartValues();

  public boolean calculateSinglePass(AllValues allValues, PhysicalQuantity wantedQuantity);

  public List<QuantityRelations> getQuantityRelations();
}
