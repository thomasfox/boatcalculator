package com.github.thomasfox.wingcalculator.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

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

  public int addToFrameInRow(JFrame frame, int rowOffset)
  {
    if (quantityInputs.size() == 0)
    {
      return 0;
    }
    int internalOffset = 0;
    if (rowOffset != 0)
    {
      SwingHelper.addSeparatorToFrame(frame, rowOffset + internalOffset++, 5);
    }
    SwingHelper.addLabelToFrame(valueSet.getName(), frame, 0, rowOffset + internalOffset++);
    SwingHelper.addLabelToFrame(" ", frame, 0, rowOffset + internalOffset++);
    SwingHelper.addLabelToFrame("Fester Wert", frame, 1, rowOffset + internalOffset);
    SwingHelper.addLabelToFrame("Scan Von", frame, 2, rowOffset + internalOffset);
    SwingHelper.addLabelToFrame("Scan Bis", frame, 3, rowOffset + internalOffset);
    SwingHelper.addLabelToFrame("Schritte", frame, 4, rowOffset+ internalOffset++);
    int row = 0;
    for (QuantityInput quantityInput : quantityInputs)
    {
      quantityInput.addToFrameInRow(frame, rowOffset + internalOffset + row++);
    }
    internalOffset += quantityInputs.size();
    if (valueSet instanceof Wing)
    {
      profileInput = new ProfileInput();
      profileInput.addToFrame(frame, rowOffset + internalOffset++);
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
