package com.github.thomasfox.sailboatcalculator.part;

import com.github.thomasfox.sailboatcalculator.calculate.value.SimpleValueSet;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class BoatPart extends SimpleValueSet
{
  @NonNull
  private PartType type;

  public BoatPart(PartType type)
  {
    super(type.name(), type.toString());
    this.type = type;
  }

  @Override
  public String getName()
  {
    return type.toString();
  }
}
