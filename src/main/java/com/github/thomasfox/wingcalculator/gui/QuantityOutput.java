package com.github.thomasfox.wingcalculator.gui;

import java.awt.GridBagConstraints;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;

import lombok.ToString;

@ToString(of={"label", "value"})
public class QuantityOutput
{
  JLabel label = new JLabel();

  JLabel valueLabel = new JLabel();

  PhysicalQuantity quantity;

  private Double value;

  public QuantityOutput(PhysicalQuantity quantity, Double value)
  {
    this.quantity = quantity;
    setValue(value);
    label.setText(quantity.getDisplayNameIncludingUnit());
  }

  public void addToFrameInRow(JFrame frame, int row)
  {
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = row;
    frame.add(label, gridBagConstraints);

    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = row;
    frame.add(valueLabel, gridBagConstraints);
  }

  public void removeFromFrame(JFrame frame)
  {
    frame.remove(label);
    frame.remove(valueLabel);
  }

  public Double getValue()
  {
    return value;
  }

  public void setValue(Double value)
  {
    this.value = value;
    if (value == null)
    {
      valueLabel.setText("");
    }
    else
    {
      valueLabel.setText(value.toString());
    }
  }

  public void setValue(String value)
  {
    Double newValue;
    try
    {
      newValue = Double.parseDouble(value);
    }
    catch (NumberFormatException e)
    {
      return;
    }
    setValue(newValue);
  }
}
