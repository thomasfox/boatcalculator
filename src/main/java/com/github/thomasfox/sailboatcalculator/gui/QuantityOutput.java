package com.github.thomasfox.sailboatcalculator.gui;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.math.BigDecimal;
import java.math.MathContext;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.CalculatedPhysicalQuantityValue;

import lombok.Getter;
import lombok.ToString;

@ToString(of={"label", "value"})
public class QuantityOutput
{
  private final JLabel label = new JLabel();

  private final JLabel valueLabel = new JLabel();

  private final JLabel originLabel = new JLabel();

  @Getter
  private final PhysicalQuantity quantity;

  @Getter
  private final String setName;

  private Double value;

  private final JCheckBox showGraph = new JCheckBox();

  private Mode mode;

  public QuantityOutput(CalculatedPhysicalQuantityValue calculatedValue, String setName)
  {
    this.quantity = calculatedValue.getPhysicalQuantity();
    this.setName = setName;
    setValue(calculatedValue.getValue());
    label.setText(quantity.getDisplayNameIncludingUnit());
    originLabel.setText(calculatedValue.getCalculatedBy());
    mode = Mode.NOT_DISPLAYED;
  }

  public void addToContainerInRow(Container container, int row, Mode newMode)
  {
    if (mode != Mode.NOT_DISPLAYED)
    {
      throw new IllegalStateException("Row is already disaplayed");
    }
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = row;
    container.add(label, gridBagConstraints);

    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = row;
    if (newMode == Mode.NUMERIC_DISPLAY)
    {
      container.add(valueLabel, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = GridBagConstraints.BOTH;
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = row;
      container.add(originLabel, gridBagConstraints);
    }
    else if (newMode == Mode.CHECKBOX_DISPLAY)
    {
      container.add(showGraph, gridBagConstraints);
    }
    else
    {
      throw new IllegalArgumentException("invalid mode " + newMode);
    }
    mode = newMode;
  }

  public void removeFromContainer(Container container)
  {
    if (mode == Mode.NOT_DISPLAYED)
    {
      throw new IllegalStateException("Row is not displayed");
    }
    container.remove(label);
    if (mode == Mode.NUMERIC_DISPLAY)
    {
      container.remove(valueLabel);
      container.remove(originLabel);
    }
    else
    {
      container.remove(showGraph);
    }
    mode = Mode.NOT_DISPLAYED;
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
      BigDecimal bd = new BigDecimal(value);
      bd = bd.round(new MathContext(3));
      valueLabel.setText(bd.toPlainString());
      valueLabel.setBorder(new EmptyBorder(0,10,0,10));
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

  public boolean isShowGraph()
  {
    return showGraph.isSelected();
  }

  public enum Mode
  {
    NOT_DISPLAYED,
    NUMERIC_DISPLAY,
    CHECKBOX_DISPLAY;
  }
}
