package com.github.thomasfox.sailboatcalculator.calculate;

import java.util.Map;

public enum PhysicalQuantity
{
  INDUCED_DRAG("Induzierter Widerstand", "N"),
  INDUCED_DRAG_COEFFICIENT("Widerstandsbeiwert für den Induzierten Widerstand", null),
  PROFILE_DRAG("Profilwiderstand", "N"),
  PROFILE_DRAG_COEFFICIENT("Widerstandsbeiwert für den Profilwiderstand", null),
  TOTAL_DRAG("Gesamtwiderstand", "N"),
  TOTAL_DRAG_COEFFICIENT("Widerstandsbeiwert für den Gesamtwiderstand", null),
  LIFT("Auftrieb", "N", "des gesamten Flügels"),
  LIFT_COEFFICIENT("Auftriebsbeiwert", null, "gemittelt nach Fläche über den gesamten Flügel"),
  LATERAL_FORCE("Lateralkraft", "N"),
  DRIVING_FORCE("Vortriebskraft", "N"),
  REYNOLDS_NUMBER("Reynoldszahl", null),
  WING_SPAN("Spannweite des Flügels", "m", "quer zur Profilierung, von einem Ende zum anderen"),
  WING_CHORD("Tiefe des Flügels", "m", "quer zur Spannweite, gleich über die geamte Spannweite"),
  WING_THICKNESS("Dicke des Flügels", "m", "Dicke an der dicksten Stelle"),
  WING_RELATIVE_THICKNESS("Relative Dicke des Flügels", null, "Dicke an der dicksten Stelle geteilt durch Profiltiefe"),
  VELOCITY("Geschwindigkeit", "m/s", "Geschwindigkeit der Strömung"),
  FLOW_DIRECTION("Winkel der Strömung", "°", "Winkel der Strömung zur Bootsachse"),
  POINTING_ANGLE("Steuerkurs", "°", "Winkel zwischen Symmetrieachse des Bootes und Windrichtung"),
  VMG("VMG", "m/s", "Komponente der Geschwindigkeit in Windrichtung"),
  APPARENT_WIND_ANGLE("Scheinbare Windrichtung", "°", "Winkel zwischen Symmetrieachse des Bootes und Scheinbarer Windrichtung"),
  APPARENT_WIND_SPEED("Scheinbarer Wind", "m/s"),
  WIND_SPEED("Windgeschwindigkeit", "m/s", "Das Wasser wird als ruhendes Bezugssystem genommen"),
  FORCE("Kraft", "N"),
  LEVER_BETWEEN_FORCES("Hebelarm", "m", "Strecke zwischen zwei gleichstarken entgegengesetzten Kräften, ohne festen Drehpunkt"),
  TORQUE_BETWEEN_FORCES("Drehmoment", "nm", "Drehmoment verursacht durch zwei gleichstarke entgegengesetzten Kräften, ohne festen Drehpunkt"),
  WEIGHT("Gewicht", "kg"),
  LEVER_WEIGHT("Hebelarm Gewicht", "m"),
  GRAVITY_ACCELERATION("Fallbeschleunigung", "m/s^2", "Im schwerefeld der Erde"),
  KINEMATIC_VISCOSITY("Kinematische Viskosität", "m^2/s"),
  DENSITY("Dichte", "kg/m^3"),
  IMMERSION_DEPTH("Eintauchtiefe des Flügels", "m", "für horizontal verlaufende Flügel"),
  ANGLE_OF_ATTACK("Anstellwinkel", "°"),
  NCRIT("nCrit", null, "The log of the amplification factor of the most-amplified frequency which triggers transition for XFOIL Calculations", 9.0d),
  MODULUS_OF_ELASTICITY("E-Modul", "N/m^2", "CFK unidirektional parallel zur Faserrichtung, reduziert wg. Handlaminiert", 100000000000d),
  BENDING_FORCE("Biegekraft", "N", "am Ende eines Flügels angreifende Kraft. Der Flügel ist am anderen Ende fest eingepsannt"),
  BENDING("Durchbiegung", "m", "am Ende eines Flügels. Der Flügel ist am anderen Ende fest eingepsannt"),
  SECOND_MOMENT_OF_AREA("Flächenmoment 2. Ordnung für den Flügelquerschnitt", "m^4", "Integral y^2 dx dy"),
  NORMALIZED_SECOND_MOMENT_OF_AREA("Flächenmoment 2. Ordnung", null, "Integral y^2 dx dy für ein Profil der Tiefe 1 und gleichem Tiefe/Dickenverhältnis wie das eigentliche Profil");

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

  public Double getValueFromAvailableQuantities(Map<PhysicalQuantity, Double> availableQuantities)
  {
    if (fixedValue != null)
    {
      return fixedValue;
    }
    return availableQuantities.get(this);
  }
}
