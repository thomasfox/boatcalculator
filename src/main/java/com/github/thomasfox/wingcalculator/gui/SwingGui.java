package com.github.thomasfox.wingcalculator.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

  private final JButton calculateButton;

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
    quantityInputs.add(new QuantityInput(PhysicalQuantity.VELOCITY, 3d));
    quantityInputs.add(new QuantityInput(PhysicalQuantity.SECOND_MOMENT_OF_AREA, 5E-08));

    addLabelToFrame("Quantity", 0, 0);
    addLabelToFrame("Fixed Value", 1, 0);
    addLabelToFrame("Scan From", 2, 0);
    addLabelToFrame("Scan To", 3, 0);
    addLabelToFrame("Scan Steps", 4, 0);

    int row = 1;
    for (QuantityInput quantityInput : quantityInputs)
    {
      quantityInput.addToFrameInRow(frame, row);
      row++;
    }

    addLabelToFrame("Profile", 0, row);
    profileSelect = new JComboBox<>();
    profileSelect.addItem(null);
    List<String> profiles = profileSelector.getProfileNames(PROFILE_DIRECTORY);
    for (String profile : profiles)
    {
      profileSelect.addItem(profile);
    }
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = row;
    frame.add(profileSelect, gridBagConstraints);
    row++;

    calculateButton = new JButton("Berechnen");
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

  private void addLabelToFrame(String label, int x, int y)
  {
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = x;
    gridBagConstraints.gridy = y;
    frame.add(new JLabel(label), gridBagConstraints);
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
    Object profileNameObject = profileSelect.getSelectedItem();
    String profileName = null;
    if (profileNameObject != null)
    {
      profileName = profileNameObject.toString();
    }
    boolean scan = (profileName == null);
    for (QuantityInput quantityInput : quantityInputs)
    {
      scan = scan || quantityInput.isScan();
    }

    if (scan)
    {
      calculateScan(profileName);
      return;
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

  public void calculateScan(String profileName)
  {
    StringBuilder result = new StringBuilder();

    List<PhysicalQuantity> calculatedQuantities = new ArrayList<>();
    if (profileName != null)
    {
      calculateScan(profileName, calculatedQuantities, new HashMap<>(), quantityInputs, result);
    }
    else
    {
      for (String selectedProfileName : profileSelector.getProfileNames(PROFILE_DIRECTORY))
      {
        calculateScan(selectedProfileName, calculatedQuantities, new HashMap<>(), quantityInputs, result);
      }
    }
    StringBuilder headline = new StringBuilder("Profil;");
    for (PhysicalQuantity calculatedQuantity : calculatedQuantities)
    {
      headline.append(calculatedQuantity.getDisplayNameIncludingUnit()).append(";");
    }
    headline.append("\r\n");

    try (FileWriter resultsWriter = new FileWriter("results.csv"))
    {
      resultsWriter.append(headline);
      resultsWriter.append(result.toString().replaceAll("\\.", ","));
    }
    catch (IOException e1)
    {
      e1.printStackTrace();
    }
  }

  private void calculateScan(String profileName, List<PhysicalQuantity> calculatedQuantities, Map<PhysicalQuantity, Double> fixedQuantities, List<QuantityInput> toScan, StringBuilder result)
  {
    if (toScan.size() == 0)
    {
      Map<PhysicalQuantity, Double> knownQuantities = new HashMap<>(fixedQuantities);
      List<QuantityRelations> quantityRelationsList = new ArrayList<>();
      if (profileName != null)
      {
        Profile profile = profileSelector.loadProfile(PROFILE_DIRECTORY, profileName);
        knownQuantities.put(PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA, profile.getSecondMomentOfArea());
        knownQuantities.put(PhysicalQuantity.WING_RELATIVE_THICKNESS, profile.getThickness());
        quantityRelationsList.addAll(profileSelector.loadXfoilResults(PROFILE_DIRECTORY, profileName));
      }

      CombinedCalculator combinedCalculator = new CombinedCalculator(quantityRelationsList);

      Map<PhysicalQuantity, Double> calculatedValues = combinedCalculator.calculate(knownQuantities);
      knownQuantities.putAll(calculatedValues);
      for (PhysicalQuantity physicalQuantity : knownQuantities.keySet())
      {
        if (!calculatedQuantities.contains(physicalQuantity))
        {
          calculatedQuantities.add(physicalQuantity);
        }
      }
      result.append(profileName).append(";");
      for (PhysicalQuantity physicalQuantity : calculatedQuantities)
      {
        result.append(knownQuantities.get(physicalQuantity)).append(";");
      }
      result.append("\r\n");
      return;
    }
    QuantityInput quantityInput = toScan.get(0);
    Iterator<Double> inputQuantityIterator = quantityInput.getIterator();
    if (inputQuantityIterator == null)
    {
      calculateScan(profileName, calculatedQuantities, fixedQuantities, toScan.subList(1, toScan.size()), result);
    }
    else
    {
      while (inputQuantityIterator.hasNext())
      {
        Double knownValue = inputQuantityIterator.next();
        Map<PhysicalQuantity, Double> newFixedQuantities = new HashMap<>(fixedQuantities);
        newFixedQuantities.put(quantityInput.getQuantity(), knownValue);
        if (!calculatedQuantities.contains(quantityInput.getQuantity()))
        {
          calculatedQuantities.add(quantityInput.getQuantity());
        }
        calculateScan(profileName, calculatedQuantities, newFixedQuantities, toScan.subList(1, toScan.size()), result);
      }
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
