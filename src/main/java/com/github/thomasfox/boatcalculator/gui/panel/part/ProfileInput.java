package com.github.thomasfox.boatcalculator.gui.panel.part;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.io.File;
import java.util.List;
import java.util.Objects;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.gui.SwingGui;
import com.github.thomasfox.boatcalculator.gui.SwingHelper;
import com.github.thomasfox.boatcalculator.interpolate.QuantityRelations;
import com.github.thomasfox.boatcalculator.profile.ProfileGeometry;
import com.github.thomasfox.boatcalculator.profile.ProfileSelector;

public class ProfileInput implements ScannedInput
{
  private final ProfileSelector profileSelector = new ProfileSelector();

  private final JComboBox<String> profileSelect;

  private final JCheckBox scanSelect;

  List<String> profileNames;

  public ProfileInput(String profileNameToSelect)
  {
    profileSelect = new JComboBox<>();
    profileSelect.addItem(null);
    profileNames = profileSelector.getProfileNames(SwingGui.PROFILE_DIRECTORY);
    String selectedProfile = null;
    for (String profile : profileNames)
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
    profileSelect.setSelectedItem(profileNames.get(step));
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
}
