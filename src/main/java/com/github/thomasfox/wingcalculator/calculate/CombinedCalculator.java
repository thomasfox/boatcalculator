package com.github.thomasfox.wingcalculator.calculate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.thomasfox.wingcalculator.calculate.impl.BendingCalculator;
import com.github.thomasfox.wingcalculator.calculate.impl.InducedResistanceCoefficientCalculator;
import com.github.thomasfox.wingcalculator.calculate.impl.LiftCoefficientCalculator;
import com.github.thomasfox.wingcalculator.calculate.impl.ProfileDragCalculator;
import com.github.thomasfox.wingcalculator.calculate.impl.ReynoldsNumberCalculator;
import com.github.thomasfox.wingcalculator.calculate.impl.SecondMomentOfAreaCalculator;
import com.github.thomasfox.wingcalculator.calculate.impl.ThicknessCalculator;
import com.github.thomasfox.wingcalculator.calculate.impl.WingDepthFromSecondMomentOfAreaCalculator;
import com.github.thomasfox.wingcalculator.interpolate.InterpolatorException;
import com.github.thomasfox.wingcalculator.interpolate.QuantityRelations;

public class CombinedCalculator
{
  public List<Calculator> calculators = new ArrayList<>();

  public List<QuantityRelations> quantityRelationsList = new ArrayList<>();

  public CombinedCalculator(List<QuantityRelations> quantityRelationsList)
  {
    calculators.add(new ReynoldsNumberCalculator());
    calculators.add(new InducedResistanceCoefficientCalculator());
    calculators.add(new BendingCalculator());
    calculators.add(new SecondMomentOfAreaCalculator());
    calculators.add(new LiftCoefficientCalculator());
    calculators.add(new ThicknessCalculator());
    calculators.add(new WingDepthFromSecondMomentOfAreaCalculator());
    calculators.add(new ProfileDragCalculator());

    this.quantityRelationsList.addAll(quantityRelationsList);
  }

  public Map<PhysicalQuantity, Double> calculate(Map<PhysicalQuantity, Double> input)
  {
    boolean changed;
    int cutoff = 100;
    Map<PhysicalQuantity, Double> allKnownValues = new HashMap<>(input);
    Map<PhysicalQuantity, Double> result = new HashMap<>();
    do
    {
      changed = false;
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
      for (QuantityRelations quantityRelations : quantityRelationsList)
      {
        for (PhysicalQuantity keyQuantity : quantityRelations.getRelatedQuantities())
        {
          Double knownKeyValue = keyQuantity.getValueFromAvailableQuantities(allKnownValues);
          if (knownKeyValue != null)
          {
            for (PhysicalQuantity relatedQuantity : quantityRelations.getRelatedQuantities())
            {
              Double knownRelatedValue = relatedQuantity.getValueFromAvailableQuantities(allKnownValues);
              if (knownRelatedValue == null)
              {
                try
                {
                  Double relatedValue = quantityRelations.interpolateValueFrom(relatedQuantity, keyQuantity, knownKeyValue);
                  allKnownValues.put(relatedQuantity, relatedValue);
                  changed = true;
                  result.put(relatedQuantity, relatedValue);
                  System.out.println("Calculated " + relatedQuantity.getDisplayName()
                  + " from quantityRelations " + quantityRelations.getName()
                  + " with fixed quantities " + quantityRelations.printFixedQuantities());
                }
                catch (InterpolatorException e)
                {
                  System.out.println("Could not calculate " + relatedQuantity.getDisplayName()
                  + " from quantityRelations " + quantityRelations.getName()
                  + " with fixed quantities " + quantityRelations.printFixedQuantities());
                }
              }
            }
          }
        }
      }
      cutoff--;
    }
    while(changed && cutoff > 0);
    return result;
  }
}
