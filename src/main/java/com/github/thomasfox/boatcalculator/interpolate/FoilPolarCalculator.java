package com.github.thomasfox.boatcalculator.interpolate;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.foil.AveragedProfilePolar;
import com.github.thomasfox.boatcalculator.foil.HalfFoilGeometry;
import com.github.thomasfox.boatcalculator.foil.TrapezoidalHalfFoilGeometry;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

import java.util.ArrayList;
import java.util.List;

public class FoilPolarCalculator
{
  public void replaceFoilPolar(ValueSet valueSet)
  {
    HalfFoilGeometry halfFoilGeometry = TrapezoidalHalfFoilGeometry.fromValueSet(valueSet);
    if (halfFoilGeometry == null)
    {
      return;
    }
    if (!valueSet.isValueKnown(PhysicalQuantity.VELOCITY))
    {
      return;
    }
    double velocity = valueSet.getKnownQuantityValue(PhysicalQuantity.VELOCITY).getValue();
    if (!valueSet.isValueKnown(PhysicalQuantity.NCRIT))
    {
      return;
    }
    double ncrit = valueSet.getKnownQuantityValue(PhysicalQuantity.NCRIT).getValue();
    List<QuantityRelation> profilePolarQuantityRelations = new ArrayList<>();
    for (QuantityRelation quantityRelation : valueSet.getQuantityRelations())
    {
      if (quantityRelation.getFixedQuantities().containsQuantity(PhysicalQuantity.NCRIT)
          && quantityRelation.getFixedQuantities().containsQuantity(PhysicalQuantity.REYNOLDS_NUMBER)
          && quantityRelation.getFixedQuantities().getValue(PhysicalQuantity.NCRIT) == ncrit)
      {
        profilePolarQuantityRelations.add(quantityRelation);
      }
    }
    if (profilePolarQuantityRelations.size() < 2)
    {
      return;
    }
    AveragedProfilePolar averagedProfilePolar = new AveragedProfilePolar(profilePolarQuantityRelations, velocity);
    QuantityRelation quantityRelation = averagedProfilePolar.average(halfFoilGeometry);
    valueSet.getQuantityRelations().removeIf(q -> q.getName().equals(AveragedProfilePolar.QUANTITY_RELATON_NAME));
    valueSet.getQuantityRelations().add(quantityRelation);
  }
}
