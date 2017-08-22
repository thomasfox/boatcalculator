package com.github.thomasfox.wingcalculator.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import com.github.thomasfox.wingcalculator.part.BoatPart;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PartOutput
{
  @NonNull
  private final BoatPart boatPart;

  @Getter
  private final List<QuantityOutput> quantityOutputs = new ArrayList<>();

  public void add(QuantityOutput quantityOutput)
  {
    quantityOutputs.add(quantityOutput);
  }

  public void removeFromFrameAndReset(JFrame frame)
  {
    for (QuantityOutput quantityOutput : quantityOutputs)
    {
      quantityOutput.removeFromFrame(frame);
    }
    quantityOutputs.clear();
  }

  public int addToFrameInRow(JFrame frame, int rowOffset)
  {
    if (quantityOutputs.size() == 0)
    {
      return 0;
    }
    SwingHelper.addSeparatorToFrame(frame, rowOffset, 5);
    SwingHelper.addLabelToFrame(boatPart.getType().toString(), frame, 0, rowOffset + 1);
    SwingHelper.addLabelToFrame(" ", frame, 0, rowOffset + 2);
    int row = 0;
    for (QuantityOutput quantityOutput : quantityOutputs)
    {
      quantityOutput.addToFrameInRow(frame, rowOffset + 3 + row++);
    }
    return 3 + quantityOutputs.size();
  }
}
