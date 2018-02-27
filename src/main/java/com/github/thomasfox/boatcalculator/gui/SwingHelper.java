package com.github.thomasfox.boatcalculator.gui;

import java.awt.Container;
import java.awt.GridBagConstraints;

import javax.swing.JLabel;
import javax.swing.JSeparator;

public class SwingHelper
{
  public static JLabel addLabelToContainer(String label, Container container, int x, int y)
  {
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = x;
    gridBagConstraints.gridy = y;
    JLabel labelComponent = new JLabel(label);
    container.add(labelComponent, gridBagConstraints);
    return labelComponent;
  }


  public static JSeparator addSeparatorToContainer(Container container, int rowOffset, int gridwidth)
  {
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.gridwidth = gridwidth;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = rowOffset;
    gridBagConstraints.ipady = 5;
    JSeparator jSeparator = new JSeparator();
    container.add(jSeparator, gridBagConstraints);
    return jSeparator;
  }
}
