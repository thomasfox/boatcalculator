package com.github.thomasfox.wingcalculator.gui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.github.thomasfox.wingcalculator.boat.Boat;
import com.github.thomasfox.wingcalculator.boat.impl.Skiff29er;
import com.github.thomasfox.wingcalculator.calculate.NamedValueSet;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantityValue;

public class SwingGui
{
  public static final File PROFILE_DIRECTORY = new File("profiles");

  public static final File HULL_DIRECTORY = new File("hulls");

  private final JFrame frame = new JFrame("wingCalculator");

  private final JPanel inputPanel = new JPanel();

  private final JPanel resultPanel = new JPanel();

  private final List<PartInput> partInputs = new ArrayList<>();

  private final List<PartOutput> partOutputs = new ArrayList<>();

  private final JButton calculateButton;

  private final int rowAfterButton;

  private final Boat boat = new Skiff29er();

  public SwingGui()
  {
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new FlowLayout());

    inputPanel.setLayout(new GridBagLayout());
    frame.add(inputPanel);

    resultPanel.setLayout(new GridBagLayout());
    frame.add(resultPanel);

    for (NamedValueSet namedValueSet : boat.getNamedValueSets())
    {
      createPartInput(namedValueSet);
    }

    int row = 0;
    for (PartInput partInput : partInputs)
    {
      row += partInput.addToContainerInRow(inputPanel, row);
    }

    SwingHelper.addSeparatorToContainer(inputPanel, row++, 5);

    calculateButton = new JButton("Berechnen");
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = row;
    calculateButton.addActionListener(this::calculateButtonPressed);
    inputPanel.add(calculateButton, gridBagConstraints);

    row++;

    rowAfterButton = row;
    frame.pack();
    frame.setVisible(true);
  }

  private void createPartInput(NamedValueSet valueSet)
  {
    PartInput partInput = new PartInput(valueSet);
    partInputs.add(partInput);
    for (PhysicalQuantity physicalQuantity : valueSet.getToInput())
    {
      if (valueSet.getFixedValue(physicalQuantity) == null)
      {
        partInput.add(new QuantityInput(physicalQuantity, valueSet.getStartValue(physicalQuantity)));
      }
    }
  }

  public static void main(String[] args)
  {
    javax.swing.SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run() {
        new SwingGui();
      }
    });
  }

  public void calculateButtonPressed(ActionEvent e)
  {
    boolean scan = false;
    for (PartInput partInput : partInputs)
    {
      scan = scan || partInput.isScan();
    }

    if (scan)
    {
      throw new IllegalStateException("scan not yet implemented");
    }


    int outputRow = 0;
    for (PartOutput partOutput : partOutputs)
    {
      partOutput.removeFromContainerAndReset(resultPanel);
    }
    partOutputs.clear();

    for (PartInput partInput : partInputs)
    {
      partInput.getValueSet().clearCalculatedValues();
      partInput.applyStartValues();
      partInput.applyProfile();
    }

    boat.calculate();

    for (PartInput partInput : partInputs)
    {
      PartOutput partOutput = new PartOutput(partInput.getValueSet().getName());
      partOutputs.add(partOutput);
      for (PhysicalQuantityValue calculatedValue : partInput.getValueSet().getCalculatedValues().getAsList())
      {
        QuantityOutput output = new QuantityOutput(calculatedValue.getPhysicalQuantity(), calculatedValue.getValue());
        partOutput.getQuantityOutputs().add(output);
      }
      outputRow += partOutput.addToContainerInRow(resultPanel, rowAfterButton + outputRow);
    }
    frame.pack();
  }
}
