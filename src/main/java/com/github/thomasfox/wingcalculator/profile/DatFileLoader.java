package com.github.thomasfox.wingcalculator.profile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.github.thomasfox.wingcalculator.interpolate.SimpleXYPoint;
import com.github.thomasfox.wingcalculator.interpolate.XYPoint;

public class DatFileLoader
{
  public List<XYPoint> load(Reader reader)
  {
    try
    {
      BufferedReader bufferedReader = new BufferedReader(reader);
      List<XYPoint> result = new ArrayList<>();
      String line = bufferedReader.readLine(); // first line contains name, ignore
      line = bufferedReader.readLine();
      do
      {
        StringTokenizer tokenizer = new StringTokenizer(line);
        Double x = Double.parseDouble(tokenizer.nextToken());
        Double y = Double.parseDouble(tokenizer.nextToken());
        result.add(new SimpleXYPoint(x, y));
        line = bufferedReader.readLine();
      }
      while (line != null);
      return result;
    }
    catch (NumberFormatException e)
    {
      throw new RuntimeException(e);
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }
}
