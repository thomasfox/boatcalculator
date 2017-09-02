package com.github.thomasfox.wingcalculator.profile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.thomasfox.wingcalculator.interpolate.QuantityRelations;
import com.github.thomasfox.wingcalculator.xfoil.XfoilResultLoader;

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
    try (FileReader reader = new FileReader(new File(directory, name + ".dat")))
    {
      return new ProfileGeometry(name, datLoader.load(reader));
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  public List<QuantityRelations> loadXfoilResults(File directory, String name)
  {
    String[] filenames = directory.list(
        (dir, filename) -> filename.indexOf("-" + name + "-") != -1 && filename.endsWith(".txt"));
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
