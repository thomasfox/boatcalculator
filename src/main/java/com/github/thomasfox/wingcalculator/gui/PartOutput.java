package com.github.thomasfox.wingcalculator.gui;

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PartOutput
{
  @NonNull
  private final String name;

  @Getter
  private final List<QuantityOutput> quantityOutputs = new ArrayList<>();

  public void add(QuantityOutput quantityOutput)
  {
    quantityOutputs.add(quantityOutput);
  }

  public void removeFromContainerAndReset(Container container)
  {
    for (QuantityOutput quantityOutput : quantityOutputs)
    {
      quantityOutput.removeFromContainer(container);
    }
    quantityOutputs.clear();
  }

  public int addToContainerInRow(Container container, int rowOffset)
  {
    if (quantityOutputs.size() == 0)
    {
      return 0;
    }
    SwingHelper.addSeparatorToContainer(container, rowOffset, 5);
    SwingHelper.addLabelToContainer(name, container, 0, rowOffset + 1);
    SwingHelper.addLabelToContainer(" ", container, 0, rowOffset + 2);
    int row = 0;
    for (QuantityOutput quantityOutput : quantityOutputs)
    {
      quantityOutput.addToContainerInRow(container, rowOffset + 3 + row++);
    }
    return 3 + quantityOutputs.size();
  }
}
