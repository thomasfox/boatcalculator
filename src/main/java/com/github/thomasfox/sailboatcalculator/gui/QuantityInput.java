package com.github.thomasfox.sailboatcalculator.gui;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.iterate.DoubleIntervalIterator;

public class QuantityInput
{
  private final JLabel label = new JLabel();

  private final JTextField fixedValueField = new JTextField();

  private final JTextField scanFromField = new JTextField();

  private final JTextField scanToField = new JTextField();

  private final JTextField scanNumberOfStepsField = new JTextField();

  private final PhysicalQuantity quantity;

  public QuantityInput(PhysicalQuantity quantity, Double value)
  {
    this.quantity = quantity;
    setValue(value);
    label.setText(quantity.getDisplayNameIncludingUnit());
    scanNumberOfStepsField.setText("20");
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
    container.add(fixedValueField, gridBagConstraints);

    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = row;
    container.add(scanFromField, gridBagConstraints);

    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = row;
    container.add(scanToField, gridBagConstraints);

    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = row;
    container.add(scanNumberOfStepsField, gridBagConstraints);
  }

  public void removeFromFrame(JFrame frame)
  {
    frame.remove(label);
    frame.remove(fixedValueField);
    frame.remove(scanFromField);
    frame.remove(scanToField);
    frame.remove(scanNumberOfStepsField);
  }

  public PhysicalQuantity getQuantity()
  {
    return quantity;
  }

  public void setValue(Double value)
  {
    if (value == null)
    {
      fixedValueField.setText("");
    }
    else
    {
      fixedValueField.setText(value.toString());
    }
  }

  public Double getValue()
  {
    return parseDouble(fixedValueField.getText());
  }

  public Double getScanFrom()
  {
    return parseDouble(scanFromField.getText());
  }

  public Double getScanTo()
  {
    return parseDouble(scanToField.getText());
  }

  public Integer getNumberOfScanSteps()
  {
    return parseInteger(scanNumberOfStepsField.getText());
  }

  public double getScanStepValue(int step)
  {
    return getScanFrom() + (getScanTo() - getScanFrom()) * (((double) step) / (getNumberOfScanSteps() - 1));
  }

  public boolean isScan()
  {
    return getScanFrom() != null
        && getScanTo() != null
        && getNumberOfScanSteps() != null;
  }

  public String toCsvString()
  {
    if (isScan())
    {
      return getQuantity().getDisplayNameIncludingUnit() + ":;"
          + "Scan From:;" + getScanFrom() + ";"
          + "Scan To:;" + getScanTo() + ";"
          + "Number of Scan Steps:;" + getNumberOfScanSteps() + ";\r\n";
    }
    else if (getValue() != null)
    {
      return getQuantity().getDisplayNameIncludingUnit() + ":;" + getValue() + ";\r\n";
    }
    else
    {
      return "";
    }
  }

  public Iterator<Double> getIterator()
  {
    if (isScan())
    {
      return new DoubleIntervalIterator(getScanFrom(), getScanTo(), getNumberOfScanSteps());
    }
    Double value = getValue();
    if (value != null)
    {
      return new DoubleIntervalIterator(value, value, 1);
    }
    return null;
  }

  private Double parseDouble(String value)
  {
    try
    {
      return Double.parseDouble(value);
    }
    catch (NumberFormatException e)
    {
      return null;
    }
  }

  private Integer parseInteger(String value)
  {
    try
    {
      return Integer.parseInt(value);
    }
    catch (NumberFormatException e)
    {
      return null;
    }
  }

  @Override
  public String toString()
  {
    return label.getText();
  }
}
