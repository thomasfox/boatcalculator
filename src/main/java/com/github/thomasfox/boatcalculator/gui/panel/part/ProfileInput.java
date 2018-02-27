package com.github.thomasfox.boatcalculator.gui.panel.part;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.io.File;
import java.util.List;
import java.util.Objects;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import com.github.thomasfox.boatcalculator.gui.SwingGui;
import com.github.thomasfox.boatcalculator.gui.SwingHelper;
import com.github.thomasfox.boatcalculator.interpolate.QuantityRelations;
import com.github.thomasfox.boatcalculator.profile.ProfileGeometry;
import com.github.thomasfox.boatcalculator.profile.ProfileSelector;

public class ProfileInput
{
  private final ProfileSelector profileSelector = new ProfileSelector();

  private final JComboBox<String> profileSelect;

  private final JCheckBox scanSelect;

  public ProfileInput(String profileNameToSelect)
  {
    profileSelect = new JComboBox<>();
    profileSelect.addItem(null);
    List<String> profiles = profileSelector.getProfileNames(SwingGui.PROFILE_DIRECTORY);
    String selectedProfile = null;
    for (String profile : profiles)
    {
      profileSelect.addItem(profile);
      if (Objects.equals(profile, profileNameToSelect))
      {
        selectedProfile = profile;
      }
    }
    scanSelect = new JCheckBox("scan");
    if (selectedProfile != null)
    {
      profileSelect.setSelectedItem(selectedProfile);
    }
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
