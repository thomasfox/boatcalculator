package com.github.thomasfox.wingcalculator.interpolate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;

public class QuantityRelationsLoader
{
  public QuantityRelations load(File file, String title)
  {
    try (FileReader reader = new FileReader(file))
    {
      return load(reader, title);
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  public QuantityRelations load(Reader reader, String title)
  {
    BufferedReader bufferedReader = new BufferedReader(reader);
    Map<PhysicalQuantity, Double> fixedQuantities;
    List<PhysicalQuantity> relatedQuantities;
    List<Map<PhysicalQuantity, Double>> relatedQuantityValues;
    try
    {
      fixedQuantities = readFixedQuantities(bufferedReader);
      relatedQuantities = readRelatedQuantities(bufferedReader);
      relatedQuantityValues = readRelatedQuantityValues(bufferedReader, relatedQuantities);
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
    QuantityRelations result = QuantityRelations.builder()
      .name(title)
      .fixedQuantities(fixedQuantities)
      .relatedQuantities(new LinkedHashSet<>(relatedQuantities))
      .relatedQuantityValues(relatedQuantityValues)
      .build();
    return result;
  }

  public Map<PhysicalQuantity, Double> readFixedQuantities(BufferedReader reader) throws IOException
  {
    Map<PhysicalQuantity, Double> result = new HashMap<>();

    String line = reader.readLine();
    while (!line.isEmpty())
    {
      StringTokenizer stringTokenizer = new StringTokenizer(line);
      PhysicalQuantity quantity = parseTokenAsPhysicalQuantity(stringTokenizer);
      Double value = parseTokenAsDouble(stringTokenizer);
      result.put(quantity, value);
      line = reader.readLine();
    }
    return result;
  }

  public List<PhysicalQuantity> readRelatedQuantities(BufferedReader reader) throws IOException
  {
    String line = reader.readLine();
    if (line.trim().isEmpty())
    {
      throw new IOException("line is empty, is this an RelatedQuantities file ?");
    }
    List<PhysicalQuantity> result = new ArrayList<>();
    StringTokenizer headlineTokenizer = new StringTokenizer(line);
    while (headlineTokenizer.hasMoreTokens())
    {
      result.add(parseTokenAsPhysicalQuantity(headlineTokenizer));
    }
    return result;
  }

  public List<Map<PhysicalQuantity, Double>> readRelatedQuantityValues(BufferedReader reader, List<PhysicalQuantity> quantities) throws IOException
  {
    List<Map<PhysicalQuantity, Double>> result = new ArrayList<>();

    do
    {
      String line = reader.readLine();
      if (line == null || line.isEmpty())
      {
        break;
      }
      StringTokenizer valueLineTokenizer = new StringTokenizer(line);
      Map<PhysicalQuantity, Double> parsedLine = new HashMap<>();
      for (PhysicalQuantity quantity : quantities)
      {
        Double value = parseTokenAsDouble(valueLineTokenizer);
        parsedLine.put(quantity, value);
      }
      result.add(parsedLine);
    }
    while (true);
    return result;
  }

  private PhysicalQuantity parseTokenAsPhysicalQuantity(StringTokenizer tokenizer) throws IOException
  {
    String physicalQuantityString = tokenizer.nextToken();
    PhysicalQuantity quantity = PhysicalQuantity.valueOf(physicalQuantityString);
    return quantity;
  }

  private double parseTokenAsDouble(StringTokenizer tokenizer) throws IOException
  {
    String token = tokenizer.nextToken();
    return Double.parseDouble(token);
  }
}
