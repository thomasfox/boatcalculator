package com.github.thomasfox.wingcalculator.gui;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.io.File;
import java.util.List;
import java.util.Objects;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import com.github.thomasfox.wingcalculator.interpolate.QuantityRelations;
import com.github.thomasfox.wingcalculator.profile.ProfileGeometry;
import com.github.thomasfox.wingcalculator.profile.ProfileSelector;

public class ProfileInput
{
  private final ProfileSelector profileSelector = new ProfileSelector();

  private final JComboBox<String> profileSelect;

  private final JCheckBox scanSelect;

  public ProfileInput()
  {
    profileSelect = new JComboBox<>();
    profileSelect.addItem(null);
    List<String> profiles = profileSelector.getProfileNames(SwingGui.PROFILE_DIRECTORY);
    for (String profile : profiles)
    {
      profileSelect.addItem(profile);
    }
    scanSelect = new JCheckBox("scan");
  }

  public void addToContainer(Container container, int row)
  {
    SwingHelper.addLabelToContainer("Profil", container, 0, row);
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = row;
    container.add(profileSelect, gridBagConstraints);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = row;
    container.add(scanSelect, gridBagConstraints);
  }

  public String getProfileName()
  {
    return Objects.toString(profileSelect.getSelectedItem(), null);
  }

  public boolean isScan()
  {
    return scanSelect.isSelected();
  }

  public ProfileGeometry loadProfile(File directory, String name)
  {
    return profileSelector.loadProfile(directory, name);
  }

  public List<QuantityRelations> loadXfoilResults(File directory, String name)
  {
    return profileSelector.loadXfoilResults(directory, name);
  }

}
