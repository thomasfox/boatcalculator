package com.github.thomasfox.sailboatcalculator.boat.valueset;

import com.github.thomasfox.sailboatcalculator.calculate.MaterialConstants;
import com.github.thomasfox.sailboatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.sailboatcalculator.calculate.value.SimpleValueSet;

import lombok.Getter;
import lombok.Setter;

public abstract class Wing extends SimpleValueSet
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
