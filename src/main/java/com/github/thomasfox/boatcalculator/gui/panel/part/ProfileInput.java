package com.github.thomasfox.boatcalculator.gui.panel.part;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.gui.SwingGui;
import com.github.thomasfox.boatcalculator.interpolate.QuantityRelation;
import com.github.thomasfox.boatcalculator.profile.ProfileGeometry;
import com.github.thomasfox.boatcalculator.profile.ProfileSelector;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ProfileInput implements ScannedInput
{
  private final ProfileSelector profileSelector = new ProfileSelector();

  private final JComboBox<ProfileInputItem> profileSelect;

  private final JCheckBox scanSelect;

  private final JComboBox<Symmetry> symmetrySelect;

  List<String> profileNames;

  List<String> symmetricProfileNames;

  public ProfileInput(String profileNameToSelect)
  {
    profileSelect = new JComboBox<>();
    profileSelect.addItem(null);
    profileNames = profileSelector.getProfileNames(SwingGui.PROFILE_DIRECTORY);
    ProfileInputItem selectedProfile = null;
    int i = 0;
    for (String profile : profileNames)
    {
      i++;
      ProfileInputItem item = new ProfileInputItem(profile, i);
      profileSelect.addItem(item);
      if (Objects.equals(profile, profileNameToSelect))
      {
        selectedProfile = item;
      }
    }
    scanSelect = new JCheckBox("scan");
    if (selectedProfile != null)
    {
      profileSelect.setSelectedItem(selectedProfile);
    }
    symmetrySelect = new JComboBox<Symmetry>();
    symmetrySelect.addItem(Symmetry.ALL);
    symmetrySelect.addItem(Symmetry.SYMMETRIC);
    symmetrySelect.addItem(Symmetry.ASSYMMERTRIC);
  }

  public void addToContainer(Container container, int row)
  {
    JPanel profileSelectPanel = new JPanel();
    profileSelectPanel.setLayout(new GridBagLayout());
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = row;
    gridBagConstraints.gridwidth = 5;
    container.add(profileSelectPanel, gridBagConstraints);

    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    JLabel labelComponent = new JLabel("Profil");
    labelComponent.setBorder(new EmptyBorder(0, 0, 0, 30));
    profileSelectPanel.add(labelComponent, gridBagConstraints);

    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    profileSelectPanel.add(profileSelect, gridBagConstraints);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.insets = new Insets(0,2,0,2);
    profileSelectPanel.add(scanSelect, gridBagConstraints);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 0;
    profileSelectPanel.add(symmetrySelect, gridBagConstraints);
  }

  public String getProfileName()
  {
    return Optional.ofNullable((ProfileInputItem) profileSelect.getSelectedItem())
        .map(ProfileInputItem::getProfileName)
        .orElse(null);
  }

  public List<String> getSymmetricProfileNames()
  {
    if (symmetricProfileNames == null)
    {
      symmetricProfileNames = new ArrayList<>();
      for (String profileName : profileNames)
      {
        ProfileGeometry geometry = loadProfile(SwingGui.PROFILE_DIRECTORY, profileName);
        if (geometry != null && geometry.isSymmetric())
        {
          symmetricProfileNames.add(profileName);
        }
      }
    }
    return symmetricProfileNames;
  }

  public List<String> getAsymmetricProfileNames()
  {
    List<String> result = new ArrayList<>(profileNames);
    result.removeAll(getSymmetricProfileNames());
    return result;
  }


  @Override
  public boolean isScan()
  {
    return scanSelect.isSelected();
  }

  public ProfileGeometry loadProfile(File directory, String name)
  {
    return profileSelector.loadProfile(directory, name);
  }

  public List<QuantityRelation> loadXfoilResults(File directory, String name)
  {
    return profileSelector.loadXfoilResults(directory, name);
  }

  @Override
  public Integer getNumberOfScanSteps()
  {
    if (!isScan())
    {
      return null;
    }
    if (symmetrySelect.getSelectedItem() == Symmetry.SYMMETRIC)
    {
      return getSymmetricProfileNames().size();
    }
    else if (symmetrySelect.getSelectedItem() == Symmetry.ASSYMMERTRIC)
    {
      return getAsymmetricProfileNames().size();
    }
    else
    {
      return profileNames.size();
    }
  }

  @Override
  public void setValueForScanStep(int step)
  {
    if (symmetrySelect.getSelectedItem() == Symmetry.SYMMETRIC)
    {
      String profileName = getSymmetricProfileNames().get(step);
      selectProfileName(profileName);
    }
    else if (symmetrySelect.getSelectedItem() == Symmetry.ASSYMMERTRIC)
    {
      String profileName = getAsymmetricProfileNames().get(step);
      selectProfileName(profileName);
    }
    else
    {
      String profileName = profileNames.get(step);
      selectProfileName(profileName);
    }
  }

  private void selectProfileName(String profileName)
  {
    for (int i = 0; i < profileSelect.getItemCount(); i++)
    {
      ProfileInputItem candidate = profileSelect.getItemAt(i);
      if (candidate != null && profileName.equals(candidate.getProfileName()))
      {
        profileSelect.setSelectedItem(candidate);
        return;
      }
    }
  }

  @Override
  public String getScanStepDescription(int step)
  {
    return profileNames.get(step);
  }

  @Override
  public String getScanDescription()
  {
    return "profile scan: ";
  }

  @Override
  public Number getScanXValue(int step)
  {
    return step;
  }

  @Override
  public PhysicalQuantity getQuantity()
  {
    return PhysicalQuantity.PROFILE;
  }

  @AllArgsConstructor
  @Getter
  private class ProfileInputItem
  {
    private final String profileName;

    private final int index;

    @Override
    public String toString()
    {
      return profileName + " (" + index + ")";
    }
  }

  private static enum Symmetry {
    ALL,
    SYMMETRIC,
    ASSYMMERTRIC
  }
}
