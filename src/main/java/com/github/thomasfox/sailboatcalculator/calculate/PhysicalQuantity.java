package com.github.thomasfox.sailboatcalculator.calculate;

public enum PhysicalQuantity
{
  INDUCED_DRAG("Induzierter Widerstand", "N", true),
  INDUCED_DRAG_COEFFICIENT("Widerstandsbeiwert f�r den Induzierten Widerstand", null, false),
  PROFILE_DRAG("Profilwiderstand", "N", true),
  PROFILE_DRAG_COEFFICIENT("Widerstandsbeiwert f�r den Profilwiderstand", null, false),
  PARASITIC_DRAG("Parasit�rer Widerstand", "N", true),
  PARASITIC_DRAG_COEFFICIENT("Widerstandsbeiwert f�r den parasit�ren Widerstand", null, false),
  WAVE_MAKING_DRAG("Wellenwiderstand", "N", true),
  WAVE_MAKING_DRAG_COEFFICIENT("Widerstandsbeiwert f�r den Wellenwiderstand", null, false),
  TOTAL_DRAG("Gesamtwiderstand", "N", true),
  TOTAL_DRAG_COEFFICIENT("Widerstandsbeiwert f�r den Gesamtwiderstand", null, false),
  LIFT("Auftrieb", "N", true, "des gesamten Fl�gels"),
  LIFT_COEFFICIENT("Auftriebsbeiwert", null, false, "gemittelt nach Fl�che �ber den gesamten Fl�gel"),
  LATERAL_FORCE("Lateralkraft", "N", true),
  DRIVING_FORCE("Vortriebskraft", "N", true),
  BRAKING_FORCE("Bremskraft", "N", true),
  REYNOLDS_NUMBER("Reynoldszahl", null, false),
  WING_SPAN("Spannweite des Fl�gels", "m", null, "quer zur Profilierung, von einem Ende zum anderen"),
  WING_AREA("Fl�che des Fl�gels", "m^2", true),
  WING_CHORD("Tiefe des Fl�gels", "m", null, "quer zur Spannweite, gleich �ber die geamte Spannweite"),
  WING_THICKNESS("Dicke des Fl�gels", "m", null, "Dicke an der dicksten Stelle"),
  WING_RELATIVE_THICKNESS("Relative Dicke des Fl�gels", null, null, "Dicke an der dicksten Stelle geteilt durch Profiltiefe"),
  VELOCITY("Geschwindigkeit", "m/s", false, "Geschwindigkeit der Str�mung"),
  FLOW_DIRECTION("Winkel der Str�mung", "�", false, "Winkel der Str�mung zur Bootsachse"),
  POINTING_ANGLE("Steuerkurs", "�", false, "Winkel zwischen Symmetrieachse des Bootes und Windrichtung"),
  SAILING_ANGLE("Gesegelter Kurs", "�", false, "Winkel zwischen Windrichtung und dem Geschwindigkeitsvektor durch das Wasser"),
  VMG("VMG", "m/s", false, "Komponente der Geschwindigkeit in Windrichtung"),
  APPARENT_WIND_ANGLE("Scheinbare Windrichtung", "�", false, "Winkel zwischen Symmetrieachse des Bootes und Scheinbarer Windrichtung"),
  APPARENT_WIND_SPEED("Scheinbarer Wind", "m/s", false),
  DRIFT_ANGLE("Abdrift", "�", false, "Winkel zwischen Symmetrieachse des Bootes und dem Geschwindigkeitsvektor durch das Wasser"),
  WIND_SPEED("Windgeschwindigkeit", "m/s", false, "Das Wasser wird als ruhendes Bezugssystem genommen"),
  FORCE("Kraft", "N", true),
  SUBMERGENCE_DEPTH("Eintauchtiefe", "m", null),
  FROUDE_NUMBER_SUMBERGENCE("Froudezahl bzgl. Eintauchtiefe", null, false),
  LEVER_BETWEEN_FORCES("Hebelarm", "m", null, "Strecke zwischen zwei gleichstarken entgegengesetzten Kr�ften, ohne festen Drehpunkt"),
  TORQUE_BETWEEN_FORCES("Drehmoment", "nm", null, "Drehmoment verursacht durch zwei gleichstarke entgegengesetzten Kr�ften, ohne festen Drehpunkt"),
  WEIGHT("Gewicht", "kg", true),
  LEVER_WEIGHT("Hebelarm Gewicht", "m", null),
  GRAVITY_ACCELERATION("Fallbeschleunigung", "m/s^2", false, "Im schwerefeld der Erde"),
  KINEMATIC_VISCOSITY("Kinematische Viskosit�t", "m^2/s", false),
  DENSITY("Dichte", "kg/m^3", false),
  IMMERSION_DEPTH("Eintauchtiefe des Fl�gels", "m", false, "f�r horizontal verlaufende Fl�gel"),
  ANGLE_OF_ATTACK("Anstellwinkel", "�", false),
  NCRIT("nCrit", null, null, "The log of the amplification factor of the most-amplified frequency which triggers transition for XFOIL Calculations", 9.0d),
  MODULUS_OF_ELASTICITY("E-Modul", "N/m^2", null, "CFK unidirektional parallel zur Faserrichtung, reduziert wg. Handlaminiert", 100000000000d),
  BENDING_FORCE("Biegekraft", "N", null, "am Ende eines Fl�gels angreifende Kraft. Der Fl�gel ist am anderen Ende fest eingespannt"),
  BENDING("Durchbiegung", "m", null, "am Ende eines Fl�gels. Der Fl�gel ist am anderen Ende fest eingespannt"),
  SECOND_MOMENT_OF_AREA("Fl�chenmoment 2. Ordnung f�r den Fl�gelquerschnitt", "m^4", null, "Integral y^2 dx dy"),
  NORMALIZED_SECOND_MOMENT_OF_AREA("Fl�chenmoment 2. Ordnung", null, false, "Integral y^2 dx dy f�r ein Profil der Tiefe 1 und gleichem Tiefe/Dickenverh�ltnis wie das eigentliche Profil"),
  RIGG_CENTER_OF_EFFORT_HEIGHT("H�he des Rigg-Druckpunktes", "m", null, "gerechnet vom Boden des Bootes (wo das Schwert/der Kiel beginnt)");

  private String displayName;

  private String unit;

  private String description;

  private Double fixedValue;

  /**
   * Whether two parts together have the same value (false)
   * or double the value (true)
   * or whether this question cannot easily be answered (null);
   */
  private Boolean additive;

  private PhysicalQuantity(String displayName, String unit, Boolean additive)
  {
    this.displayName = displayName;
    this.unit = unit;
  }

  private PhysicalQuantity(String displayName, String unit, Boolean additive, String description)
  {
    this.displayName = displayName;
    this.unit = unit;
    this.description = description;
  }

  private PhysicalQuantity(String displayName, String unit, Boolean additive, String description, Double fixedValue)
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

  public Boolean getAdditive()
  {
    return additive;
  }

  public boolean isStrictlyAdditive()
  {
    return additive != null && additive;
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
