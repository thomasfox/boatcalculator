package com.github.thomasfox.boatcalculator.gui.panel.part;

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.gui.SwingGui;
import com.github.thomasfox.boatcalculator.gui.SwingHelper;
import com.github.thomasfox.boatcalculator.interpolate.QuantityRelation;
import com.github.thomasfox.boatcalculator.profile.ProfileGeometry;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.valueset.HasProfile;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * A GUI to input start values for a <code>ValueSet</code>.
 */
@RequiredArgsConstructor
public class ValueSetInput
{
  @NonNull
  @Getter
  private final ValueSet valueSet;

  @Getter
  private final List<QuantityInput> quantityInputs = new ArrayList<>();

  private List<QuantityRelation> originalQuantityRelationsList;

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
    SwingHelper.addLabelToContainer(valueSet.getDisplayName(), container, 0, rowOffset + internalOffset++);
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

  public ProfileInput getProfileInput()
  {
    return profileInput;
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
          valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(quantityInput.getQuantity(), quantityInput.getValue()));
        }
        else if (quantityInput.getScanFrom() != null)
        {
          valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(quantityInput.getQuantity(), quantityInput.getScanFrom()));
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
      valueSet.setProfileName(profileName);
      ProfileGeometry profileGeometry = profileInput.loadProfile(
          SwingGui.PROFILE_DIRECTORY, profileName);
      if (profileGeometry != null)
      {
        valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(
            PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA,
            profileGeometry.getSecondMomentOfArea()));
        valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(
            PhysicalQuantity.WING_RELATIVE_THICKNESS,
            profileGeometry.getThickness()));
        valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(
            PhysicalQuantity.NORMALIZED_AREA_OF_CROSSECTION,
            profileGeometry.getCrossectionArea()));
        valueSet.setCalculatedValueNoOverwrite(new SimplePhysicalQuantityValue(
            PhysicalQuantity.MAX_RELATIVE_CAMBER,
            profileGeometry.getMaxRelativeCamber()), profileName + " geometry", false);
        valueSet.getQuantityRelations().addAll(
            profileInput.loadXfoilResults(
                SwingGui.PROFILE_DIRECTORY, profileName));
      }
    }
  }

  @Override
  public String toString()
  {
    return valueSet.getDisplayName();
  }
}
