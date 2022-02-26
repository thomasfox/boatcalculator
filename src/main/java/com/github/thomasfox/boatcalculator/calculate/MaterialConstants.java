package com.github.thomasfox.boatcalculator.calculate;

import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;

public class MaterialConstants
{
  // Water at 15°C
  public static final PhysicalQuantityValue KINEMATIC_VISCOSITY_WATER
     = new SimplePhysicalQuantityValue(PhysicalQuantity.KINEMATIC_VISCOSITY, 0.000001375d);

  public static final PhysicalQuantityValue KINEMATIC_VISCOSITY_AIR
      = new SimplePhysicalQuantityValue(PhysicalQuantity.KINEMATIC_VISCOSITY, 15.2E-6d);

  public static final PhysicalQuantityValue DENSITY_WATER
      = new SimplePhysicalQuantityValue(PhysicalQuantity.DENSITY, 1000d);

  public static final PhysicalQuantityValue DENSITY_AIR
      = new SimplePhysicalQuantityValue(PhysicalQuantity.DENSITY, 1.2041d);

  public static final PhysicalQuantityValue GRAVITY_ACCELERATION
      = new SimplePhysicalQuantityValue(PhysicalQuantity.GRAVITY_ACCELERATION, 9.81d);
}
