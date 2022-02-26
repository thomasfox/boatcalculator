package com.github.thomasfox.boatcalculator.valueset;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.interpolate.QuantityRelation;
import com.github.thomasfox.boatcalculator.value.CalculatedPhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.CalculatedPhysicalQuantityValues;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValues;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValuesWithSetIdPerValue;

/**
 * A collection of physical quantity values, and known relations between the quantities.
 * Usually describes a single part or aspect of a boat, e.g. the rigg or the crew's righting moment
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

  public PhysicalQuantityValue getKnownQuantityValue(PhysicalQuantity toGet);

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

  public String getProfileName();

  public void setProfileName(String profileName);

  /**
   * Sets a calculated value and makes sure it is not already set permanently (not as trial value).
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
      boolean isTrialValue,
      PhysicalQuantityValueWithSetId... calculatedFrom);

  /**
   * Sets a calculated value and makes sure it is not already set permanently (not as trial value).
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
      boolean isTrialValue,
      PhysicalQuantityValuesWithSetIdPerValue calculatedFrom);

  public void setCalculatedValue(
      PhysicalQuantityValue calculatedValue,
      String calculatedBy,
      boolean trialValue,
      PhysicalQuantityValueWithSetId... calculatedFrom);


  public void addToInput(PhysicalQuantity toAdd);

  public Set<PhysicalQuantity> getToInput();

  public void addHiddenOutput(PhysicalQuantity toAdd);

  public Set<PhysicalQuantity> getHiddenOutputs();

  public void moveCalculatedValuesToStartValues();

  public void clearCalculatedValues();

  public void clearStartValues();

  public Set<String> calculateSinglePass(
      ValuesAndCalculationRules allValues,
      PhysicalQuantity
      wantedQuantity,
      int step);

  public List<QuantityRelation> getQuantityRelations();

  public ValueSet clone();
}
