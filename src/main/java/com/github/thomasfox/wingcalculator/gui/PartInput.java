package com.github.thomasfox.wingcalculator.gui;

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import com.github.thomasfox.wingcalculator.calculate.NamedValueSet;
import com.github.thomasfox.wingcalculator.part.impl.Wing;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PartInput
{
  @NonNull
  @Getter
  private final NamedValueSet valueSet;

  @Getter
  private final List<QuantityInput> quantityInputs = new ArrayList<>();

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
    if (valueSet instanceof Wing)
    {
      profileInput = new ProfileInput();
      profileInput.addToContainer(container, rowOffset + internalOffset++);
    }
    return internalOffset;
  }

  public boolean isScan()
  {
    boolean scan = false;
    for (QuantityInput quantityInput : quantityInputs)
    {
      scan = scan || quantityInput.isScan();
    }
    scan = scan || (profileInput != null && profileInput.isScan());
    return scan;
  }

  public String getProfileName()
  {
    if (profileInput == null)
    {
      return null;
    }
    return profileInput.getProfileName();
  }
}