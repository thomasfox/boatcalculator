package com.github.thomasfox.wingcalculator.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.github.thomasfox.wingcalculator.calculate.CombinedCalculator;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;

public class SwingGui
{
  JFrame frame = new JFrame("wingCalculator");

  private final List<QuantityInput> quantityInputs = new ArrayList<>();

  private final List<QuantityOutput> quantityOutputs = new ArrayList<>();

  private final JButton calculateButton;

  private final CombinedCalculator combinedCalculator = new CombinedCalculator();

  private final int rowAfterButton;

  public SwingGui()
  {
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new GridBagLayout());

    quantityInputs.add(new QuantityInput(PhysicalQuantity.LIFT, 0d));
    quantityInputs.add(new QuantityInput(PhysicalQuantity.WING_WIDTH, 2d));
    quantityInputs.add(new QuantityInput(PhysicalQuantity.WING_VELOCITY, 3d));
    quantityInputs.add(new QuantityInput(PhysicalQuantity.WING_DEPTH, 0.1d));

    int row = 0;
    for (QuantityInput quantityInput : quantityInputs)
    {
      quantityInput.addToFrameInRow(frame, row);
      row++;
    }

    calculateButton = new JButton("Ok");
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = row;
    calculateButton.addActionListener(this::calculateButtonPressed);
    frame.add(calculateButton, gridBagConstraints);
    row++;

    rowAfterButton = row;
    frame.pack();
    frame.setVisible(true);
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
    Map<PhysicalQuantity, Double> knownQuantities = new HashMap<>();
    for (QuantityInput quantityInput : quantityInputs)
    {
      knownQuantities.put(quantityInput.getQuantity(), quantityInput.getValue());
    }
    Map<PhysicalQuantity, Double> calculatedValues = combinedCalculator.calculate(knownQuantities);

    for (QuantityOutput quantityOutput : quantityOutputs)
    {
      quantityOutput.removeFromFrame(frame);
    }
    quantityOutputs.clear();

    int outputRow = 0;
    for (Map.Entry<PhysicalQuantity, Double> calculatedValue : calculatedValues.entrySet())
    {
      QuantityOutput output = new QuantityOutput(calculatedValue.getKey(), calculatedValue.getValue());
      quantityOutputs.add(output);
      output.addToFrameInRow(frame, rowAfterButton + outputRow);
      outputRow++;
    }
    frame.pack();
  }
}
