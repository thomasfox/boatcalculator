package com.github.thomasfox.wingcalculator.calculate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.thomasfox.wingcalculator.calculate.impl.BendingCalculator;
import com.github.thomasfox.wingcalculator.calculate.impl.InducedResistanceCoefficientCalculator;
import com.github.thomasfox.wingcalculator.calculate.impl.ReynoldsNumberCalculator;
import com.github.thomasfox.wingcalculator.calculate.impl.SecondMomentOfAreaCalculator;

public class CombinedCalculator
{
  public List<Calculator> calculators = new ArrayList<>();

  public CombinedCalculator()
  {
    calculators.add(new ReynoldsNumberCalculator());
    calculators.add(new InducedResistanceCoefficientCalculator());
    calculators.add(new BendingCalculator());
    calculators.add(new SecondMomentOfAreaCalculator());
  }

  public Map<PhysicalQuantity, Double> calculate(Map<PhysicalQuantity, Double> input)
  {
    boolean changed = false;
    int cutoff = 100;
    Map<PhysicalQuantity, Double> allKnownValues = new HashMap<>(input);
    Map<PhysicalQuantity, Double> result = new HashMap<>(input);
    do
    {
      for (Calculator calculator: calculators)
      {
        if (!calculator.areNeededQuantitiesPresent(allKnownValues))
        {
          continue;
        }
        if (calculator.isOutputPresent(allKnownValues))
        {
          continue;
        }
        double calculationResult = calculator.calculate(allKnownValues);
        allKnownValues.put(calculator.getOutputQuantity(), calculationResult);
        result.put(calculator.getOutputQuantity(), calculationResult);
        changed = true;
      }
      cutoff--;
    }
    while(changed && cutoff > 0);
    return result;
  }
}
