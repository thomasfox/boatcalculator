package com.github.thomasfox.wingcalculator.gui;

import java.awt.GridBagConstraints;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;

public class QuantityInput
{
  JLabel label = new JLabel();

  JTextField textField = new JTextField();

  PhysicalQuantity quantity;

  private Double value;

  public QuantityInput(PhysicalQuantity quantity, Double value)
  {
    this.quantity = quantity;
    setValue(value);
    label.setText(quantity.getDisplayNameIncludingUnit());
    textField.getDocument().addDocumentListener(
        new QuantityInputDocumentListener());
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
    frame.add(textField, gridBagConstraints);
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
      textField.setText("");
    }
    else
    {
      textField.setText(value.toString());
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

  private class QuantityInputDocumentListener implements DocumentListener
  {
    @Override
    public void insertUpdate(DocumentEvent event)
    {
      handleChange(event.getDocument());
    }

    @Override
    public void removeUpdate(DocumentEvent event)
    {
      handleChange(event.getDocument());
    }

    @Override
    public void changedUpdate(DocumentEvent event)
    {
      handleChange(event.getDocument());
    }

    private void handleChange(Document document)
    {
      try
      {
        setValue(document.getText(0, document.getLength()));
      }
      catch (BadLocationException e)
      {
        // should not happen
        throw new RuntimeException(e);
      }
    }
  }
}
