package com.github.thomasfox.wingcalculator.xfoil;

import static  org.assertj.core.api.Assertions.assertThat;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.wingcalculator.interpolate.QuantityRelations;

public class XfoilResultLoaderTest
{
  private final XfoilResultLoader sut = new XfoilResultLoader();

  @Test
  public void testLoad() throws Exception
  {
    Reader reader = new InputStreamReader(getClass().getResourceAsStream("/xfoil.txt"), "iso-8859-1");

    QuantityRelations result = sut.load(reader);

    assertThat(result.getName()).isEqualTo("XFOIL Calculated polar for: XY 123");
    assertThat(result.getFixedQuantities()).hasSize(2);
    assertThat(result.getFixedQuantities().get(PhysicalQuantity.REYNOLDS_NUMBER)).isEqualTo(1000000d);
    assertThat(result.getFixedQuantities().get(PhysicalQuantity.NCRIT)).isEqualTo(9d);
    assertThat(result.getRelatedQuantities()).isEqualTo(Arrays.asList(
        new PhysicalQuantity[] {
            PhysicalQuantity.ANGLE_OF_ATTACK,
            PhysicalQuantity.LIFT_COEFFICIENT,
            PhysicalQuantity.PROFILE_DRAG_COEFFICIENT}));
    assertThat(result.getRelatedQuantityValues()).hasSize(2);
    Map<PhysicalQuantity, Double> relatedQuantitiesLine = result.getRelatedQuantityValues().get(0);
    assertThat(relatedQuantitiesLine).hasSize(3);
    assertThat(relatedQuantitiesLine.get(PhysicalQuantity.ANGLE_OF_ATTACK)).isEqualTo(-13d);
    assertThat(relatedQuantitiesLine.get(PhysicalQuantity.LIFT_COEFFICIENT)).isEqualTo(-1.0123d);
    assertThat(relatedQuantitiesLine.get(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT)).isEqualTo(0.20000);
    relatedQuantitiesLine = result.getRelatedQuantityValues().get(1);
    assertThat(relatedQuantitiesLine).hasSize(3);
    assertThat(relatedQuantitiesLine.get(PhysicalQuantity.ANGLE_OF_ATTACK)).isEqualTo(7.750);
    assertThat(relatedQuantitiesLine.get(PhysicalQuantity.LIFT_COEFFICIENT)).isEqualTo(0.0823);
    assertThat(relatedQuantitiesLine.get(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT)).isEqualTo(0.10000);
  }
}
