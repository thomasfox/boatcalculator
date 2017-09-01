package com.github.thomasfox.wingcalculator.gui;

import java.awt.Container;
import java.awt.GridBagConstraints;

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

  public void addToContainerInRow(Container container, int row)
  {
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = row;
    container.add(label, gridBagConstraints);

    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = row;
    container.add(valueLabel, gridBagConstraints);
  }

  public void removeFromContainer(Container container)
  {
    container.remove(label);
    container.remove(valueLabel);
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
