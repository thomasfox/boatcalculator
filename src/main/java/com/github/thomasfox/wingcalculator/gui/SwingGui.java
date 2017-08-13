package com.github.thomasfox.wingcalculator.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.github.thomasfox.wingcalculator.calculate.CombinedCalculator;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.wingcalculator.interpolate.QuantityRelations;
import com.github.thomasfox.wingcalculator.profile.Profile;
import com.github.thomasfox.wingcalculator.profile.ProfileSelector;

public class SwingGui
{
  private static final File PROFILE_DIRECTORY = new File("profiles");

  JFrame frame = new JFrame("wingCalculator");

  private final List<QuantityInput> quantityInputs = new ArrayList<>();

  private final List<QuantityOutput> quantityOutputs = new ArrayList<>();

  private final JButton selectProfileButton;

  private final JButton allProfilesButton;

  private final ProfileSelector profileSelector = new ProfileSelector();

  private final JComboBox<String> profileSelect;

  private final int rowAfterButton;

  public SwingGui()
  {
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new GridBagLayout());


    quantityInputs.add(new QuantityInput(PhysicalQuantity.LIFT, 200d));
    quantityInputs.add(new QuantityInput(PhysicalQuantity.BENDING_FORCE, 1000d));
    quantityInputs.add(new QuantityInput(PhysicalQuantity.WING_WIDTH, 1.5d));
    quantityInputs.add(new QuantityInput(PhysicalQuantity.WING_DEPTH, null));
    quantityInputs.add(new QuantityInput(PhysicalQuantity.WING_VELOCITY, 3d));
    quantityInputs.add(new QuantityInput(PhysicalQuantity.SECOND_MOMENT_OF_AREA, 5E-08));

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
    List<String> profiles = profileSelector.getProfileNames(PROFILE_DIRECTORY);
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

    selectProfileButton = new JButton("selected Profile");
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = row;
    selectProfileButton.addActionListener(this::selectProfileButtonPressed);
    frame.add(selectProfileButton, gridBagConstraints);
    allProfilesButton = new JButton("all Profiles");
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = row;
    allProfilesButton.addActionListener(this::allProfilesButtonPressed);
    frame.add(allProfilesButton, gridBagConstraints);
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

  public void selectProfileButtonPressed(ActionEvent e)
  {
    Object profileNameObject = profileSelect.getSelectedItem();
    String profileName = null;
    if (profileNameObject != null)
    {
      profileName = profileNameObject.toString();
    }

    Map<PhysicalQuantity, Double> calculatedValues
        = calculateForProfile(profileName);

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

  public void allProfilesButtonPressed(ActionEvent e)
  {
    StringBuilder result = new StringBuilder();

    for (QuantityInput quantityInput : quantityInputs)
    {
      if (quantityInput.getValue() != null)
      {
        result.append(
            quantityInput.getQuantity().getDisplayNameIncludingUnit()).append(":;")
            .append(quantityInput.getValue()).append(";\r\n");
      }
    }
    result.append("\r\n");

    List<PhysicalQuantity> quantities = null;
    for (String profileName : profileSelector.getProfileNames(PROFILE_DIRECTORY))
    {
      Map<PhysicalQuantity, Double> calculatedValues
          = calculateForProfile(profileName);
      if (quantities == null)
      {
        quantities = new ArrayList<>(calculatedValues.keySet());
        Collections.sort(quantities);
        result.append("name").append(";");
        for (PhysicalQuantity quantity : quantities)
        {
          result.append(quantity.getDisplayNameIncludingUnit()).append(";");
        }
        result.append("\r\n");
      }
      result.append(profileName).append(";");
      for (PhysicalQuantity quantity : quantities)
      {
        result.append(calculatedValues.get(quantity)).append(";");
      }
      result.append("\r\n");
    }
    try (FileWriter resultsWriter = new FileWriter("results.csv"))
    {
      resultsWriter.append(result.toString().replaceAll("\\.", ","));
    }
    catch (IOException e1)
    {
      e1.printStackTrace();
    }
  }

  private Map<PhysicalQuantity, Double> calculateForProfile(String profileName)
  {
    Map<PhysicalQuantity, Double> knownQuantities = new HashMap<>();
    List<QuantityRelations> quantityRelationsList = new ArrayList<>();
    for (QuantityInput quantityInput : quantityInputs)
    {
      knownQuantities.put(quantityInput.getQuantity(), quantityInput.getValue());
    }
    if (profileName != null)
    {
      Profile profile = profileSelector.loadProfile(PROFILE_DIRECTORY, profileName);
      knownQuantities.put(PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA, profile.getSecondMomentOfArea());
      knownQuantities.put(PhysicalQuantity.WING_RELATIVE_THICKNESS, profile.getThickness());
      quantityRelationsList.addAll(profileSelector.loadXfoilResults(PROFILE_DIRECTORY, profileName));
    }

    CombinedCalculator combinedCalculator = new CombinedCalculator(quantityRelationsList);

    Map<PhysicalQuantity, Double> calculatedValues = combinedCalculator.calculate(knownQuantities);
    return calculatedValues;
  }
}
