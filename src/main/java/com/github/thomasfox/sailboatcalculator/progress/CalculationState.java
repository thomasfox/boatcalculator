package com.github.thomasfox.sailboatcalculator.progress;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CalculationState
{
  private static Map<String, String> stateMap = new LinkedHashMap<>();

  private static List<StateChangedListener> listeners = new ArrayList<>();

  public static void set(String key, String value)
  {
    stateMap.put(key, value);
    stateChanged();
  }

  public static void set(String key, double value)
  {
    String displayValue;
    try
    {
      BigDecimal bd = new BigDecimal(value);
      bd = bd.round(new MathContext(3));
      displayValue = bd.toPlainString();
    }
    catch (Exception e)
    {
      displayValue = Double.toString(value);
    }
    stateMap.put(key, key + ":" + displayValue);
    stateChanged();
  }

  public static void stateChanged()
  {
    for (StateChangedListener listener : listeners)
    {
      listener.stateChanged();
    }
  }

  public static void register(StateChangedListener listener)
  {
    listeners.add(listener);
  }

  public static Map<String, String> getState()
  {
    return Collections.unmodifiableMap(stateMap);
  }
}
