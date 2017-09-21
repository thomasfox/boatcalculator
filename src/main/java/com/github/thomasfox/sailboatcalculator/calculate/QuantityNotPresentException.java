package com.github.thomasfox.sailboatcalculator.calculate;

public class QuantityNotPresentException extends CalculatorException
{
  private static final long serialVersionUID = 1L;

  private final PhysicalQuantity missingQuantity;

  public QuantityNotPresentException(PhysicalQuantity missingQuantity)
  {
    super("The quantity " + missingQuantity.getDisplayName() + " is needed but not present");
    this.missingQuantity = missingQuantity;
  }

  public PhysicalQuantity getMissingQuantity()
  {
    return missingQuantity;
  }
}
