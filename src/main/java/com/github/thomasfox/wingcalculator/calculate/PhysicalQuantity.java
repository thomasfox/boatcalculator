package com.github.thomasfox.wingcalculator.calculate;

import java.util.Map;

public enum PhysicalQuantity
{
  INDUCED_DRAG("Induzierter Widerstand", "N"),
  INDUCED_DRAG_COEFFICIENT("Widerstandsbeiwert f�r den Induzierten Widerstand", null),
  PROFILE_DRAG("Profilwiderstand", "N"),
  PROFILE_DRAG_COEFFICIENT("Widerstandsbeiwert f�r den Profilwiderstand", null),
  LIFT("Auftrieb", "N", "des gesamten Fl�gels"),
  LIFT_COEFFICIENT("Auftriebsbeiwert", null, "gemittelt nach Fl�che �ber den gesamten Fl�gel"),
  REYNOLDS_NUMBER("Reynoldszahl", null),
  WING_SPAN("Spannweite des Fl�gels", "m", "quer zur Profilierung, von einem Ende zum anderen"),
  WING_CHORD("Tiefe des Fl�gels", "m", "quer zur Spannweite, gleich �ber die geamte Spannweite"),
  WING_THICKNESS("Dicke des Fl�gels", "m", "Dicke an der dicksten Stelle"),
  WING_RELATIVE_THICKNESS("Relative Dicke des Fl�gels", null, "Dicke an der dicksten Stelle geteilt durch Profiltiefe"),
  VELOCITY("Geschwindigkeit", "m/s"),
  POINTING_ANGLE("Steuerkurs", "�", "Winkel zwischen Symmetrieachse des Bootes und Windrichtung"),
  WIND_SPEED("Windgeschwindigkeit", "m/s", "Das Wasser wird als ruhendes Bezugssystem genommen"),
  KINEMATIC_VISCOSITY("Kinematische Viskosit�t", "m^2/s"),
  DENSITY("Dichte", "kg/m^3"),
  IMMERSION_DEPTH("Eintauchtiefe des Fl�gels", "m", "f�r horizontal verlaufende Fl�gel"),
  ANGLE_OF_ATTACK("Anstellwinkel", "�"),
  NCRIT("nCrit", null, "The log of the amplification factor of the most-amplified frequency which triggers transition for XFOIL Calculations", 9.0d),
  MODULUS_OF_ELASTICITY("E-Modul", "N/m^2", "CFK unidirektional parallel zur Faserrichtung, reduziert wg. Handlaminiert", 100000000000d),
  BENDING_FORCE("Biegekraft", "N", "am Ende eines Fl�gels angreifende Kraft. Der Fl�gel ist am anderen Ende fest eingepsannt"),
  BENDING("Durchbiegung", "m", "am Ende eines Fl�gels. Der Fl�gel ist am anderen Ende fest eingepsannt"),
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

  public Double getValueFromAvailableQuantities(Map<PhysicalQuantity, Double> availableQuantities)
  {
    if (fixedValue != null)
    {
      return fixedValue;
    }
    return availableQuantities.get(this);
  }
}
