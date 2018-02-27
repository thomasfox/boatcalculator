package com.github.thomasfox.boatcalculator.gui.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.github.thomasfox.boatcalculator.progress.CalculationState;
import com.github.thomasfox.boatcalculator.progress.CalculationStateChangedListener;

public class CalculationStateDisplay extends JPanel implements CalculationStateChangedListener
{
  private static final long serialVersionUID = 1L;

  private final Map<String, JLabel> labels = new HashMap<>();

  public CalculationStateDisplay()
  {
    setPreferredSize(new Dimension(1000, 20));
    setBorder(BorderFactory.createLineBorder(Color.black));
    setLayout(new GridBagLayout());
  }

  @Override
  public void stateChanged()
  {
    for (Map.Entry<String, String> stateEntry : CalculationState.getState().entrySet())
    {
      JLabel label = labels.get(stateEntry.getKey());
      if (label == null)
      {
        label = new JLabel();
        label.setMinimumSize(new Dimension(250,20));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = labels.size();
        gridBagConstraints.weightx = 1;
        gridBagConstraints.insets = new Insets(2, 10, 2, 0);
        add(label, gridBagConstraints);
        labels.put(stateEntry.getKey(), label);
      }
      label.setText(stateEntry.getValue());
      label.repaint();
    }
  }

  public void clear()
  {
    for (JLabel label : labels.values())
    {
      remove(label);
    }
    labels.clear();
    CalculationState.clear();
  }
}
