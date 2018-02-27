package com.github.thomasfox.boatcalculator.boat.impl;

import static  org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import org.assertj.core.data.Offset;
import org.junit.Test;

import com.github.thomasfox.boatcalculator.boat.impl.Flying29er;
import com.github.thomasfox.boatcalculator.boat.impl.Skiff29er;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.gui.SwingGui;
import com.github.thomasfox.boatcalculator.profile.ProfileGeometry;
import com.github.thomasfox.boatcalculator.profile.ProfileSelector;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.valueset.HasProfile;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;
import com.github.thomasfox.boatcalculator.valueset.impl.BoatGlobalValues;

public class Flying29erTest
{
  Skiff29er sut = new Skiff29er();

  @Test
  public void testCalculateNonFlying()
  {
    sut = new Flying29er();
    PhysicalQuantityValue globalBoatVelocity = calculateVelocity(3d, 45d);
    assertThat(globalBoatVelocity.getValue()).isCloseTo(1.19, Offset.offset(0.01));
  }

  @Test
  public void testCalculateFlying()
  {
    sut = new Flying29er();
    PhysicalQuantityValue globalBoatVelocity = calculateVelocity(4d, 85d);
    assertThat(globalBoatVelocity.getValue()).isCloseTo(5.74, Offset.offset(0.01));
  }

  private PhysicalQuantityValue calculateVelocity(double windSpeed, double pointingAngle)
  {
    sut.getValueSetNonNull(BoatGlobalValues.ID).setStartValueNoOverwrite(new PhysicalQuantityValue(PhysicalQuantity.WIND_SPEED, windSpeed));
    sut.getValueSetNonNull(BoatGlobalValues.ID).setStartValueNoOverwrite(new PhysicalQuantityValue(PhysicalQuantity.POINTING_ANGLE, pointingAngle));

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

    PhysicalQuantityValue globalBoatVelocity = sut.getValueSetNonNull(BoatGlobalValues.ID).getCalculatedValues().getPhysicalQuantityValue(PhysicalQuantity.VELOCITY);
    if (globalBoatVelocity == null)
    {
      fail("Velocity could not be calculated");
    }
    return globalBoatVelocity;
  }
}
