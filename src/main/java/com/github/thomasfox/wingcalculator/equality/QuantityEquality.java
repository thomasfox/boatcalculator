package com.github.thomasfox.wingcalculator.equality;

import com.github.thomasfox.wingcalculator.calculate.NamedValueSet;
import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class QuantityEquality
{
  private final PhysicalQuantity sourceQuantity;
  private final NamedValueSet sourceSet;
  private final PhysicalQuantity targetQuantity;
}