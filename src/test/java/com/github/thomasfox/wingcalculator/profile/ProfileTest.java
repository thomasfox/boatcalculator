package com.github.thomasfox.wingcalculator.profile;

import static  org.assertj.core.api.Assertions.assertThat;

import java.io.InputStreamReader;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.thomasfox.wingcalculator.interpolate.XYPoint;

public class ProfileTest
{
  private final DatFileLoader datFileLoader = new DatFileLoader();

  private Profile sut;

  @Before
  public void before() throws Exception
  {
    try (InputStreamReader reader
        = new InputStreamReader(getClass().getResourceAsStream("/test.dat"), "ISO-8859-1"))
    {
      List<XYPoint> xyPoints = datFileLoader.load(reader);
      sut = new Profile("Test", xyPoints);
    }
  }

  @Test
  public void testGetThickness()
  {
    double thickness = sut.getThickness();
    assertThat(thickness).isEqualTo(0.04d);
  }

  @Test
  public void testGetUpperY_at0()
  {
    double upperY = sut.getUpperY(0d);
    assertThat(upperY).isEqualTo(0d);
  }

  @Test
  public void testGetUpperY_at0_4()
  {
    double upperY = sut.getUpperY(0.4d);
    assertThat(upperY).isEqualTo(0.01d);
  }

  @Test
  public void testGetUpperY_at1()
  {
    double upperY = sut.getUpperY(1d);
    assertThat(upperY).isEqualTo(0d);
  }

  @Test
  public void testGetLowerY_at0()
  {
    double lowerY = sut.getLowerY(0d);
    assertThat(lowerY).isEqualTo(0d);
  }

  @Test
  public void testGetLowerY_at0_4()
  {
    double lowerY = sut.getLowerY(0.4d);
    assertThat(lowerY).isEqualTo(-0.03d);
  }

  @Test
  public void testGetLowerY_at1()
  {
    double lowerY = sut.getLowerY(1d);
    assertThat(lowerY).isEqualTo(0d);
  }
}
