package com.github.thomasfox.boatcalculator.gui.panel.part;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.gui.SwingGui;
import com.github.thomasfox.boatcalculator.gui.SwingHelper;
import com.github.thomasfox.boatcalculator.interpolate.QuantityRelations;
import com.github.thomasfox.boatcalculator.profile.ProfileGeometry;
import com.github.thomasfox.boatcalculator.profile.ProfileSelector;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ProfileInput implements ScannedInput
{
  private final ProfileSelector profileSelector = new ProfileSelector();

  private final JComboBox<ProfileInputItem> profileSelect;

  private final JCheckBox scanSelect;

  List<String> profileNames;

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
  }

  public void addToContainer(Container container, int row)
  {
    SwingHelper.addLabelToContainer("Profil", container, 0, row);

    JPanel profileSelectPanel = new JPanel();
    profileSelectPanel.setLayout(new GridBagLayout());
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = row;
    gridBagConstraints.gridwidth = 4;
    container.add(profileSelectPanel, gridBagConstraints);

    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    profileSelectPanel.add(profileSelect, gridBagConstraints);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.insets = new Insets(0,2,0,2);
    profileSelectPanel.add(scanSelect, gridBagConstraints);
  }

  public String getProfileName()
  {
    return Optional.ofNullable((ProfileInputItem) profileSelect.getSelectedItem())
        .map(ProfileInputItem::getProfileName)
        .orElse(null);
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

  public List<QuantityRelations> loadXfoilResults(File directory, String name)
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
    return profileNames.size();
  }

  @Override
  public void setValueForScanStep(int step)
  {
    profileSelect.setSelectedItem(profileSelect.getItemAt(step));
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
}
