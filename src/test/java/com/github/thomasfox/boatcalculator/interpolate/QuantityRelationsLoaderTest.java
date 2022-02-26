package com.github.thomasfox.boatcalculator.interpolate;

import static  org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileReader;

import org.junit.Test;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;

public class QuantityRelationsLoaderTest
{
  private final QuantityRelationLoader sut = new QuantityRelationLoader();

  @Test
  public void testLoad() throws Exception
  {
    QuantityRelation result;
    try (FileReader reader = new FileReader(new File("hulls/29er_204kg.txt")))
    {
      // execute
      result = sut.load(reader, "Hull");
    }

    // verify
    assertThat(result.getFixedQuantities().getAsList()).containsExactly(
        new SimplePhysicalQuantityValue(PhysicalQuantity.LIFT, 2001d));
    assertThat(result.getRelatedQuantities()).containsExactly(PhysicalQuantity.VELOCITY, PhysicalQuantity.TOTAL_DRAG);
    assertThat(result.getRelatedQuantityValues()).hasSize(21);
    assertThat(result.getRelatedQuantityValues().get(0).getAsList()).containsOnly(
        new SimplePhysicalQuantityValue(PhysicalQuantity.VELOCITY, 0d),
        new SimplePhysicalQuantityValue(PhysicalQuantity.TOTAL_DRAG, 0d));
    assertThat(result.getRelatedQuantityValues().get(19).getAsList()).containsOnly(
        new SimplePhysicalQuantityValue(PhysicalQuantity.VELOCITY, 8.018428827d),
        new SimplePhysicalQuantityValue(PhysicalQuantity.TOTAL_DRAG, 367.62d));
  }

}
