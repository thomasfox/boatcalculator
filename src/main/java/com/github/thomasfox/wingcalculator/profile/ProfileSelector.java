package com.github.thomasfox.wingcalculator.profile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProfileSelector
{
  private final DatFileLoader loader = new DatFileLoader();

  public List<String> getProfileNames(File directory)
  {
    String[] filenames = directory.list((dir, name) -> name.endsWith(".dat"));
    List<String> result = Arrays.stream(filenames)
        .map(s -> s.substring(0, s.length() - 4))
        .collect(Collectors.toList());
    return result;
  }

  public Profile load(File directory, String name)
  {
    FileReader reader;
    try
    {
      reader = new FileReader(new File(directory, name + ".dat"));
    }
    catch (FileNotFoundException e)
    {
      throw new RuntimeException(e);
    }
    return new Profile(name, loader.load(reader));
  }
}
