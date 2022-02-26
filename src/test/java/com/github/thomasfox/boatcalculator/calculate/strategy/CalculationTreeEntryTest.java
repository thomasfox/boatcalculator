package com.github.thomasfox.boatcalculator.calculate.strategy;

import static  org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;

import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.value.CalculatedPhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.CalculatedPhysicalQuantityWithSetId;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityInSet;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValueWithSetId;
import com.github.thomasfox.boatcalculator.valueset.impl.Crew;
import com.github.thomasfox.boatcalculator.valueset.impl.Rigg;

public class CalculationTreeEntryTest
{
  private final PhysicalQuantityValueWithSetId CREW_LEVER_WEIGHT = new CalculatedPhysicalQuantityWithSetId(
      new CalculatedPhysicalQuantityValue(
          new SimplePhysicalQuantityValue(PhysicalQuantity.LEVER_WEIGHT, 0.1d),
          "me",
          true),
      Crew.ID);

  private final PhysicalQuantityValueWithSetId CREW_LEVER_WEIGHT2 = new CalculatedPhysicalQuantityWithSetId(
      new CalculatedPhysicalQuantityValue(
          new SimplePhysicalQuantityValue(PhysicalQuantity.LEVER_WEIGHT, 0.2d),
          "me",
          true),
      Crew.ID);


  private final PhysicalQuantityValueWithSetId CREW_LEVER_WEIGHT3 = new CalculatedPhysicalQuantityWithSetId(
      new CalculatedPhysicalQuantityValue(
          new SimplePhysicalQuantityValue(PhysicalQuantity.LEVER_WEIGHT, 0.3d),
          "me",
          true),
      Crew.ID);


  private final PhysicalQuantityValueWithSetId CREW_WEIGHT = new SimplePhysicalQuantityValueWithSetId(
      new SimplePhysicalQuantityValue(PhysicalQuantity.WEIGHT, 0.75d),
      Crew.ID);

  private final PhysicalQuantityValueWithSetId RIGG_LIFT = new CalculatedPhysicalQuantityWithSetId(
      new CalculatedPhysicalQuantityValue(
          new SimplePhysicalQuantityValue(PhysicalQuantity.LIFT, 0.75d),
          "me",
          true),
      Rigg.ID);


  @Test
  public void fromRoot()
  {
    CalculationTreeEntry root = CalculationTreeEntry.fromRoot(CREW_LEVER_WEIGHT);

    assertThat(root.getParent()).isNull();
    assertThat(root.getValue()).isSameAs(CREW_LEVER_WEIGHT);
    assertThat(root.getChildren()).isEmpty();
  }

  @Test
  public void addChild()
  {
    CalculationTreeEntry root = CalculationTreeEntry.fromRoot(CREW_LEVER_WEIGHT);
    CalculationTreeEntry child = root.addChild(CREW_WEIGHT);

    assertThat(root.getParent()).isNull();
    assertThat(root.getValue()).isSameAs(CREW_LEVER_WEIGHT);
    assertThat(root.getChildren()).containsOnly(child);
    assertThat(child.getParent()).isSameAs(root);
    assertThat(child.getValue()).isSameAs(CREW_WEIGHT);
    assertThat(child.getChildren()).isEmpty();
  }

  @Test
  public void removeChild()
  {
    CalculationTreeEntry root = CalculationTreeEntry.fromRoot(CREW_LEVER_WEIGHT);
    CalculationTreeEntry child = root.addChild(CREW_WEIGHT);
    CalculationTreeEntry child2 = root.addChild(RIGG_LIFT);

    root.removeChild(child);

    assertThat(root.getParent()).isNull();
    assertThat(root.getValue()).isSameAs(CREW_LEVER_WEIGHT);
    assertThat(root.getChildren()).containsOnly(child2);
    assertThat(child2.getParent()).isSameAs(root);
    assertThat(child2.getValue()).isSameAs(RIGG_LIFT);
    assertThat(child2.getChildren()).isEmpty();
  }

  @Test
  public void clearChildren()
  {
    CalculationTreeEntry root = CalculationTreeEntry.fromRoot(CREW_LEVER_WEIGHT);
    root.addChild(CREW_WEIGHT);
    root.addChild(RIGG_LIFT);

    root.clearChildren();

    assertThat(root.getParent()).isNull();
    assertThat(root.getValue()).isSameAs(CREW_LEVER_WEIGHT);
    assertThat(root.getChildren()).isEmpty();
  }

  @Test
  public void getChildValues()
  {
    CalculationTreeEntry root = CalculationTreeEntry.fromRoot(CREW_LEVER_WEIGHT);
    root.addChild(CREW_WEIGHT);
    root.addChild(RIGG_LIFT);

    Set<PhysicalQuantityValueWithSetId> result = root.getChildValues();

    assertThat(result).containsOnly(CREW_WEIGHT, RIGG_LIFT);
  }

  @Test
  public void isLeaf()
  {
    CalculationTreeEntry root = CalculationTreeEntry.fromRoot(CREW_LEVER_WEIGHT);
    CalculationTreeEntry child = root.addChild(CREW_WEIGHT);

    assertThat(root.isLeaf()).isFalse();
    assertThat(child.isLeaf()).isTrue();
  }

