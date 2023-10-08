package com.github.thomasfox.boatcalculator.xfoil;

import static  org.assertj.core.api.Assertions.assertThat;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashSet;

import org.junit.Test;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.interpolate.QuantityRelation;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValues;

public class XfoilResultLoaderTest
{
  private final XfoilResultLoader sut = new XfoilResultLoader();

  @Test
  public void testLoadXfoilFile() throws Exception
  {
    Reader reader = new InputStreamReader(getClass().getResourceAsStream("/xfoil-100000.txt"), StandardCharsets.ISO_8859_1);

    QuantityRelation result = sut.load(reader);

    assertThat(result.getName()).isEqualTo("Calculated polar for: XY 123");
    assertThat(result.getFixedQuantities().getAsList()).hasSize(2);
    assertThat(result.getFixedQuantities().getValue(PhysicalQuantity.REYNOLDS_NUMBER)).isEqualTo(100000d);
    assertThat(result.getFixedQuantities().getValue(PhysicalQuantity.NCRIT)).isEqualTo(9d);
    assertThat(result.getRelatedQuantities()).isEqualTo(new LinkedHashSet<>(Arrays.asList(
        new PhysicalQuantity[] {
            PhysicalQuantity.ANGLE_OF_ATTACK,
            PhysicalQuantity.LIFT_COEFFICIENT,
            PhysicalQuantity.PROFILE_DRAG_COEFFICIENT})));
    assertThat(result.getRelatedQuantityValues()).hasSize(3);
    PhysicalQuantityValues relatedQuantitiesLine = result.getRelatedQuantityValues().get(0);
    assertThat(relatedQuantitiesLine.getAsList()).hasSize(3);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.ANGLE_OF_ATTACK)).isEqualTo(-10d);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.LIFT_COEFFICIENT)).isEqualTo(-1d);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT)).isEqualTo(0.20000);
    relatedQuantitiesLine = result.getRelatedQuantityValues().get(1);
    assertThat(relatedQuantitiesLine.getAsList()).hasSize(3);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.ANGLE_OF_ATTACK)).isEqualTo(0d);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.LIFT_COEFFICIENT)).isEqualTo(0d);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT)).isEqualTo(0.10000);
    relatedQuantitiesLine = result.getRelatedQuantityValues().get(2);
    assertThat(relatedQuantitiesLine.getAsList()).hasSize(3);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.ANGLE_OF_ATTACK)).isEqualTo(10d);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.LIFT_COEFFICIENT)).isEqualTo(1d);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT)).isEqualTo(0.20000);
  }

  @Test
  public void testLoadMeasuredXfoilFile() throws Exception
  {
    Reader reader = new InputStreamReader(getClass().getResourceAsStream("/measured-140000.txt"), StandardCharsets.ISO_8859_1);

    QuantityRelation result = sut.load(reader);

    assertThat(result.getName()).isEqualTo("MEASURED RIGG polar for: flat plate aspect ratio 1");
    assertThat(result.getFixedQuantities().getAsList()).hasSize(0);
    assertThat(result.getRelatedQuantities()).isEqualTo(new LinkedHashSet<>(Arrays.asList(
            new PhysicalQuantity[] {
                    PhysicalQuantity.ANGLE_OF_ATTACK,
                    PhysicalQuantity.LIFT_COEFFICIENT_3D,
                    PhysicalQuantity.TOTAL_DRAG_COEFFICIENT})));
    assertThat(result.getRelatedQuantityValues()).hasSize(3);
    PhysicalQuantityValues relatedQuantitiesLine = result.getRelatedQuantityValues().get(0);
    assertThat(relatedQuantitiesLine.getAsList()).hasSize(3);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.ANGLE_OF_ATTACK)).isEqualTo(-10d);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.LIFT_COEFFICIENT_3D)).isEqualTo(-0.25d);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.TOTAL_DRAG_COEFFICIENT)).isEqualTo(0.06d);
    relatedQuantitiesLine = result.getRelatedQuantityValues().get(1);
    assertThat(relatedQuantitiesLine.getAsList()).hasSize(3);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.ANGLE_OF_ATTACK)).isEqualTo(0d);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.LIFT_COEFFICIENT_3D)).isEqualTo(0d);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.TOTAL_DRAG_COEFFICIENT)).isEqualTo(0.015d);
    relatedQuantitiesLine = result.getRelatedQuantityValues().get(2);
    assertThat(relatedQuantitiesLine.getAsList()).hasSize(3);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.ANGLE_OF_ATTACK)).isEqualTo(15d);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.LIFT_COEFFICIENT_3D)).isEqualTo(0.5d);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.TOTAL_DRAG_COEFFICIENT)).isEqualTo(0.15d);
  }

  @Test
  public void testLoadXflr5File() throws Exception
  {
    Reader reader = new InputStreamReader(getClass().getResourceAsStream("/xflr5-500000.txt"), StandardCharsets.ISO_8859_1);

    QuantityRelation result = sut.load(reader);

    assertThat(result.getName()).isEqualTo("Calculated polar for: XY-123");
    assertThat(result.getFixedQuantities().getAsList()).hasSize(2);
    assertThat(result.getFixedQuantities().getValue(PhysicalQuantity.REYNOLDS_NUMBER)).isEqualTo(500000d);
    assertThat(result.getFixedQuantities().getValue(PhysicalQuantity.NCRIT)).isEqualTo(9d);
    assertThat(result.getRelatedQuantities()).isEqualTo(new LinkedHashSet<>(Arrays.asList(
            new PhysicalQuantity[] {
                    PhysicalQuantity.ANGLE_OF_ATTACK,
                    PhysicalQuantity.LIFT_COEFFICIENT,
                    PhysicalQuantity.PROFILE_DRAG_COEFFICIENT})));
    assertThat(result.getRelatedQuantityValues()).hasSize(3);
    PhysicalQuantityValues relatedQuantitiesLine = result.getRelatedQuantityValues().get(0);
    assertThat(relatedQuantitiesLine.getAsList()).hasSize(3);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.ANGLE_OF_ATTACK)).isEqualTo(-10d);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.LIFT_COEFFICIENT)).isEqualTo(-1.3381d);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT)).isEqualTo(0.01725);
    relatedQuantitiesLine = result.getRelatedQuantityValues().get(1);
    assertThat(relatedQuantitiesLine.getAsList()).hasSize(3);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.ANGLE_OF_ATTACK)).isEqualTo(0d);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.LIFT_COEFFICIENT)).isEqualTo(-0.3407);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT)).isEqualTo(0.00956);
    relatedQuantitiesLine = result.getRelatedQuantityValues().get(2);
    assertThat(relatedQuantitiesLine.getAsList()).hasSize(3);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.ANGLE_OF_ATTACK)).isEqualTo(10d);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.LIFT_COEFFICIENT)).isEqualTo(0.3765);
    assertThat(relatedQuantitiesLine.getValue(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT)).isEqualTo(0.11770);
  }
}
