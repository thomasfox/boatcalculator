package com.github.thomasfox.boatcalculator.gui.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.data.xy.XYDataItem;

/**
 * The result of a scan where a single physical qquantity is scanned.
 */
public class ScanResult1D
{
  private final List<ScanResultForSingleQuantity1D> singleQuantityScanResults = new ArrayList<>();

  private Map<Integer, String> profileNames = new HashMap<>();

  public List<ScanResultForSingleQuantity1D> getSingleQuantityScanResults()
  {
    return singleQuantityScanResults;
  }

  public Map<Integer, String> getProfileNames()
  {
    return profileNames;
  }

  public void setProfileNames(Map<Integer, String> profileNames)
  {
    this.profileNames = profileNames;
  }

  public void exportToCsv(File file)
  {
    if (file.exists())
    {
      throw new IllegalStateException("File " + file.getAbsolutePath() + " exists");
    }
    try (Writer fileWriter = new OutputStreamWriter(new FileOutputStream(file), "ISO-8859-1"))
    {
      writeCsvHeader(fileWriter);
      writeCsvData(fileWriter);
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  private void writeCsvHeader(Writer fileWriter) throws IOException
  {
    if (profileNames != null && !profileNames.isEmpty())
    {
      fileWriter.write("Nummer;Profil;");
    }
    else
    {
      ScanResultForSingleQuantity1D firstScanResult = singleQuantityScanResults.get(0);
      fileWriter.write(firstScanResult.getScannedQuantity().getDisplayName() + ";");
    }
    for (ScanResultForSingleQuantity1D singleQuantityResult : singleQuantityScanResults)
    {
      fileWriter.write(singleQuantityResult.getResultQuantity().getSetId()
          + ":"
          + singleQuantityResult.getResultQuantity().getPhysicalQuantity().getDisplayName() + ";");
    }
    fileWriter.write("\r\n");
  }

  private void writeCsvData(Writer fileWriter) throws IOException
  {
    int i = 0;
    while (true)
    {
      Number x = null;
      if (profileNames != null && !profileNames.isEmpty())
      {
        String profileName = profileNames.get(i);
        if (profileName == null)
        {
          return;
        }
        fileWriter.write(Integer.toString(i) + ";" + profileName + ";");
        x = i;
      }
      for (ScanResultForSingleQuantity1D singleQuantityResult : singleQuantityScanResults)
      {
        if (x == null)
        {
          if (i >= singleQuantityResult.getSeries().getItemCount())
          {
            return;
          }
          x = singleQuantityResult.getSeries().getDataItem(i).getX();
          fileWriter.write(x.toString().replace(".", ",") + ";");
        }
        boolean xFound = false;
        for (int itemIndex = 0; itemIndex < singleQuantityResult.getSeries().getItemCount(); ++itemIndex)
        {
          XYDataItem dataItem = singleQuantityResult.getSeries().getDataItem(itemIndex);
          if (dataItem.getX().equals(x))
          {
            fileWriter.write(dataItem.getY().toString().replace(".", ",") + ";");
            xFound = true;
            break;
          }
        }
        if (!xFound)
        {
          fileWriter.write(";");
        }
      }
      fileWriter.write("\r\n");
      i++;
    }
  }
}
