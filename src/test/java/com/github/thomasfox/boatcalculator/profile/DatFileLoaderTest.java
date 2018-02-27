package com.github.thomasfox.boatcalculator.profile;

import static  org.assertj.core.api.Assertions.assertThat;

import java.io.InputStreamReader;
import java.util.List;

import org.junit.Test;

import com.github.thomasfox.boatcalculator.interpolate.XYPoint;
import com.github.thomasfox.boatcalculator.profile.DatFileLoader;

public class DatFileLoaderTest
{
  private final DatFileLoader sut = new DatFileLoader();

  @Test
  public void testLoad() throws Exception
  {
    try (InputStreamReader reader
        = new InputStreamReader(getClass().getResourceAsStream("/test.dat"), "ISO-8859-1"))
    {
      List<XYPoint> result = sut.load(reader);

      assertThat(result.size()).isEqualTo(7);
      // some random checks
      assertThat(result.get(0).getX()).isEqualTo(1d);
      assertThat(result.get(6).getX()).isEqualTo(1d);
      assertThat(result.get(2).getY()).isEqualTo(0.01d);
      assertThat(result.get(5).getY()).isEqualTo(-0.03d);
    }
  }
}
