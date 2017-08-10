package com.github.thomasfox.wingcalculator.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.github.thomasfox.wingcalculator.calculate.CombinedCalculator;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.wingcalculator.profile.Profile;
import com.github.thomasfox.wingcalculator.profile.ProfileSelector;

public class SwingGui
{
  JFrame frame = new JFrame("wingCalculator");

  private final List<QuantityInput> quantityInputs = new ArrayList<>();

  private final List<QuantityOutput> quantityOutputs = new ArrayList<>();

  private final JButton calculateButton;

  private final ProfileSelector profileSelector = new ProfileSelector();

  private final JComboBox<String> profileSelect;

  private final CombinedCalculator combinedCalculator = new CombinedCalculator();

  private final int rowAfterButton;

  public SwingGui()
  {
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new GridBagLayout());


    quantityInputs.add(new QuantityInput(PhysicalQuantity.LIFT, 1000d));
    quantityInputs.add(new QuantityInput(PhysicalQuantity.BENDING_FORCE, 1000d));
    quantityInputs.add(new QuantityInput(PhysicalQuantity.WING_WIDTH, 2d));
    quantityInputs.add(new QuantityInput(PhysicalQuantity.WING_DEPTH, 0.1d));
    quantityInputs.add(new QuantityInput(PhysicalQuantity.WING_VELOCITY, 3d));

    int row = 0;
    for (QuantityInput quantityInput : quantityInputs)
    {
      quantityInput.addToFrameInRow(frame, row);
      row++;
    }

    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = row;
    frame.add(new JLabel("Profil"), gridBagConstraints);
    profileSelect = new JComboBox<>();
    profileSelect.addItem(null);
    List<String> profiles = profileSelector.getProfileNames(new File("profiles"));
    for (String profile : profiles)
    {
      profileSelect.addItem(profile);
    }
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = row;
    frame.add(profileSelect, gridBagConstraints);
    row++;

    calculateButton = new JButton("Ok");
    gridBagConstraints = new GridBagConstraints();
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
    Object profileName = profileSelect.getSelectedItem();
    if (profileName != null)
    {
      Profile profile = profileSelector.load(new File("profiles"), (String) profileName);
      knownQuantities.put(PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA, profile.getSecondMomentOfArea());
      knownQuantities.put(PhysicalQuantity.WING_RELATIVE_THICKNESS, profile.getThickness());
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
