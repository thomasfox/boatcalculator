package com.github.thomasfox.wingcalculator.xfoil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.github.thomasfox.wingcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.wingcalculator.interpolate.QuantityRelations;

public class XfoilResultLoader
{
  public QuantityRelations load(Reader reader)
  {
    BufferedReader bufferedReader = new BufferedReader(reader);
    String title;
    Map<PhysicalQuantity, Double> fixedQuantities;
    List<Map<PhysicalQuantity, Double>> relatedQuantityValues;
    try
    {
      readPrelude(bufferedReader);
      title = readTitle(bufferedReader);
      fixedQuantities = readFixedQuantities(bufferedReader);
      relatedQuantityValues = readPolar(bufferedReader);
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
    QuantityRelations result = QuantityRelations.builder()
      .name(title)
      .fixedQuantities(fixedQuantities)
      .relatedQuantities(Arrays.asList(
          new PhysicalQuantity[] {
              PhysicalQuantity.ANGLE_OF_ATTACK,
              PhysicalQuantity.LIFT_COEFFICIENT,
              PhysicalQuantity.PROFILE_DRAG_COEFFICIENT}))
      .relatedQuantityValues(relatedQuantityValues)
      .build();
    return result;
  }

  public void readPrelude(BufferedReader reader) throws IOException
  {
    String line = reader.readLine();
    if (!line.trim().isEmpty())
    {
      throw new IOException("First line is not empty, is this an XFOIL result file ?");
    }
     line = reader.readLine();
    if (!line.contains("XFOIL"))
    {
      throw new IOException("Second line does not contain XFOIL, is this an XFOIL result file ?");
    }
    line = reader.readLine();
    if (!line.trim().isEmpty())
    {
      throw new IOException("Third line is not empty, is this an XFOIL result file ?");
    }
  }

  public String readTitle(BufferedReader reader) throws IOException
  {
    String line = reader.readLine();
    if (line.trim().isEmpty())
    {
      throw new IOException("Fourth line is empty, is this an XFOIL result file ?");
    }
    String result = "XFOIL " + line.trim();
    line = reader.readLine();
    if (!line.trim().isEmpty())
    {
      throw new IOException("Fifth line is not empty, is this an XFOIL result file ?");
    }
    return result;
  }

  public Map<PhysicalQuantity, Double> readFixedQuantities(BufferedReader reader) throws IOException
  {
    Map<PhysicalQuantity, Double> result = new HashMap<>();

    String line = reader.readLine();
    if (!line.contains("Reynolds number fixed"))
    {
      throw new IOException("Reynolds number is not fixed");
    }
    if (!line.contains("Mach number fixed"))
    {
      throw new IOException("Mach number is not fixed");
    }
    line = reader.readLine();
    if (!line.trim().isEmpty())
    {
      throw new IOException("7th line is not empty, is this an XFOIL result file ?");
    }
    line = reader.readLine();
    if (!line.contains("xtrf =   1.000 (top)"))
    {
      throw new IOException("8th line does not contain xtrf =   1.000 (top)");
    }
    if (!line.contains("1.000 (bottom)"))
    {
      throw new IOException("8th line does not contain 1.000 (bottom)");
    }

    line = reader.readLine().trim();
    if (!line.startsWith("Mach =   0.000 "))
    {
      throw new IOException("9th line does not start with Mach =   0.000 ");
    }
    line = line.substring("Mach =   0.000 ".length()).trim();

    if (!line.startsWith("Re ="))
    {
      throw new IOException("9th line does not contain Re =");
    }
    if (!line.contains("Ncrit ="))
    {
      throw new IOException("9th line does not contain Ncrit =");
    }
    line = line.substring("Re =".length()).trim();
    String reNumber = line.substring(0, line.indexOf("Ncrit ="));
    reNumber = reNumber.replace(" ", "");
    result.put(PhysicalQuantity.REYNOLDS_NUMBER, Double.parseDouble(reNumber));

    line = line.substring(line.indexOf("Ncrit =")).trim();
    line = line.substring("Ncrit =".length()).trim();
    String ncritNumber = line;
    ncritNumber = ncritNumber.replace(" ", "");
    result.put(PhysicalQuantity.NCRIT, Double.parseDouble(ncritNumber));

    line = reader.readLine();
    if (!line.trim().isEmpty())
    {
      throw new IOException("10th line is not empty, is this an XFOIL result file ?");
    }
    return result;
  }

  public List<Map<PhysicalQuantity, Double>> readPolar(BufferedReader reader) throws IOException
  {
    List<Map<PhysicalQuantity, Double>> result = new ArrayList<>();
    String line = reader.readLine();
    if (line.trim().isEmpty())
    {
      throw new IOException("11th line is empty, is this an XFOIL result file ?");
    }
    StringTokenizer headlineTokenizer = new StringTokenizer(line);
    verifyToken(headlineTokenizer, "alpha");
    verifyToken(headlineTokenizer, "CL");
    verifyToken(headlineTokenizer, "CD");
    verifyToken(headlineTokenizer, "CDp");
    verifyToken(headlineTokenizer, "CM");
    verifyToken(headlineTokenizer, "Top_Xtr");
    verifyToken(headlineTokenizer, "Bot_Xtr");

    line = reader.readLine();
    if (!line.contains("-"))
    {
      throw new IOException("12th line does not contain -, is this an XFOIL result file ?");
    }

    do
    {
      line = reader.readLine();
      if (line == null || line.isEmpty())
      {
        break;
      }
      StringTokenizer valueLineTokenizer = new StringTokenizer(line);
      Map<PhysicalQuantity, Double> parsedLine = new HashMap<>();
      parsedLine.put(PhysicalQuantity.ANGLE_OF_ATTACK, parseTokenAsDouble(valueLineTokenizer));
      parsedLine.put(PhysicalQuantity.LIFT_COEFFICIENT, parseTokenAsDouble(valueLineTokenizer));
      parsedLine.put(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT, parseTokenAsDouble(valueLineTokenizer));
      parseTokenAsDouble(valueLineTokenizer); // ignore pressure drag coefficient
      parseTokenAsDouble(valueLineTokenizer); // ignore momentum coefficient
      parseTokenAsDouble(valueLineTokenizer); // ignore top transition
      parseTokenAsDouble(valueLineTokenizer); // ignore bottom transition
      result.add(parsedLine);
    }
    while (true);
    return result;
  }

  private void verifyToken(StringTokenizer tokenizer, String expected) throws IOException
  {
    String token = tokenizer.nextToken();
    if (!expected.equals(token))
    {
      throw new IOException("Token expected : " + expected + " but was " + token);
    }
  }

  private double parseTokenAsDouble(StringTokenizer tokenizer) throws IOException
  {
    String token = tokenizer.nextToken();
    return Double.parseDouble(token);
  }
}
