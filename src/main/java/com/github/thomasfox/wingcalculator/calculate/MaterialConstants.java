package com.github.thomasfox.wingcalculator.calculate;

public class MaterialConstants
{
  public static final PhysicalQuantityValue KINEMATIC_VISCOSITY_WATER
     = new FixedPhysicalQuantityValue(PhysicalQuantity.KINEMATIC_VISCOSITY, 0.000001d);

  public static final PhysicalQuantityValue KINEMATIC_VISCOSITY_AIR
      = new FixedPhysicalQuantityValue(PhysicalQuantity.KINEMATIC_VISCOSITY, 15.2E-6d);

  public static final PhysicalQuantityValue DENSITY_WATER
      = new FixedPhysicalQuantityValue(PhysicalQuantity.DENSITY, 1000d);

  public static final PhysicalQuantityValue DENSITY_AIR
      = new FixedPhysicalQuantityValue(PhysicalQuantity.DENSITY, 1.2041d);
}
