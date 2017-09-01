package com.github.thomasfox.wingcalculator.gui;

import java.awt.FlowLayout;
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
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.github.thomasfox.wingcalculator.boat.Boat;
import com.github.thomasfox.wingcalculator.boat.impl.Skiff29er;
import com.github.thomasfox.wingcalculator.calculate.CombinedCalculator;
import com.github.thomasfox.wingcalculator.calculate.NamedValueSet;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantityValue;
import com.github.thomasfox.wingcalculator.interpolate.QuantityRelations;
import com.github.thomasfox.wingcalculator.profile.Profile;
import com.github.thomasfox.wingcalculator.profile.ProfileSelector;

public class SwingGui
{
  public static final File PROFILE_DIRECTORY = new File("profiles");

  private final JFrame frame = new JFrame("wingCalculator");

  private final JPanel inputPanel = new JPanel();

  private final JPanel resultPanel = new JPanel();

  private final List<PartInput> partInputs = new ArrayList<>();

  private final List<PartOutput> partOutputs = new ArrayList<>();

  private final JButton calculateButton;

  private final ProfileSelector profileSelector = new ProfileSelector();

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

    createPartInput(boat);
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
//      calculateScan(PartInput);
      return;
    }


    int outputRow = 0;
    for (PartOutput partOutput : partOutputs)
    {
      partOutput.removeFromContainerAndReset(resultPanel);
    }
    partOutputs.clear();
    boolean changed;
    int cutoff = 100;

    do
    {
      changed = false;
      for (PartInput partInput : partInputs)
      {
        boolean partChanged = calculateForProfile(partInput.getProfileName(), partInput);
        changed = changed || partChanged;
      }
      for (PartInput partInput : partInputs)
      {
        boolean partChanged = partInput.getValueSet().fillEqualities();
        changed = changed || partChanged;
      }
      cutoff--;
    }
    while (changed && cutoff > 0);


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

  public void calculateScan(PartInput partInput)
  {
    StringBuilder result = new StringBuilder();

    List<PhysicalQuantity> calculatedQuantities = new ArrayList<>();
    if (partInput.getProfileName() != null)
    {
      calculateScan(partInput.getProfileName(), calculatedQuantities, new HashMap<>(), partInput.getQuantityInputs(), result);
    }
    else
    {
      for (String selectedProfileName : profileSelector.getProfileNames(PROFILE_DIRECTORY))
      {
        calculateScan(selectedProfileName, calculatedQuantities, new HashMap<>(), partInput.getQuantityInputs(), result);
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

  private void calculateScan(
      String profileName,
      List<PhysicalQuantity> calculatedQuantities,
      Map<PhysicalQuantity, Double> fixedQuantities,
      List<QuantityInput> toScan,
      StringBuilder result)
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

      Map<PhysicalQuantity, Double> calculatedValues = new HashMap<>();//combinedCalculator.calculate(knownQuantities);
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


  private boolean calculateForProfile(String profileName, PartInput input)
  {
    Map<PhysicalQuantity, Double> knownQuantities = new HashMap<>();
    List<QuantityRelations> quantityRelationsList = new ArrayList<>();
    for (QuantityInput quantityInput : input.getQuantityInputs())
    {
      if (quantityInput.getValue() != null && !input.getValueSet().isValueKnown(quantityInput.getQuantity()))
      {
        input.getValueSet().setStartValueNoOverwrite(quantityInput.getQuantity(), quantityInput.getValue());
      }
    }
    if (profileName != null)
    {
      Profile profile = profileSelector.loadProfile(PROFILE_DIRECTORY, profileName);
      knownQuantities.put(PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA, profile.getSecondMomentOfArea());
      knownQuantities.put(PhysicalQuantity.WING_RELATIVE_THICKNESS, profile.getThickness());
      quantityRelationsList.addAll(profileSelector.loadXfoilResults(PROFILE_DIRECTORY, profileName));
    }

    CombinedCalculator combinedCalculator = new CombinedCalculator(quantityRelationsList);

    return combinedCalculator.calculate(input.getValueSet());
  }
}