  @Test
  public void findLeaves()
  {
    CalculationTreeEntry root = CalculationTreeEntry.fromRoot(CREW_LEVER_WEIGHT);
    CalculationTreeEntry child1 = root.addChild(CREW_WEIGHT);
    CalculationTreeEntry grandchild = child1.addChild(RIGG_LIFT);
    CalculationTreeEntry child2 = root.addChild(RIGG_LIFT);

    Set<CalculationTreeEntry> result = root.findLeaves();

    assertThat(result).containsOnly(grandchild, child2);
  }

  @Test
  public void removeLeavesExcept()
  {
    CalculationTreeEntry root = CalculationTreeEntry.fromRoot(CREW_LEVER_WEIGHT);
    CalculationTreeEntry child1 = root.addChild(CREW_WEIGHT);
    CalculationTreeEntry grandchild1 = child1.addChild(RIGG_LIFT);
    CalculationTreeEntry grandgrandchild1 = grandchild1.addChild(CREW_LEVER_WEIGHT2);
    grandchild1.addChild(CREW_WEIGHT);
    CalculationTreeEntry child2 = root.addChild(CREW_LEVER_WEIGHT3);
    child2.addChild(CREW_WEIGHT);
    root.addChild(RIGG_LIFT);

    root.removeLeavesExcept(new PhysicalQuantityInSet(PhysicalQuantity.LEVER_WEIGHT, Crew.ID));

    assertThat(root.getChildren()).containsOnly(child1, child2);
    assertThat(child1.getChildren()).containsOnly(grandchild1);
    assertThat(grandchild1.getChildren()).containsOnly(grandgrandchild1);
    assertThat(child2.getChildren()).isEmpty();
  }

  @Test
  public void getNextDescendants()
  {
    CalculationTreeEntry root = CalculationTreeEntry.fromRoot(CREW_LEVER_WEIGHT);
    CalculationTreeEntry child1 = root.addChild(CREW_WEIGHT);
    CalculationTreeEntry grandchild1 = child1.addChild(CREW_LEVER_WEIGHT2);
    grandchild1.addChild(RIGG_LIFT);
    CalculationTreeEntry child2 = root.addChild(CREW_LEVER_WEIGHT3);
    CalculationTreeEntry grandchild2 = child2.addChild(RIGG_LIFT);
    grandchild2.addChild(CREW_LEVER_WEIGHT);
    root.addChild(RIGG_LIFT);

    Set<CalculationTreeEntry> result = root.getNextDescendants(new PhysicalQuantityInSet(PhysicalQuantity.LEVER_WEIGHT, Crew.ID));
    assertThat(result).containsOnly(grandchild1, child2);
  }

  @Test
  public void getAllValuesOf()
  {
    CalculationTreeEntry root = CalculationTreeEntry.fromRoot(CREW_LEVER_WEIGHT);
    CalculationTreeEntry child1 = root.addChild(CREW_WEIGHT);
    CalculationTreeEntry grandchild1 = child1.addChild(CREW_LEVER_WEIGHT2);
    grandchild1.addChild(RIGG_LIFT);
    CalculationTreeEntry child2 = root.addChild(CREW_LEVER_WEIGHT3);
    CalculationTreeEntry grandchild2 = child2.addChild(RIGG_LIFT);
    grandchild2.addChild(CREW_LEVER_WEIGHT);
    root.addChild(RIGG_LIFT);

    Set<PhysicalQuantityValueWithSetId> result = root.getAllValuesOf(new PhysicalQuantityInSet(PhysicalQuantity.LEVER_WEIGHT, Crew.ID));
    assertThat(result).containsOnly(CREW_LEVER_WEIGHT, CREW_LEVER_WEIGHT2, CREW_LEVER_WEIGHT3);
  }

  @Test
  public void removeDuplicatePhysicalQuantitiesInSetExcept()
  {
    CalculationTreeEntry root = CalculationTreeEntry.fromRoot(CREW_LEVER_WEIGHT);
    CalculationTreeEntry child1 = root.addChild(CREW_WEIGHT);
    CalculationTreeEntry grandchild1 = child1.addChild(CREW_LEVER_WEIGHT2);
    CalculationTreeEntry grandgrandchild1 = grandchild1.addChild(RIGG_LIFT);
    CalculationTreeEntry child2 = root.addChild(RIGG_LIFT);
    CalculationTreeEntry grandchild2 = child2.addChild(CREW_LEVER_WEIGHT3);
    grandchild2.addChild(RIGG_LIFT);
    CalculationTreeEntry child3 = root.addChild(RIGG_LIFT);
    child3.addChild(RIGG_LIFT);

    root.removeDuplicatePhysicalQuantitiesInSetExcept(new PhysicalQuantityInSet(PhysicalQuantity.LEVER_WEIGHT, Crew.ID));
    assertThat(root.getChildren()).containsOnly(child1, child2, child3);
    assertThat(child1.getChildren()).containsOnly(grandchild1);
    assertThat(grandchild1.getChildren()).containsOnly(grandgrandchild1);
    assertThat(child2.getChildren()).containsOnly(grandchild2);
    assertThat(grandchild2.getChildren()).isEmpty();
    assertThat(child3.getChildren()).isEmpty();
  }
}
