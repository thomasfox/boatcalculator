package com.github.thomasfox.boatcalculator.gui.panel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.gui.SwingHelper;
import com.github.thomasfox.boatcalculator.gui.panel.part.ProfileInput;
import com.github.thomasfox.boatcalculator.gui.panel.part.QuantityInput;
import com.github.thomasfox.boatcalculator.gui.panel.part.QuantityOutput;
import com.github.thomasfox.boatcalculator.gui.panel.part.ValueSetInput;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

/**
 * Component to input start values of the calculation.
 */
public class InputPanel extends JPanel
{
  private static final long serialVersionUID = 1L;

  private final JButton calculateButton = new JButton("Berechnen");

  private final JButton scanButton = new JButton("Diagramme anzeigen");

  private final JButton saveResultsButton = new JButton("Ergebnis Speichern");

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

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridBagLayout());
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = row;
    gridBagConstraints.gridwidth = 5;
    add(buttonPanel, gridBagConstraints);

    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    buttonPanel.add(calculateButton, gridBagConstraints);

    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    scanButton.setVisible(false);
    buttonPanel.add(scanButton, gridBagConstraints);

    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    saveResultsButton.setVisible(false);
    buttonPanel.add(saveResultsButton, gridBagConstraints);

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
    JScrollPane scrollPane = new JScrollPane(this);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setPreferredSize(new Dimension(480, 800));
    scrollPane.setMinimumSize(new Dimension(400, 400));
    frame.add(scrollPane, gridBagConstraints);
  }

  public void addCalculateButtonActionListener(ActionListener actionListener)
  {
    calculateButton.addActionListener(actionListener);
  }

  public void addScanButtonActionListener(ActionListener actionListener)
  {
    scanButton.addActionListener(actionListener);
  }

  public void addSaveResultsButtonActionListener(ActionListener actionListener)
  {
    saveResultsButton.addActionListener(actionListener);
  }

  public void setScanButtonVisible(boolean visible)
  {
    scanButton.setVisible(visible);
  }

  public void setSaveResultsButtonVisible(boolean visible)
  {
    saveResultsButton.setVisible(visible);
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

  private boolean hasScannedInput()
  {
    if (getScannedQuantityInputs().size() > 0)
    {
      return true;
    }
    if (getScannedProfileInputs().size() > 0)
    {
      return true;
    }
    return false;
  }

  public Map<String, ProfileInput> getScannedProfileInputs()
  {
    Map<String, ProfileInput> result = new LinkedHashMap<>();
    for (ValueSetInput valueSetInput : valueSetInputs)
    {
      ProfileInput profileInput = valueSetInput.getProfileInput();
      if (profileInput != null && profileInput.isScan())
      {
        result.put(valueSetInput.getValueSet().getId(), profileInput);
      }
    }
    return result;
  }

  public ProfileInput getScannedProfileInput()
  {
    Map<String, ProfileInput> scannedInputs = getScannedProfileInputs();
    if (scannedInputs.size() > 1)
    {
      throw new IllegalArgumentException("Can only handle one scanned input");
    }
    if (!scannedInputs.isEmpty())
    {
      ProfileInput scannedInput = scannedInputs.values().iterator().next();
      return scannedInput;
    }
    return null;
  }

  public List<QuantityInput> getScannedQuantityInputs()
  {
    List<QuantityInput> result = new ArrayList<>();
    for (ValueSetInput valueSetInput : valueSetInputs)
    {
      result.addAll(valueSetInput.getScannedQuantityInputs());
    }
    if (result.size() > 2)
    {
      throw new IllegalArgumentException("Can handle at most two scanned inputs");
    }
    return result;
  }

  public QuantityOutput.Mode getOutputMode()
  {
    if (!hasScannedInput())
    {
      return QuantityOutput.Mode.NUMERIC_DISPLAY;
    }
    else
    {
      return QuantityOutput.Mode.CHECKBOX_DISPLAY;
    }
  }
}
