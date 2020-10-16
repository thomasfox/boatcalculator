package com.github.thomasfox.boatcalculator.profile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.thomasfox.boatcalculator.interpolate.QuantityRelations;
import com.github.thomasfox.boatcalculator.interpolate.XYPoint;
import com.github.thomasfox.boatcalculator.xfoil.XfoilResultLoader;

public class ProfileSelector
{
  private final DatFileLoader datLoader = new DatFileLoader();

  private final XfoilResultLoader xfoilLoader = new XfoilResultLoader();

  public List<String> getProfileNames(File directory)
  {
    String[] filenames = directory.list((dir, name) -> name.endsWith(".dat"));
    Arrays.sort(filenames);
    List<String> result = Arrays.stream(filenames)
        .map(s -> s.substring(0, s.length() - 4))
        .collect(Collectors.toList());
    return result;
  }

  public ProfileGeometry loadProfile(File directory, String name)
  {
    List<XYPoint> pointList;
    try (FileReader reader = new FileReader(new File(directory, name + ".dat")))
    {
      pointList = datLoader.load(reader);
    }
    catch (IOException | RuntimeException e)
    {
      return null;
    }
    return new ProfileGeometry(name, pointList);
  }

  public List<QuantityRelations> loadXfoilResults(File directory, String name)
  {
    String[] filenames = directory.list(
        (dir, filename) -> filename.matches("^xf-" + name + "-[\\d]*.txt"));
    List<QuantityRelations> result = new ArrayList<>();
    for (String filename : filenames)
    {
      try (FileReader reader = new FileReader(new File(directory, filename)))
      {
        result.add(xfoilLoader.load(reader));
      }
      catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    }
    return result;
  }
}
