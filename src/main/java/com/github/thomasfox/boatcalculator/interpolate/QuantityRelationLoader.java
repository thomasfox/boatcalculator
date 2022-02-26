package com.github.thomasfox.boatcalculator.interpolate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValues;

public class QuantityRelationLoader
{
  private static String COMMENT_LINE_START = "#";

  public QuantityRelation load(File file, String title)
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

  public QuantityRelation load(Reader reader, String title)
  {
    BufferedReader bufferedReader = new BufferedReader(reader);
    PhysicalQuantityValues fixedQuantities;
    List<PhysicalQuantity> relatedQuantities;
    List<PhysicalQuantityValues> relatedQuantityValues;
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
    QuantityRelation result = QuantityRelation.builder()
      .name(title)
      .fixedQuantities(fixedQuantities)
      .relatedQuantityValues(relatedQuantityValues)
      .build();
    return result;
  }

  public PhysicalQuantityValues readFixedQuantities(BufferedReader reader) throws IOException
  {
    PhysicalQuantityValues result = new PhysicalQuantityValues();

    String line = reader.readLine();
    while (!line.isEmpty())
    {
      if (line.startsWith(COMMENT_LINE_START))
      {
        line = reader.readLine();
        continue;
      }
      StringTokenizer stringTokenizer = new StringTokenizer(line);
      PhysicalQuantity quantity = parseTokenAsPhysicalQuantity(stringTokenizer);
      Double value = parseTokenAsDouble(stringTokenizer);
      result.setValueNoOverwrite(quantity, value);
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

  public List<PhysicalQuantityValues> readRelatedQuantityValues(
      BufferedReader reader,
      List<PhysicalQuantity> quantities)
          throws IOException
  {
    List<PhysicalQuantityValues> result = new ArrayList<>();

    do
    {
      String line = reader.readLine();
      if (line == null || line.isEmpty())
      {
        break;
      }
      StringTokenizer valueLineTokenizer = new StringTokenizer(line);
      PhysicalQuantityValues parsedLine = new PhysicalQuantityValues();
      for (PhysicalQuantity quantity : quantities)
      {
        Double value = parseTokenAsDouble(valueLineTokenizer);
        parsedLine.setValueNoOverwrite(quantity, value);
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
