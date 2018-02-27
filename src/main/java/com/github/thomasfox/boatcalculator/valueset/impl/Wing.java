package com.github.thomasfox.boatcalculator.valueset.impl;

import com.github.thomasfox.boatcalculator.calculate.MaterialConstants;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.valueset.HasProfile;
import com.github.thomasfox.boatcalculator.valueset.SimpleValueSet;

import lombok.Getter;
import lombok.Setter;

public abstract class Wing extends SimpleValueSet implements HasProfile
{
  @Getter
  @Setter
  private String profileName;

  public Wing(String id, String name)
  {
    super(id, name);
    addToInput(PhysicalQuantity.WING_SPAN);
    setFixedValueNoOverwrite(PhysicalQuantity.NCRIT, 9d);
    setFixedValueNoOverwrite(MaterialConstants.GRAVITY_ACCELERATION);
  }
}
