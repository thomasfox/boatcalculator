package com.github.thomasfox.boatcalculator.gui.panel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.github.thomasfox.boatcalculator.gui.panel.part.QuantityOutput;
import com.github.thomasfox.boatcalculator.gui.panel.part.ValueSetOutput;
import com.github.thomasfox.boatcalculator.value.CalculatedPhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

/**
 * Component to output textual results of the calculation.
 */
public class TextResultPanel extends JPanel
{
  private static final long serialVersionUID = 1L;

  private final List<ValueSetOutput> valueSetOutputs = new ArrayList<>();

  public TextResultPanel()
  {
    setLayout(new GridBagLayout());
    setBorder(new EmptyBorder(0,10,0,10));
  }

  public void addToFrame(JFrame frame)
  {
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    JScrollPane scrollPane = new JScrollPane(this);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setPreferredSize(new Dimension(450, 400));
    scrollPane.setMinimumSize(new Dimension(450, 400));
    frame.add(scrollPane, gridBagConstraints);
  }

  public int displayCalculateResultInValueSetOutputs(QuantityOutput.Mode mode, List<ValueSet> valueSets)
  {
    int outputRow = 0;
    for (ValueSet valueSet : valueSets)
    {
      ValueSetOutput valueSetOutput = new ValueSetOutput(valueSet.getId(), valueSet.getDisplayName());
      valueSetOutputs.add(valueSetOutput);
      for (CalculatedPhysicalQuantityValue calculatedValue : valueSet.getCalculatedValues().getAsList())
      {
        if (valueSet.getHiddenOutputs().contains(calculatedValue.getPhysicalQuantity()))
        {
          continue;
        }
        QuantityOutput output = new QuantityOutput(calculatedValue, valueSet.getDisplayName());
        valueSetOutput.add(output);
      }
      outputRow += valueSetOutput.addToContainerInRow(this, outputRow, mode);
    }
    return outputRow;
  }

  public void clear()
  {
    for (ValueSetOutput partOutput : valueSetOutputs)
    {
      partOutput.removeFromContainerAndReset(this);
    }
    valueSetOutputs.clear();
  }

  public Set<PhysicalQuantityInSet> getShownGraphs()
  {
    Set<PhysicalQuantityInSet> result = new HashSet<>();
    for (ValueSetOutput partOutput : valueSetOutputs)
    {
      List<QuantityOutput> shownGraphsInValueSet = partOutput.getShownGraphs();
      for (QuantityOutput quantityOutput : shownGraphsInValueSet)
      {
        result.add(new PhysicalQuantityInSet(quantityOutput.getQuantity(), partOutput.getId()));
      }
    }
    return result;
  }
}
