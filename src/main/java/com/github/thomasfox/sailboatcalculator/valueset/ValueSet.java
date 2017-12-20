package com.github.thomasfox.sailboatcalculator.valueset;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.interpolate.QuantityRelations;
import com.github.thomasfox.sailboatcalculator.value.CalculatedPhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.value.CalculatedPhysicalQuantityValues;
import com.github.thomasfox.sailboatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.value.PhysicalQuantityValueWithSetId;
import com.github.thomasfox.sailboatcalculator.value.PhysicalQuantityValues;
import com.github.thomasfox.sailboatcalculator.value.PhysicalQuantityValuesWithSetIdPerValue;

/**
 * A collection of physical quantity values, and known relations between the quantities.
 *
 * Contains three sorts of values:
 * Fixed quantities (e.g. natural or material constants),
 * start quantities (which are known at the start of the calculation),
 * and calculated quantities, which are calculated from fixed values,
 * start values and other already calculated values.
 *
 * Relations between physical quantities in the set are stored as a list of QuantityRelations objects.
 */
public interface ValueSet extends Cloneable
{
  public String getId();

  public String getDisplayName();

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

  /**
   * Sets a calculated value and makes sure it is not already set.
   *
   * @param calculatedValue the value to set.
   * @param calculatedBy the way this value was calculated (e.g. the name of the used calculator)
   * @param calculatedFrom the input values which were used in the calculation.
   *
   * @throws IllegalArgumentException if the value is already set.
   */
  public void setCalculatedValueNoOverwrite(
      PhysicalQuantityValue calculatedValue,
      String calculatedBy,
      PhysicalQuantityValueWithSetId... calculatedFrom);

  /**
   * Sets a calculated value and makes sure it is not already set.
   *
   * @param calculatedValue the value to set.
   * @param calculatedBy the way this value was calculated (e.g. the name of the used calculator)
   * @param calculatedFrom the input values which were used in the calculation.
   *
   * @throws IllegalArgumentException if the value is already set.
   */
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

  public ValueSet clone();
}
