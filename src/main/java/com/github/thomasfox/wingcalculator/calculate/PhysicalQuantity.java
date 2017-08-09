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
  IMMERSION_DEPTH("Eintauchtiefe des Fl�gels", "m", "f�r horizontal verlaufende Fl�gel"),
  ANGLE_OF_ATTACK("Anstellwinkel", "�"),
  NCRIT("The log of the amplification factor of the most-amplified frequency which triggers transition", null, "f�r XFOIL-Berechnungen"),
  MODULUS_OF_ELASTICITY("E-Modul", "N/m^2"),
  BENDING_FORCE("Biegekraft", "N", "am Ende eines Fl�gels angreifende Kraft. Der Fl�gel ist am anderen Ende fest eingepsannt"),
  SECOND_MOMENT_OF_AREA("Fl�chenmoment 2. Ordnung f�r den Fl�gelquerschnitt", "m^4", "Integral y^2 dx dy"),
  NORMALIZED_SECOND_MOMENT_OF_AREA("Fl�chenmoment 2. Ordnung", null, "Integral y^2 dx dy f�r ein Profil der Tiefe 1 und gleichem Tiefe/Dickenverh�ltnis wie das eigentliche Profil");

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
    this.fixedValue = fixedValue;
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

  public String getDisplayNameIncludingUnit()
  {
    String result = displayName;
    if (unit != null)
    {
      result += " [" + unit + "]";
    }
    return result;
  }
}
