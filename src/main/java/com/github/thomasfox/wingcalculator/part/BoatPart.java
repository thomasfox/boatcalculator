package com.github.thomasfox.wingcalculator.part;

import com.github.thomasfox.wingcalculator.calculate.NamedValueSet;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class BoatPart extends NamedValueSet
{
  @NonNull
  private PartType type;

  public BoatPart(PartType type)
  {
    super(type.toString());
    this.type = type;
  }

  @Override
  public String getName()
  {
    return type.toString();
  }
}
