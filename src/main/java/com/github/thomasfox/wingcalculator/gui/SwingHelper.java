package com.github.thomasfox.wingcalculator.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;

public class SwingHelper
{
  public static JLabel addLabelToFrame(String label, JFrame frame, int x, int y)
  {
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = x;
    gridBagConstraints.gridy = y;
    JLabel labelComponent = new JLabel(label);
    frame.add(labelComponent, gridBagConstraints);
    return labelComponent;
  }


  public static JSeparator addSeparatorToFrame(JFrame frame, int rowOffset, int gridwidth)
  {
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.gridwidth = gridwidth;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = rowOffset;
    gridBagConstraints.ipady = 5;
    JSeparator jSeparator = new JSeparator();
    jSeparator.setBackground(Color.BLACK);
    frame.add(new JSeparator(), gridBagConstraints);
    return jSeparator;
  }

}
