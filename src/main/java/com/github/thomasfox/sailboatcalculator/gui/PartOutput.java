package com.github.thomasfox.sailboatcalculator.gui;

import java.awt.Component;
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
  @Getter
  private final String id;

  @NonNull
  @Getter
  private final String name;

  @Getter
  private final List<QuantityOutput> quantityOutputs = new ArrayList<>();

  private final List<Component> additionalComponents = new ArrayList<>();

  public void add(QuantityOutput quantityOutput)
  {
    quantityOutputs.add(quantityOutput);
  }

  public void removeFromContainerAndReset(Container container)
  {
    for (Component additionalComponent : additionalComponents)
    {
      container.remove(additionalComponent);
    }
    additionalComponents.clear();
    for (QuantityOutput quantityOutput : quantityOutputs)
    {
      quantityOutput.removeFromContainer(container);
    }
    quantityOutputs.clear();
  }

  public int addToContainerInRow(Container container, int rowOffset, QuantityOutput.Mode mode)
  {
    if (quantityOutputs.size() == 0)
    {
      return 0;
    }
    additionalComponents.add(SwingHelper.addSeparatorToContainer(container, rowOffset, 5));
    additionalComponents.add(SwingHelper.addLabelToContainer(name, container, 0, rowOffset + 1));
    additionalComponents.add(SwingHelper.addLabelToContainer(" ", container, 0, rowOffset + 2));
    int row = 0;
    for (QuantityOutput quantityOutput : quantityOutputs)
    {
      quantityOutput.addToContainerInRow(container, rowOffset + 3 + row++, mode);
    }
    return 3 + quantityOutputs.size();
  }

  public List<QuantityOutput> getShownGraphs()
  {
    List<QuantityOutput> result = new ArrayList<>();
    for (QuantityOutput quantityOutput : quantityOutputs)
    {
      if (quantityOutput.isShowGraph())
      {
        result.add(quantityOutput);
      }
    }
    return result;
  }
}
