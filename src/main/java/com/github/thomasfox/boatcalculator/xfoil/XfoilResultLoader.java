package com.github.thomasfox.boatcalculator.xfoil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.interpolate.OutOfInterpolationIntervalStrategy;
import com.github.thomasfox.boatcalculator.interpolate.QuantityRelation;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValues;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;

public class XfoilResultLoader
{
  public QuantityRelation load(Reader reader)
  {
    BufferedReader bufferedReader = new BufferedReader(reader);
    String title;
    PhysicalQuantityValues fixedQuantities;
    List<PhysicalQuantityValues> relatedQuantityValues;
    boolean measuredRiggPolar;
    try
    {
      readPrelude(bufferedReader);
      title = readTitle(bufferedReader);
      measuredRiggPolar = title.startsWith("MEASURED RIGG");
      fixedQuantities = readFixedQuantities(bufferedReader, measuredRiggPolar);
      relatedQuantityValues = readPolar(bufferedReader, measuredRiggPolar);
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

    OutOfInterpolationIntervalStrategy notEnoughLiftStrategy
        = new OutOfInterpolationIntervalStrategy(measuredRiggPolar ? PhysicalQuantity.LIFT_COEFFICIENT_3D : PhysicalQuantity.LIFT_COEFFICIENT, true);
    notEnoughLiftStrategy.addProvidedQuantities(
        new SimplePhysicalQuantityValue(PhysicalQuantity.ANGLE_OF_ATTACK, 30d),
        new SimplePhysicalQuantityValue(measuredRiggPolar ? PhysicalQuantity.TOTAL_DRAG_COEFFICIENT : PhysicalQuantity.PROFILE_DRAG_COEFFICIENT , 0.3d));
    result.add(notEnoughLiftStrategy);

    return result;
  }

  void readPrelude(BufferedReader reader) throws IOException
  {
    String line = reader.readLine();
    if (line.trim().isEmpty())
    {
      readXfoilPrelude(reader);
    }
    else if (line.startsWith("xflr5"))
    {
      readxflr5Prelude(reader);
    }
    else
    {
      throw new IOException("First line is \""+ line + "\", is this an XFOIL result file ?");
    }
  }

  void readXfoilPrelude(BufferedReader reader) throws IOException
  {
    String line = reader.readLine();
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

  void readxflr5Prelude(BufferedReader reader) throws IOException
  {
    String line = reader.readLine();
    if (!line.trim().isEmpty())
    {
      throw new IOException("Second line is not empty, is this an xflr5 result file ?");
    }
  }


  String readTitle(BufferedReader reader) throws IOException
  {
    String line = reader.readLine();
    if (line.trim().isEmpty())
    {
      throw new IOException("Fourth/Third line is empty, is this an XFOIL/xflr5 result file ?");
    }
    String result = line.trim();
    line = reader.readLine();
    if (!line.trim().isEmpty())
    {
      throw new IOException("Fifth/Fourth line is not empty, is this an XFOIL/xflr5 result file ?");
    }
    return result;
  }

  PhysicalQuantityValues readFixedQuantities(BufferedReader reader, boolean measuredRiggPolar)
      throws IOException
  {
    PhysicalQuantityValues result = new PhysicalQuantityValues();

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
      throw new IOException("7/6th line is not empty, is this an XFOIL/xflr5 result file ?");
    }
    line = reader.readLine();
    if (!line.contains("xtrf ="))
    {
      throw new IOException("8/7th line does not contain xtrf =");
    }

    line = reader.readLine().trim();
    if (!line.startsWith("Mach =   0.000 "))
    {
      throw new IOException("9/8th line does not start with Mach =   0.000 ");
    }
    line = line.substring("Mach =   0.000 ".length()).trim();

    if (!line.startsWith("Re ="))
    {
      throw new IOException("9/8th line does not contain Re =");
    }
    if (!line.contains("Ncrit ="))
    {
      throw new IOException("9th line does not contain Ncrit =");
    }
    if (!measuredRiggPolar) // in measurede polar we typically only have one Re num,ber
    {
      line = line.substring("Re =".length()).trim();
      String reNumber = line.substring(0, line.indexOf("Ncrit ="));
      reNumber = reNumber.replace(" ", "");
      result.setValueNoOverwrite(PhysicalQuantity.REYNOLDS_NUMBER, Double.parseDouble(reNumber));

      line = line.substring(line.indexOf("Ncrit =")).trim();
      line = line.substring("Ncrit =".length()).trim();
      String ncritNumber = line;
      ncritNumber = ncritNumber.replace(" ", "");
      result.setValueNoOverwrite(PhysicalQuantity.NCRIT, Double.parseDouble(ncritNumber));
    }

    line = reader.readLine();
    if (!line.trim().isEmpty())
    {
      throw new IOException("10/9th line is not empty, is this an XFOIL/xflr5 result file ?");
    }
    return result;
  }

  List<PhysicalQuantityValues> readPolar(BufferedReader reader, boolean measuredRiggPolar)
      throws IOException
  {
    List<PhysicalQuantityValues> result = new ArrayList<>();
    String line = reader.readLine();
    if (line.trim().isEmpty())
    {
      throw new IOException("11th/1th line is empty, is this an XFOIL/xflr5 result file ?");
    }
    StringTokenizer headlineTokenizer = new StringTokenizer(line);
    verifyToken(headlineTokenizer, "alpha");
    verifyToken(headlineTokenizer, "CL");
    verifyToken(headlineTokenizer, "CD");
    verifyToken(headlineTokenizer, "CDp");
    verifyToken(headlineTokenizer, "CM");

    line = reader.readLine();
    if (!line.contains("-"))
    {
      throw new IOException("12th/11th line does not contain -, is this an XFOIL result file ?");
    }

    do
    {
      line = reader.readLine();
      if (line == null || line.isEmpty())
      {
        break;
      }
      StringTokenizer valueLineTokenizer = new StringTokenizer(line);
      PhysicalQuantityValues parsedLine = new PhysicalQuantityValues();
      parsedLine.setValueNoOverwrite(PhysicalQuantity.ANGLE_OF_ATTACK, parseTokenAsDouble(valueLineTokenizer));
      if (measuredRiggPolar)
      {
        // measured rigg polar: ca is 3d-ca, cd is for total drag
        parsedLine.setValueNoOverwrite(PhysicalQuantity.LIFT_COEFFICIENT_3D, parseTokenAsDouble(valueLineTokenizer));
        parsedLine.setValueNoOverwrite(PhysicalQuantity.TOTAL_DRAG_COEFFICIENT, parseTokenAsDouble(valueLineTokenizer));
      }
      else
      {
        // normal polar
        parsedLine.setValueNoOverwrite(PhysicalQuantity.LIFT_COEFFICIENT, parseTokenAsDouble(valueLineTokenizer));
        parsedLine.setValueNoOverwrite(PhysicalQuantity.PROFILE_DRAG_COEFFICIENT, parseTokenAsDouble(valueLineTokenizer));
      }
      parseTokenAsDouble(valueLineTokenizer); // ignore pressure drag coefficient
      parseTokenAsDouble(valueLineTokenizer); // ignore momentum coefficient
      parseTokenAsDouble(valueLineTokenizer); // ignore top transition
      parseTokenAsDouble(valueLineTokenizer); // ignore bottom transition
      // ignore following tokens for xflr5
      result.add(parsedLine);
    }
    while (true);
    return result;
  }

  private void verifyToken(StringTokenizer tokenizer, String expected) throws IOException
  {
    String token = tokenizer.nextToken();
    if (!expected.equalsIgnoreCase(token))
    {
      throw new IOException("Token expected : " + expected + " but was " + token);
    }
  }

  private double parseTokenAsDouble(StringTokenizer tokenizer)
  {
    String token = tokenizer.nextToken();
    return Double.parseDouble(token);
  }
}
