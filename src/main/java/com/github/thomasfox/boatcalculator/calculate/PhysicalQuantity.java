package com.github.thomasfox.boatcalculator.calculate;

public enum PhysicalQuantity
{
  INDUCED_DRAG("Induzierter Widerstand", "N", true),
  INDUCED_DRAG_COEFFICIENT("Widerstandsbeiwert Induzierter Widerstand", null, false),
  PROFILE_DRAG("Profilwiderstand", "N", true),
  PROFILE_DRAG_COEFFICIENT("Widerstandsbeiwert Profilwiderstand", null, false),
  PARASITIC_DRAG("Parasit�rer Widerstand", "N", true),
  PARASITIC_DRAG_COEFFICIENT("Widerstandsbeiwert parasit. Widerstand", null, false),
  WAVE_MAKING_DRAG("Wellenwiderstand", "N", true),
  WAVE_MAKING_DRAG_COEFFICIENT("Widerstandsbeiwert Wellenwiderstand", null, false),
  SURFACE_PIERCING_DRAG("Oberfl�chen-Widerstand", "N", true, "Widerstand durch Spray und Wellen, weil Tragfl�che die Wasseroberfl�che durchst��t"),
  SURFACE_PIERCING_DRAG_COEFFICIENT("Oberfl�chen-Widerstandskoeffizient", null, false),
  TOTAL_DRAG("Gesamtwiderstand", "N", true),
  TOTAL_DRAG_COEFFICIENT("Widerstandsbeiwert Gesamtwiderstand", null, false),
  LIFT("Auftrieb", "N", true),
  LIFT_COEFFICIENT("Auftriebsbeiwert", null, false, "2D, gemittelt nach Fl�che �ber den gesamten Fl�gel"),
  LIFT_COEFFICIENT_3D("3D-Auftriebsbeiwert", null, false, "korrigiert um endliche Spannweite"),
  LIFT_DIVIDED_BY_TOTAL_DRAG("Cl/Cd", null, false),
  LATERAL_FORCE("Lateralkraft", "N", true),
  DRIVING_FORCE("Vortriebskraft", "N", true),
  BRAKING_FORCE("Bremskraft", "N", true),
  FORWARD_FORCE("Gesamtkraft Vorw�rts", "N", true),
  REYNOLDS_NUMBER("Reynoldszahl", null, false),
  HALFWING_SPAN("Halbe Spannweite des Fl�gels", "m", false, "quer zur Profilierung, von einem Ende zum anderen, ein Ende ist die Mitte"),
  WING_SPAN("Spannweite des Fl�gels", "m", false, "quer zur Profilierung, von einem Ende zum anderen"),
  WING_SPAN_IN_MEDIUM("Spannweite im Medium", "m", false, "quer zur Profilierung, die L�nge die im dichten Medium (Wasser) ist"),
  AREA_IN_MEDIUM("Fl�che im Medium", "m^2", true),
  WING_CHORD("Tiefe des Fl�gels", "m", false, "quer zur Spannweite, gleich �ber die geamte Spannweite"),
  WING_INNER_CHORD("Innere Tiefe des Fl�gels", "m", false, "quer zur Spannweite, f�r einen trapezf�rmigen Fl�gel"),
  WING_OUTER_CHORD("�u�ere Tiefe des Fl�gels", "m", false, "quer zur Spannweite, f�r einen trapezf�rmigen Fl�gel"),
  WING_INNER_OUTER_CHORD_RATIO("Verh�ltnis innerer zu �u�erer Tiefe des Fl�gels", null, false, "quer zur Spannweite, f�r einen trapezf�rmigen Fl�gel"),
  TRAPEZOIDAL_WING_BENDING_CORRECTION_FACTOR("Korrekturfaktor f�r Durchbiegung bzgl. innerre Tiefe", null, false, "quer zur Spannweite, f�r einen trapezf�rmigen Fl�gel"),
  SEMIWING_ASPECT_RATIO("Halbfl�gelstreckung", null, false),
  WING_THICKNESS("Dicke des Fl�gels", "m", false, "Dicke an der dicksten Stelle"),
  WING_RELATIVE_THICKNESS("Relative Dicke des Fl�gels", null, false, "Dicke an der dicksten Stelle geteilt durch Profiltiefe"),
  VELOCITY("Geschwindigkeit", "m/s", false, "Geschwindigkeit der Str�mung"),
  FLOW_DIRECTION("Winkel der Str�mung", "�", false, "Winkel der Str�mung zur Bootsachse"),
  POINTING_ANGLE("Steuerkurs", "�", false, "Winkel zwischen Symmetrieachse des Bootes und Windrichtung"),
  SAILING_ANGLE("Gesegelter Kurs", "�", false, "Winkel zwischen Windrichtung und dem Geschwindigkeitsvektor durch das Wasser"),
  VMG("VMG", "m/s", false, "Komponente der Geschwindigkeit in Windrichtung"),
  APPARENT_WIND_ANGLE("Scheinbare Windrichtung", "�", false, "Winkel zwischen Symmetrieachse des Bootes und Scheinbarer Windrichtung"),
  APPARENT_WIND_SPEED("Scheinbarer Wind", "m/s", false),
  // die folgenden 2 Punkte bechreiben die Abweichung einer Fl�che zur Horizontalen
  // die X-Achse ist nach hinten, die X-Achse seitw�rts und die Z-Aches nach oben
  // Erst wird die Fl�chennormale aus der Z-Richtung hinaus in der X-Y-Ebene um den seitlichen Kippwinkel gedreht,
  // und dann wird sie in Richtung X-Achse um den R�ckw�rts-Kippwinkel gedreht.
  DRIFT_ANGLE("Abdrift", "�", false, "Winkel zwischen Symmetrieachse des Bootes und dem Geschwindigkeitsvektor durch das Wasser"),
  SIDEWAY_ANGLE("Seitlicher Kippwinkel", "�", false, "wieviel der Fl�gel/die Fl�che um die X-Achse im Vergleich zur Horizontalen gedreht ist"),
  BACKWAY_ANGLE("R�ckw�rts-Kippwinkel", "�", false, "wieviel der Fl�gel/die Fl�che um die um dem Kippwinkel gedrehte Y-Achse im Vergleich zur Horizontalen gedreht ist"),
  MAX_WINDWARD_HEEL_ANGLE("Max. Lee-Kr�ngungswinkel", "�", false),
  WINDWARD_HEEL_ANGLE("Lee-Kr�ngungswinkel", "�", false),
  WIND_SPEED("Windgeschwindigkeit", "m/s", false, "Das Wasser wird als ruhendes Bezugssystem genommen"),
  FORCE("Kraft", "N", true),
  SUBMERGENCE_DEPTH("Eintauchtiefe", "m", false),
  FROUDE_NUMBER_SUMBERGENCE("Froudezahl bzgl. Eintauchtiefe", null, false),
  LEVER_BETWEEN_FORCES("Hebelarm", "m", null, "Strecke zwischen zwei gleichstarken entgegengesetzten Kr�ften, ohne festen Drehpunkt"),
  TORQUE_BETWEEN_FORCES("Drehmoment", "nm", null, "Drehmoment verursacht durch zwei gleichstarke entgegengesetzten Kr�ften, ohne festen Drehpunkt"),
  MASS("Masse", "kg", true),
  WEIGHT("Gewichtskraft", "N", true),
  LEVER_WEIGHT("Hebelarm Gewicht", "m", null),
  MAX_LEVER_WEIGHT("Max. Hebelarm Gewicht", "m", null),
  GRAVITY_ACCELERATION("Fallbeschleunigung", "m/s^2", false, "Im schwerefeld der Erde"),
  KINEMATIC_VISCOSITY("Kinematische Viskosit�t", "m^2/s", false),
  DENSITY("Dichte", "kg/m^3", false),
  TEMPERATURE("Temperatur", "�C", false),
  IMMERSION_DEPTH("Eintauchtiefe des Fl�gels", "m", false, "f�r horizontal verlaufende Fl�gel"),
  ANGLE_OF_ATTACK("Anstellwinkel", "�", false),
  MAX_ANGLE_OF_ATTACK("maximaler Anstellwinkel", "�", false, "wenn der Anstellwinkel nicht fest ist"),
  NCRIT("nCrit", null, null, "The log of the amplification factor of the most-amplified frequency which triggers transition for XFOIL Calculations", 9.0d),
  MODULUS_OF_ELASTICITY("E-Modul", "N/m^2", false, "CFK unidirektional parallel zur Faserrichtung, reduziert wg. Handlaminiert", 100000000000d),
  BENDING_FORCE("Biegekraft", "N", true, "am Ende eines Fl�gels angreifende Kraft. Der Fl�gel ist am anderen Ende fest eingespannt"),
  BENDING("Durchbiegung", "m", false, "am Ende eines Fl�gels. Der Fl�gel ist am anderen Ende fest eingespannt"),
  SECOND_MOMENT_OF_AREA("Fl�chenmoment 2.Ordnung", "m^4", false, "Integral y^2 dx dy"),
  NORMALIZED_SECOND_MOMENT_OF_AREA("Fl�chenmoment 2. Ordnung", null, false, "Integral y^2 dx dy f�r ein Profil der Tiefe 1 und gleichem Tiefe/Dickenverh�ltnis wie das eigentliche Profil"),
  AREA_OF_CROSSECTION("Fl�che des Querschnitts", "m^2", false),
  NORMALIZED_AREA_OF_CROSSECTION("Normalisierte Fl�che des Querschnitts", "m^2", false, "Querschnittsfl�che f�r ein Profil der Tiefe 1 und gleichem Tiefe/Dickenverh�ltnis wie das eigentliche Profil"),
  MAX_RELATIVE_CAMBER("Maximale relative Assymetrie", null, false, "Maximale relative Abweichung des Mittels von Ober- und Unterseite von der Nullinie des Profils bzgl der maximalen Dicke"),
  CENTER_OF_EFFORT_HEIGHT("H�he des Druckpunktes", "m", false, "gerechnet vom Boden des Bootes (wo das Schwert/der Kiel beginnt)"),
  RIDING_HEIGHT("Flugh�he", "m", null, "Abstand Wasseroberfl�che zum Boden des Bootes (wo das Schwert beginnt)"),
  PROFILE("Profil", null, false);

  private final String displayName;

  private final String unit;

  private final String description;

  private final Double fixedValue;

  /**
   * Whether two parts together have the same value (false)
   * or double the value (true)
   * or whether this question cannot easily be answered (null);
   */
  private final Boolean additive;

  PhysicalQuantity(String displayName, String unit, Boolean additive)
  {
    this.displayName = displayName;
    this.unit = unit;
    this.additive = additive;
    this.description = null;
    this.fixedValue = null;
  }

  PhysicalQuantity(String displayName, String unit, Boolean additive, String description)
  {
    this.displayName = displayName;
    this.unit = unit;
    this.description = description;
    this.additive = additive;
    this.fixedValue = null;
  }

  PhysicalQuantity(String displayName, String unit, Boolean additive, String description, Double fixedValue)
  {
    this.displayName = displayName;
    this.unit = unit;
    this.description = description;
    this.fixedValue = fixedValue;
    this.additive = additive;
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
