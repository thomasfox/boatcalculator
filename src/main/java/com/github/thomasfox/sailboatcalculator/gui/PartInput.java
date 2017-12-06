package com.github.thomasfox.sailboatcalculator.gui;

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.HasProfile;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.calculate.value.ValueSet;
import com.github.thomasfox.sailboatcalculator.interpolate.QuantityRelations;
import com.github.thomasfox.sailboatcalculator.profile.ProfileGeometry;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PartInput
{
  @NonNull
  @Getter
  private final ValueSet valueSet;

  @Getter
  private final List<QuantityInput> quantityInputs = new ArrayList<>();

  private List<QuantityRelations> originalQuantityRelationsList;

  private ProfileInput profileInput;

  public void add(QuantityInput quantityInput)
  {
    quantityInputs.add(quantityInput);
  }

  public int addToContainerInRow(Container container, int rowOffset)
  {
    if (quantityInputs.size() == 0)
    {
      return 0;
    }
    int internalOffset = 0;
    if (rowOffset != 0)
    {
      SwingHelper.addSeparatorToContainer(container, rowOffset + internalOffset++, 5);
    }
    SwingHelper.addLabelToContainer(valueSet.getName(), container, 0, rowOffset + internalOffset++);
    SwingHelper.addLabelToContainer(" ", container, 0, rowOffset + internalOffset++);
    SwingHelper.addLabelToContainer("Fester Wert", container, 1, rowOffset + internalOffset);
    SwingHelper.addLabelToContainer("Scan Von", container, 2, rowOffset + internalOffset);
    SwingHelper.addLabelToContainer("Scan Bis", container, 3, rowOffset + internalOffset);
    SwingHelper.addLabelToContainer("Schritte", container, 4, rowOffset+ internalOffset++);
    int row = 0;
    for (QuantityInput quantityInput : quantityInputs)
    {
      quantityInput.addToContainerInRow(container, rowOffset + internalOffset + row++);
    }
    internalOffset += quantityInputs.size();
    if (valueSet instanceof HasProfile)
    {
      profileInput = new ProfileInput(((HasProfile) valueSet).getProfileName());
      profileInput.addToContainer(container, rowOffset + internalOffset++);
    }
    return internalOffset;
  }

  public List<QuantityInput> getScannedQuantityInputs()
  {
    List<QuantityInput> result = new ArrayList<>();
    for (QuantityInput quantityInput : quantityInputs)
    {
      if (quantityInput.isScan())
      {
        result.add(quantityInput);
      }
    }
    return result;
  }

  public String getProfileName()
  {
    if (profileInput == null)
    {
      return null;
    }
    return profileInput.getProfileName();
  }

  public void applyStartValues()
  {
    valueSet.clearStartValues();
    for (QuantityInput quantityInput : quantityInputs)
    {
      if (!valueSet.isValueKnown(quantityInput.getQuantity()))
      {
        if (quantityInput.getValue() != null)
        {
          valueSet.setStartValueNoOverwrite(new PhysicalQuantityValue(quantityInput.getQuantity(), quantityInput.getValue()));
        }
        else if (quantityInput.getScanFrom() != null)
        {
          valueSet.setStartValueNoOverwrite(new PhysicalQuantityValue(quantityInput.getQuantity(), quantityInput.getScanFrom()));
        }
      }
    }
  }

  public void applyProfile()
  {
    if (originalQuantityRelationsList == null)
    {
      originalQuantityRelationsList = new ArrayList<>(valueSet.getQuantityRelations());
    }
    else
    {
      valueSet.getQuantityRelations().clear();
      valueSet.getQuantityRelations().addAll(originalQuantityRelationsList);
    }

    String profileName = getProfileName();
    if (profileName != null)
    {
      ProfileGeometry profileGeometry = profileInput.loadProfile(
          SwingGui.PROFILE_DIRECTORY, profileName);
      valueSet.setStartValueNoOverwrite(new PhysicalQuantityValue(
          PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA,
          profileGeometry.getSecondMomentOfArea()));
      valueSet.setStartValueNoOverwrite(new PhysicalQuantityValue(
          PhysicalQuantity.WING_RELATIVE_THICKNESS,
          profileGeometry.getThickness()));
      valueSet.getQuantityRelations().addAll(
          profileInput.loadXfoilResults(
              SwingGui.PROFILE_DIRECTORY, profileName));
    }
  }

  @Override
  public String toString()
  {
    return valueSet.getName();
  }
}
