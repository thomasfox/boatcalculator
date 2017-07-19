package com.github.thomasfox.wingcalculator.calculate;

public enum PhysicalQuantity
{
  INDUCED_RESISTANCE("Induzierter Widerstand", "N"),
  INDUCED_DRAG_COEFFICIENT("Widerstandsbeiwert f�r den Induzierten Widerstand", null),
  PROFILE_RESISTANCE("Profilwiderstand", "N"),
  PROFILE_DRAG_COEFFICIENT("Widerstandsbeiwert f�r den Profilwiderstand", null),
  LIFT("Auftrieb", "N", "des gesamten Fl�gels"),
  LIFT_COEFFICIENT("Auftriebsbeiwert", null, "gemittelt nach Fl�che �ber den gesamten Fl�gel"),
  REYNOLDS_NUMBER("Reynoldszahl", null),
  WING_WIDTH("Spannweite des Fl�gels", "m", "quer zur Profilierung, von einem Ende zum anderen"),
  WING_DEPTH("Tiefe des Fl�gels", "m", "quer zur Spannweite, gleich �ber die geamte Spannweite"),
  WING_THICKNESS("Dicke des Fl�gels", "m", "Dicke an der dicksten Stelle"),
  WING_VELOCITY("Geschwindigkeit des Fl�gels durch das Wasser", "m/s"),
  KINEMATIC_VISCOSITY("Kinematische Viskosit�t von Wasser", "m^2/s", null, 0.000001d),
  DENSITY("Dichte von Wasser", "kg/m^3", null, 1000d),
  IMMERSION_DEPTH("Eintauchtiefe des Fl�gels", "m", "f�r horizontal verlaufende Fl�gel");

  private String displayName;

  private String unit;

  private String description;

  private Double fixedValue;

  private PhysicalQuantity(String displayName, String unit)
  {
    this.displayName = displayName;

    this.unit = unit;
  }

  private PhysicalQuantity(String displayName, String unit, String description)
  {
    this.displayName = displayName;

    this.unit = unit;

    this.description = description;
  }

  private PhysicalQuantity(String displayName, String unit, String description, Double fixedValue)
  {
    this.displayName = displayName;

    this.unit = unit;

    this.description = description;
  }

  public String getDisplayName()
  {
    return displayName;
  }

  public String getUnit()
  {
    return unit;
  }

  public String getDescription()
  {
    return description;
  }

  public Double getFixedValue()
  {
    return fixedValue;
  }
}
