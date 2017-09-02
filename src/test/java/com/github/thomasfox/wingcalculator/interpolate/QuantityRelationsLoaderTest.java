package com.github.thomasfox.wingcalculator.interpolate;

import static  org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileReader;

import org.assertj.core.data.MapEntry;
import org.junit.Test;

import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;

public class QuantityRelationsLoaderTest
{
  private final QuantityRelationsLoader sut = new QuantityRelationsLoader();

  @Test
  public void testLoad() throws Exception
  {
    QuantityRelations result;
    try (FileReader reader = new FileReader(new File("hulls/29er.txt")))
    {
      // execute
      result = sut.load(reader, "Hull");
    }

    // verify
    assertThat(result.getFixedQuantities()).containsExactly(
        MapEntry.entry(PhysicalQuantity.WEIGHT, 204d));
    assertThat(result.getRelatedQuantities()).containsExactly(PhysicalQuantity.VELOCITY, PhysicalQuantity.TOTAL_DRAG);
    assertThat(result.getRelatedQuantityValues()).hasSize(20);
    assertThat(result.getRelatedQuantityValues().get(0)).containsOnly(
        MapEntry.entry(PhysicalQuantity.VELOCITY, 0d),
        MapEntry.entry(PhysicalQuantity.TOTAL_DRAG, 0d));
    assertThat(result.getRelatedQuantityValues().get(19)).containsOnly(
        MapEntry.entry(PhysicalQuantity.VELOCITY, 8.018428827d),
        MapEntry.entry(PhysicalQuantity.TOTAL_DRAG, 444.822d));
  }

}
