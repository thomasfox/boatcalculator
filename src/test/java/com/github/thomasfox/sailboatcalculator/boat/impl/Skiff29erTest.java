package com.github.thomasfox.sailboatcalculator.boat.impl;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.github.thomasfox.sailboatcalculator.boat.valueset.BoatGlobalValues;
import com.github.thomasfox.sailboatcalculator.boat.valueset.Wing;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.CalculatedPhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.calculate.value.PhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.calculate.value.ValueSet;
import com.github.thomasfox.sailboatcalculator.gui.SwingGui;
import com.github.thomasfox.sailboatcalculator.profile.ProfileGeometry;
import com.github.thomasfox.sailboatcalculator.profile.ProfileSelector;

public class Skiff29erTest
{
  Skiff29er sut = new Skiff29er();

  @Test
  public void testCalculate()
  {
    sut = new Skiff29er();
    sut.getValueSetNonNull(BoatGlobalValues.ID).setStartValueNoOverwrite(new PhysicalQuantityValue(PhysicalQuantity.WIND_SPEED, 3d));
    sut.getValueSetNonNull(BoatGlobalValues.ID).setStartValueNoOverwrite(new PhysicalQuantityValue(PhysicalQuantity.POINTING_ANGLE, 30d));

    for (ValueSet valueSet : sut.getValueSets())
    {
      if (!(valueSet instanceof Wing))
      {
        continue;
      }
      String profileName = ((Wing) valueSet).getProfileName();
      if (profileName != null)
      {
        ProfileGeometry profileGeometry = new ProfileSelector().loadProfile(
            SwingGui.PROFILE_DIRECTORY, profileName);
        valueSet.setStartValueNoOverwrite(new PhysicalQuantityValue(
            PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA,
            profileGeometry.getSecondMomentOfArea()));
        valueSet.setStartValueNoOverwrite(new PhysicalQuantityValue(
            PhysicalQuantity.WING_RELATIVE_THICKNESS,
            profileGeometry.getThickness()));
        valueSet.getQuantityRelations().addAll(
            new ProfileSelector().loadXfoilResults(
                SwingGui.PROFILE_DIRECTORY, profileName));
      }
    }

    sut.calculate();
    for (CalculatedPhysicalQuantityValue value : sut.getValueSetNonNull(BoatGlobalValues.ID).getCalculatedValues().getAsList())
    {
      System.out.println(value.getPhysicalQuantity() + "=" + value.getValue());
    }
    if (sut.getValueSetNonNull(BoatGlobalValues.ID).getCalculatedValues().getPhysicalQuantityValue(PhysicalQuantity.VELOCITY) == null)
    {
      fail("Velocity could not be calculated");
    }
  }
}
