package com.github.thomasfox.boatcalculator.foil;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity.*;

@Data
class ModifiableWingParameters
{
  public static final Set<PhysicalQuantity> PHYSICAL_QUANTITIES
      = Set.of(WING_SPAN, WING_INNER_CHORD, WING_OUTER_CHORD);

  @Getter
  private LimitedValue wingSpan = new LimitedValue(1d, 0.1d, 2d);

  @Getter
  private LimitedValue innerChord = new LimitedValue(0.1d, 0.01d, 0.5d);

  @Getter
  private LimitedValue outerChord = new LimitedValue(0.05d, 0.01d, 0.5d);

  private final Map<PhysicalQuantity, Double> steps = new HashMap<>();

  private double normalizedSecondMomentOfArea;

  public ModifiableWingParameters()
  {
    steps.put(WING_SPAN, 0.2d);
    steps.put(WING_INNER_CHORD, 0.02d);
    steps.put(WING_OUTER_CHORD, 0.01d);
    correctForMinimalAndMaximalValues();
  }

  public double getAverageChord()
  {
    return (innerChord.getValue() + outerChord.getValue()) / 2;
  }

  public double getArea()
  {
    return getAverageChord() * wingSpan.getValue();
  }

  private ModifiableWingParameters copy()
  {
    ModifiableWingParameters result = new ModifiableWingParameters();
    result.wingSpan = new LimitedValue(wingSpan.getValue(), wingSpan.getLowerLimit(), wingSpan.getUpperLimit());
    result.innerChord = new LimitedValue(innerChord.getValue(), innerChord.getLowerLimit(), innerChord.getUpperLimit());
    result.outerChord = new LimitedValue(outerChord.getValue(), outerChord.getLowerLimit(), outerChord.getUpperLimit());
    result.steps.putAll(steps);
    result.normalizedSecondMomentOfArea = normalizedSecondMomentOfArea;
    return result;
  }

  public Double getStep(PhysicalQuantity parameter)
  {
    return steps.get(parameter);
  }

  public void setStep(PhysicalQuantity parameter, Double value)
  {
    steps.put(parameter, value);
  }

  /**
   * Returns a copy with the value one step further.
   */
  public ModifiableWingParameters withStep(PhysicalQuantity parameter)
  {
    ModifiableWingParameters result = copy();
    LimitedValue limitedValue = getLimitedValue(parameter);

    double newValue = limitedValue.getValue() + steps.get(parameter);
    if (newValue < limitedValue.getLowerLimit())
    {
      newValue = limitedValue.getLowerLimit();
    }
    if (newValue > limitedValue.getUpperLimit())
    {
      newValue = limitedValue.getUpperLimit();
    }
    result.setValue(parameter, newValue);
    return result;
  }

  private LimitedValue getLimitedValue(PhysicalQuantity parameter)
  {
    LimitedValue limitedValue;
    if (parameter == WING_SPAN)
    {
      limitedValue = wingSpan;
    }
    else if (parameter == WING_INNER_CHORD)
    {
      limitedValue = innerChord;
    }
    else if (parameter == WING_OUTER_CHORD)
    {
      limitedValue = outerChord;
    }
    else
    {
      throw new IllegalArgumentException("Unknown parameter " + parameter);
    }
    return limitedValue;
  }

  private void setValue(PhysicalQuantity parameter, double newValue)
  {
    if (parameter == WING_SPAN)
    {
      wingSpan.setValue(newValue);
    }
    else if (parameter == WING_INNER_CHORD)
    {
      innerChord.setValue(newValue);
    }
    else if (parameter == WING_OUTER_CHORD)
    {
      outerChord.setValue(newValue);
    }
    else
    {
      throw new IllegalArgumentException("Unknown parameter " + parameter);
    }
  }


  public ModifiableWingParameters withReverseStep(PhysicalQuantity parameter)
  {
    ModifiableWingParameters result = copy();
    LimitedValue limitedValue = getLimitedValue(parameter);
    double newValue = limitedValue.getValue() - steps.get(parameter);
    if (newValue < limitedValue.getLowerLimit())
    {
      newValue = limitedValue.getLowerLimit();
    }
    if (newValue > limitedValue.getUpperLimit())
    {
      newValue = limitedValue.getUpperLimit();
    }
    result.setValue(parameter, newValue);
    return result;
  }

  public void halfStep(PhysicalQuantity parameter)
  {
    steps.put(parameter, steps.get(parameter) * 0.5d);
  }

  public void reverseStep(PhysicalQuantity parameter)
  {
    steps.put(parameter, steps.get(parameter) * -1d);
  }

  public double relativeStepSize(PhysicalQuantity parameter)
  {
     return Math.abs(steps.get(parameter)/ getLimitedValue(parameter).getValue());
  }

  private void correctForMinimalAndMaximalValues()
  {
    correctForMinimalAndMaximalValue(wingSpan);
    correctForMinimalAndMaximalValue(innerChord);
    correctForMinimalAndMaximalValue(outerChord);
  }

  private static void correctForMinimalAndMaximalValue(LimitedValue valueEntry)
  {
    if (valueEntry.getValue() < valueEntry.getLowerLimit())
    {
      valueEntry.setValue(valueEntry.getLowerLimit());
    }
    if (valueEntry.getValue() > valueEntry.getUpperLimit())
    {
      valueEntry.setValue(valueEntry.getUpperLimit());
    }
  }

  public String toVerboseString()
  {
    return "wing span: " + wingSpan.getValue() + " (" + steps.get(WING_SPAN) + ")"
        + "\ninner chord: " + innerChord.getValue() + " (" + steps.get(WING_INNER_CHORD) + ")"
        + "\nouter chord: " + outerChord.getValue() + " (" + steps.get(WING_OUTER_CHORD) + ")";
  }
}
