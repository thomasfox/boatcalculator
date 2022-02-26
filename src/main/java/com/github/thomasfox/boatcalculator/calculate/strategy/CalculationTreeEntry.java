package com.github.thomasfox.boatcalculator.calculate.strategy;

import java.io.PrintStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValueWithSetId;

class CalculationTreeEntry
{
  private PhysicalQuantityValueWithSetId value;

  private CalculationTreeEntry parent;

  private final Set<CalculationTreeEntry> children = new HashSet<>();

  /**
   * Use fromRoot or addCalculatedFrom methods to create new tree entries
   */
  private CalculationTreeEntry()
  {
  }

  public static CalculationTreeEntry fromRoot(PhysicalQuantityValueWithSetId calculatedValue)
  {
    CalculationTreeEntry result = new CalculationTreeEntry();
    result.value = calculatedValue;
    return result;
  }

  public CalculationTreeEntry addChild(PhysicalQuantityValueWithSetId toAdd)
  {
    CalculationTreeEntry newEntry = new CalculationTreeEntry();
    newEntry.value = toAdd;
    newEntry.parent = this;
    children.add(newEntry);
    return newEntry;
  }

  public boolean removeChild(CalculationTreeEntry toRemove)
  {
    return children.remove(toRemove);
  }

  public void clearChildren()
  {
    children.clear();;
  }

  public CalculationTreeEntry getParent()
  {
    return parent;
  }

  public PhysicalQuantityValueWithSetId getValue()
  {
    return value;
  }

  public Set<CalculationTreeEntry> getChildren()
  {
    return Collections.unmodifiableSet(children);
  }

  public Set<PhysicalQuantityValueWithSetId> getChildValues()
  {
    return children.stream()
        .map(CalculationTreeEntry::getValue)
        .collect(Collectors.toSet());
  }

  public boolean isLeaf()
  {
    return children.isEmpty();
  }

  public Set<CalculationTreeEntry> findLeaves()
  {
    Set<CalculationTreeEntry> result = new HashSet<>();
    findLeaves(result, 100);
    return result;
  }

  private void findLeaves(Set<CalculationTreeEntry> result, int cutoffAfterSteps)
  {
    if (cutoffAfterSteps <= 0 )
    {
      return;
    }
    if (isLeaf())
    {
      result.add(this);
    }
    else
    {
      for (CalculationTreeEntry child : children)
      {
        child.findLeaves(result, --cutoffAfterSteps);
      }
    }
  }

  public void removeLeavesExcept(PhysicalQuantityInSet doNotRemove)
  {
    Set<CalculationTreeEntry> leaves = findLeaves();
    while (!leaves.isEmpty())
    {
      Set<CalculationTreeEntry> newLeaves = new HashSet<>();
      for (CalculationTreeEntry leaf : leaves)
      {
        if (!leaf.getValue().getPhysicalQuantityInSet().equals(doNotRemove))
        {
          leaf.getParent().removeChild(leaf);
          if (leaf.getParent().isLeaf())
          {
            newLeaves.add(leaf.getParent());
          }
        }
      }
      leaves = newLeaves;
    }
  }

  public Set<CalculationTreeEntry> getNextDescendants(PhysicalQuantityInSet toFind)
  {
    Set<CalculationTreeEntry> result = new HashSet<>();
    for (CalculationTreeEntry child : children)
    {
      child.getNextDescendants(toFind, result, 100);
    }
    return result;
  }

  public void getNextDescendants(PhysicalQuantityInSet toFind, Set<CalculationTreeEntry> result, int cutoffAfterSteps)
  {
    if (cutoffAfterSteps <= 0)
    {
      return;
    }
    if (this.getValue().getPhysicalQuantityInSet().equals(toFind))
    {
      result.add(this);
      return;
    }
    for (CalculationTreeEntry child : children)
    {
      child.getNextDescendants(toFind, result, --cutoffAfterSteps);
    }
  }

  public Set<PhysicalQuantityValueWithSetId> getAllValuesOf(PhysicalQuantityInSet toFind)
  {
    Set<PhysicalQuantityValueWithSetId> result = new HashSet<>();
    getAllValuesOf(toFind, result);
    return result;
  }

  public void  getAllValuesOf(PhysicalQuantityInSet toFind, Set<PhysicalQuantityValueWithSetId> result)
  {
    if (value.getPhysicalQuantityInSet().equals(toFind))
    {
      result.add(value);
    }
    for (CalculationTreeEntry child : children)
    {
      child.getAllValuesOf(toFind, result);
    }
  }

  public void removeDuplicatePhysicalQuantitiesInSetExcept(PhysicalQuantityInSet doNotRemove)
  {
    Set<PhysicalQuantityInSet> alreadyFound = new HashSet<>();
    removeDuplicatePhysicalQuantitiesInSetExcept(doNotRemove, alreadyFound);
  }

  private void removeDuplicatePhysicalQuantitiesInSetExcept(PhysicalQuantityInSet doNotRemove, Set<PhysicalQuantityInSet> alreadyFound)
  {
    Set<CalculationTreeEntry> toIterateOver = new HashSet<>(children);
    for (CalculationTreeEntry child : toIterateOver)
    {
      Set<PhysicalQuantityInSet> newAlreadyFound = new HashSet<>(alreadyFound);
      PhysicalQuantityInSet physicalQuantityInSet = child.getValue().getPhysicalQuantityInSet();
      if (alreadyFound.contains(physicalQuantityInSet))
      {
        removeChild(child);
        continue;
      }
      if (!physicalQuantityInSet.equals(doNotRemove))
      {
        newAlreadyFound.add(physicalQuantityInSet);
      }
      child.removeDuplicatePhysicalQuantitiesInSetExcept(doNotRemove, newAlreadyFound);
    }
  }


  public void print(PrintStream printStream)
  {
    print(0, printStream);
  }

  private void print(int indent, PrintStream printStream)
  {
    for (int i = 0; i < indent; i++)
    {
      printStream.print(' ');
    }
    System.out.println(value);
    for (CalculationTreeEntry child : children)
    {
      child.print(indent + 2, printStream);
    }
  }

  @Override
  public String toString()
  {
    return value.toString();
  }
}