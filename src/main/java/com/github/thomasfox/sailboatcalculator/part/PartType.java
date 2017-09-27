package com.github.thomasfox.sailboatcalculator.part;

public enum PartType
{
  DAGGERBOARD("Schwert"),
  RUDDER("Ruder"),
  RIGG("Rigg"),
  HULL("Rumpf"),
  CREW("Besatzung");

  private String name;

  private PartType(String name)
  {
    this.name = name;
  }

  @Override
  public String toString()
  {
    return name;
  }
}
