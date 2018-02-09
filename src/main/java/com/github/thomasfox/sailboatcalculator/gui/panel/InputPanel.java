package com.github.thomasfox.sailboatcalculator.gui.panel;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.gui.SwingHelper;
import com.github.thomasfox.sailboatcalculator.gui.panel.part.QuantityInput;
import com.github.thomasfox.sailboatcalculator.gui.panel.part.QuantityOutput;
import com.github.thomasfox.sailboatcalculator.gui.panel.part.ValueSetInput;
import com.github.thomasfox.sailboatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.valueset.ValueSet;

/**
 * Component to input start values of the calculation.
 */
public class InputPanel extends JPanel
{
  private static final long serialVersionUID = 1L;

  private final JButton calculateButton = new JButton("Berechnen");

  private final JButton scanButton = new JButton("Diagramme anzeigen");

  private final List<ValueSetInput> valueSetInputs = new ArrayList<>();

  public InputPanel(Collection<ValueSet> valueSets)
  {
    setLayout(new GridBagLayout());
    reset(valueSets);
  }

  public void reset(Collection<ValueSet> valueSets)
  {
    synchronized (getTreeLock())
    {
      for (Component component: getComponents())
      {
        remove(component);
      }
    }
    valueSetInputs.clear();

    for (ValueSet valueSet : valueSets)
    {
      createPartInput(valueSet);
    }

    int row = 0;
    for (ValueSetInput partInput : valueSetInputs)
    {
      row += partInput.addToContainerInRow(this, row);
    }

    SwingHelper.addSeparatorToContainer(this, row++, 5);

    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = row;
    add(calculateButton, gridBagConstraints);

    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = row;
    scanButton.setVisible(false);
    add(scanButton, gridBagConstraints);
    revalidate();
    repaint();
  }

  public void reinitalizeValueSetInputs()
  {
    for (ValueSetInput valueSetInput : valueSetInputs)
    {
      valueSetInput.getValueSet().clearCalculatedValues();
      valueSetInput.applyStartValues();
      valueSetInput.applyProfile();
    }
  }

  public void addToFrame(JFrame frame)
  {
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    frame.add(this, gridBagConstraints);
  }

  public void addCalculateButtonActionListener(ActionListener actionListener)
  {
    calculateButton.addActionListener(actionListener);
  }

  public void addScanButtonActionListener(ActionListener actionListener)
  {
    scanButton.addActionListener(actionListener);
  }

  public void setScanButtonVisible(boolean visible)
  {
    scanButton.setVisible(visible);
  }

  private void createPartInput(ValueSet valueSet)
  {
    ValueSetInput partInput = new ValueSetInput(valueSet);
    valueSetInputs.add(partInput);
    for (PhysicalQuantity physicalQuantity : valueSet.getToInput())
    {
      if (valueSet.getFixedValue(physicalQuantity) == null)
      {
        PhysicalQuantityValue startValue = valueSet.getStartValue(physicalQuantity);
        if (startValue == null)
        {
          partInput.add(new QuantityInput(physicalQuantity));
        }
        else
        {
          partInput.add(new QuantityInput(startValue));
        }
      }
    }
  }

  public List<QuantityInput> getScannedInputs()
  {
    List<QuantityInput> scannedInputs = new ArrayList<>();
    for (ValueSetInput valueSetInput : valueSetInputs)
    {
      scannedInputs.addAll(valueSetInput.getScannedQuantityInputs());
    }
    return scannedInputs;
  }

  public QuantityInput getScannedInput()
  {
    List<QuantityInput> scannedInputs = getScannedInputs();
    if (scannedInputs.size() > 1)
    {
      throw new IllegalArgumentException("Can only handle one scanned input");
    }
    QuantityInput scannedInput = scannedInputs.get(0);
    return scannedInput;
  }

  public QuantityOutput.Mode getOutputMode()
  {
    QuantityOutput.Mode mode;
    List<QuantityInput> scannedInputs = getScannedInputs();
    if (scannedInputs.isEmpty())
    {
      mode = QuantityOutput.Mode.NUMERIC_DISPLAY;
    }
    else
    {
      mode = QuantityOutput.Mode.CHECKBOX_DISPLAY;
    }
    return mode;
  }
}
