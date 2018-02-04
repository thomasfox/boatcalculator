package com.github.thomasfox.sailboatcalculator.progress;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores information the state of the current calculation,
 * which then can be displayed to the user.
 * In particular trial values for variables are stored.
 * Listeners can be registered, which are notified each time
 * a trial value changes.
 */
public class CalculationState
{
  private static Map<String, String> stateMap = new LinkedHashMap<>();

  private static List<StateChangedListener> listeners = new ArrayList<>();

  /**
   * Sets the value of a trial value.
   *
   * @param key the name of the trial value
   * @param value the current value of the trial value, formatted as string.
   */
  public static void set(String key, String value)
  {
    stateMap.put(key, value);
    stateChanged();
  }

  /**
   * Sets the value of a trial value.
   *
   * @param key the name of the trial value
   * @param value the current value of the trial value.
   */
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

  public static void clear()
  {
    stateMap.clear();
    stateChanged();
  }

  /**
   * Broadcasts a stateChanged event to all listeners.
   */
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
