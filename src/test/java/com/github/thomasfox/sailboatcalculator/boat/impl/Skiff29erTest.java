package com.github.thomasfox.sailboatcalculator.boat.impl;

import static  org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import org.assertj.core.data.Offset;
import org.junit.Test;

import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.gui.SwingGui;
import com.github.thomasfox.sailboatcalculator.profile.ProfileGeometry;
import com.github.thomasfox.sailboatcalculator.profile.ProfileSelector;
import com.github.thomasfox.sailboatcalculator.value.CalculatedPhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.valueset.ValueSet;
import com.github.thomasfox.sailboatcalculator.valueset.impl.BoatGlobalValues;
import com.github.thomasfox.sailboatcalculator.valueset.impl.Wing;

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

    PhysicalQuantityValue globalBoatVelocity = sut.getValueSetNonNull(BoatGlobalValues.ID).getCalculatedValues().getPhysicalQuantityValue(PhysicalQuantity.VELOCITY);
    if (globalBoatVelocity == null)
    {
      fail("Velocity could not be calculated");
    }
    assertThat(globalBoatVelocity.getValue()).isCloseTo(1.06, Offset.offset(0.01));
  }
}
