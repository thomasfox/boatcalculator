package com.github.thomasfox.boatcalculator.foil;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PartialWingState
{
  private static final int NUM_CHORDS = 50;

  private final HalfFoilGeometry halfFoilGeometry;

  private int chordStep = 0;

  List<WeightedReynoldsNumber> reynoldsNumbers = new ArrayList<>();

  public PartialWingState(HalfFoilGeometry halfFoilGeometry)
  {
    this.halfFoilGeometry = halfFoilGeometry;
  }

  public boolean nextStep()
  {
    chordStep++;
    return stepValid();
  }

  public double getChordForStep()
  {
    double halfwingSpan = halfFoilGeometry.getHalfwingSpan() / NUM_CHORDS * (0.5d + chordStep);
    return halfFoilGeometry.getChord(halfwingSpan);
  }

  public double getAreaForStep()
  {
    return getChordForStep() * halfFoilGeometry.getHalfwingSpan() / NUM_CHORDS;
  }

  private boolean stepValid()
  {
    return chordStep < NUM_CHORDS;
  }

  public void addReynoldsNumber(WeightedReynoldsNumber weightedReynoldsNumber)
  {
    reynoldsNumbers.add(weightedReynoldsNumber);
  }
}
