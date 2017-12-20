package com.github.thomasfox.sailboatcalculator.calculate;

import com.github.thomasfox.sailboatcalculator.value.FixedPhysicalQuantityValue;
import com.github.thomasfox.sailboatcalculator.value.PhysicalQuantityValue;

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

  public static final PhysicalQuantityValue GRAVITY_ACCELERATION
      = new FixedPhysicalQuantityValue(PhysicalQuantity.GRAVITY_ACCELERATION, 9.81d);
}
