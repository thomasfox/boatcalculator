package com.github.thomasfox.wingcalculator.gui;

import java.awt.GridBagConstraints;
import java.util.List;
import java.util.Objects;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;

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

  public void addToFrame(JFrame frame, int row)
  {
    SwingHelper.addLabelToFrame("Profil", frame, 0, row);
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = row;
    frame.add(profileSelect, gridBagConstraints);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = row;
    frame.add(scanSelect, gridBagConstraints);
  }

  public String getProfileName()
  {
    return Objects.toString(profileSelect.getSelectedItem(), null);
  }

  public boolean isScan()
  {
    return scanSelect.isSelected();
  }
}
