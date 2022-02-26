package com.github.thomasfox.boatcalculator.boat.impl;

import static  org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import org.assertj.core.data.Offset;
import org.junit.Test;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.gui.SwingGui;
import com.github.thomasfox.boatcalculator.profile.ProfileGeometry;
import com.github.thomasfox.boatcalculator.profile.ProfileSelector;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.valueset.HasProfile;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;
import com.github.thomasfox.boatcalculator.valueset.impl.BoatGlobalValues;

public class Skiff29erTest
{
  Skiff29er sut = new Skiff29er();

  @Test
  public void testCalculate()
  {
    sut = new Skiff29er();
    sut.getValueSetNonNull(BoatGlobalValues.ID).setStartValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.WIND_SPEED, 3d));
    sut.getValueSetNonNull(BoatGlobalValues.ID).setStartValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.POINTING_ANGLE, 30d));

    for (ValueSet valueSet : sut.getValueSets())
    {
      if (!(valueSet instanceof HasProfile))
      {
        continue;
      }
      String profileName = ((HasProfile) valueSet).getProfileName();
      if (profileName != null)
      {
        ProfileGeometry profileGeometry = new ProfileSelector().loadProfile(
            SwingGui.PROFILE_DIRECTORY, profileName);
        valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(
            PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA,
            profileGeometry.getSecondMomentOfArea()));
        valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(
            PhysicalQuantity.WING_RELATIVE_THICKNESS,
            profileGeometry.getThickness()));
        valueSet.getQuantityRelations().addAll(
            new ProfileSelector().loadXfoilResults(
                SwingGui.PROFILE_DIRECTORY, profileName));
      }
    }

    sut.calculate();

    PhysicalQuantityValue globalBoatVelocity = sut.getValueSetNonNull(BoatGlobalValues.ID).getCalculatedValues().getPhysicalQuantityValue(PhysicalQuantity.VELOCITY);
    if (globalBoatVelocity == null)
    {
      fail("Velocity could not be calculated");
    }
    assertThat(globalBoatVelocity.getValue()).isCloseTo(1.06, Offset.offset(0.01));
  }
}
