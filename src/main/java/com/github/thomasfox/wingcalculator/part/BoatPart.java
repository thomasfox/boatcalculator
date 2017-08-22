package com.github.thomasfox.wingcalculator.part;

import com.github.thomasfox.wingcalculator.calculate.NamedValueSet;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class BoatPart extends NamedValueSet
{
  @NonNull
  private PartType type;

  @Override
  public String getName()
  {
    return type.toString();
  }
}
