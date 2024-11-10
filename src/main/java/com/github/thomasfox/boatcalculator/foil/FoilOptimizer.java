package com.github.thomasfox.boatcalculator.foil;

import com.github.thomasfox.boatcalculator.calculate.CombinedCalculator;
import com.github.thomasfox.boatcalculator.calculate.MaterialConstants;
import com.github.thomasfox.boatcalculator.calculate.PhysicalQuantity;
import com.github.thomasfox.boatcalculator.interpolate.QuantityRelation;
import com.github.thomasfox.boatcalculator.interpolate.QuantityRelationLoader;
import com.github.thomasfox.boatcalculator.profile.ProfileGeometry;
import com.github.thomasfox.boatcalculator.profile.ProfileSelector;
import com.github.thomasfox.boatcalculator.value.PhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.value.SimplePhysicalQuantityValue;
import com.github.thomasfox.boatcalculator.valueset.SimpleValueSet;
import com.github.thomasfox.boatcalculator.valueset.ValueSet;

import java.io.*;
import java.util.List;
import java.util.Random;

public class FoilOptimizer
{
  public static final File PROFILE_DIRECTORY = new File("profiles");

  public static final double TARGET_DRAG = 20; // in N

  public static final double ACCEPTED_BENDING= 0.01; // in m
  public static final int VELOCITY = 7;
  public static final int LIFT = 800;

  private static final CombinedCalculator combinedCalculator = new CombinedCalculator();

  private static final Random random = new Random();

  private static final double RELATIVE_CUTOFF = 0.001;

  private static final int REMAINING_STEPS_START = 200;
  private static final int MATRIX_STEPS = 5;

  private static final QuantityRelation kinematicViscosityRelation;

  private static final ProfileSelector profileSelector = new ProfileSelector();

  private List<QuantityRelation> profilePolars;

  private ModifiableWingParameters modifiableWingParameters;

  static
  {
    Reader kinematicViscosityWaterReader
        = new InputStreamReader(FoilOptimizer.class.getResourceAsStream("/kinematicViscosity_water.txt"));
    kinematicViscosityRelation =
        new QuantityRelationLoader().load(kinematicViscosityWaterReader, "kinematicViscosityWater");
  }

  public static void main(String[] argv) throws IOException
  {
    if (argv != null && argv.length > 0 && argv[0].equalsIgnoreCase("step"))
    {
      new FoilOptimizer().stepOptimizer();
    }
    else {
      new FoilOptimizer().matrix();
    }
  }

  private boolean initForProfile(String profileName)
  {
    System.out.println("Analyzing " + profileName + " for velocity " + VELOCITY + "m/s and Lift " + LIFT + "N");
    profilePolars = profileSelector.loadXfoilResults(PROFILE_DIRECTORY, profileName);
    if (profilePolars.isEmpty())
    {
      System.out.println("No polar available");
      return false;
    }
    else if (profilePolars.size() == 1)
    {
      System.out.println("Only one polar available");
      return false;
    }

    double minimalReynoldsNumber = getMinimalReynoldsNumber(profilePolars);
    double minimalProfileChord = getProfileChordFromReynoldsNumber(minimalReynoldsNumber);

    double maximalReynoldsNumber = getMaximalReynoldsNumber(profilePolars);
    double maximalProfileChord = getProfileChordFromReynoldsNumber(maximalReynoldsNumber);

    modifiableWingParameters = new ModifiableWingParameters();
    modifiableWingParameters.getInnerChord().setLowerLimit(Math.max(minimalProfileChord + 0.001, 0.06));
    modifiableWingParameters.getOuterChord().setLowerLimit(Math.max(minimalProfileChord + 0.001, 0.06));
    modifiableWingParameters.getInnerChord().setUpperLimit(Math.min(maximalProfileChord - 0.001, 0.18));
    modifiableWingParameters.getOuterChord().setUpperLimit(Math.min(maximalProfileChord - 0.001, 0.18));
    ProfileGeometry profileGeometry = profileSelector.loadProfile(PROFILE_DIRECTORY, profileName);
    if (profileGeometry == null)
    {
      System.out.println("Could not load profile geometry for " + profileName);
      return false;
    }
    double secondMomentOfArea = profileGeometry.getSecondMomentOfArea();
    modifiableWingParameters.setNormalizedSecondMomentOfArea(secondMomentOfArea);
    return true;
  }

  public void stepOptimizer()
  {
    List<String> profileNames = profileSelector.getProfileNames(PROFILE_DIRECTORY);

    for (String profileName : profileNames)
    {
      if (!initForProfile(profileName))
      {
        continue;
      }

      int remainingSteps = 200;
      boolean relativeChangeIsSmall;
      Penalty penalty = getPenalty(profilePolars, modifiableWingParameters);
      if (penalty.getPenalty() == Double.MAX_VALUE)
      {
        System.out.println("Could not calculate total drag for start configuration " + modifiableWingParameters);
        continue;
      }
      do
      {
        relativeChangeIsSmall = true;
        for (PhysicalQuantity parameter : ModifiableWingParameters.PHYSICAL_QUANTITIES)
        {
          ModifiableWingParameters withStep = modifiableWingParameters.withStep(parameter);
          Penalty penaltyWithStep = getPenalty(profilePolars, withStep);
          double penaltyDifference = penaltyWithStep.getPenalty() - penalty.getPenalty();
          if (penaltyDifference < getRandomOffset(remainingSteps))
          {
            relativeChangeIsSmall
                &= -penaltyDifference/modifiableWingParameters.relativeStepSize(parameter) < RELATIVE_CUTOFF;
            penalty = penaltyWithStep;
            modifiableWingParameters = withStep;
            System.out.print('.');
          }
          else
          {
            ModifiableWingParameters withReverseStep = modifiableWingParameters.withReverseStep(parameter);
            Penalty penaltyWithReverseStep = getPenalty(profilePolars, withReverseStep);
            double penaltyDifferenceReverseStep = penaltyWithReverseStep.getPenalty() - penalty.getPenalty();
            if (penaltyDifferenceReverseStep < getRandomOffset(remainingSteps))
            {
              relativeChangeIsSmall
                  &= -penaltyDifferenceReverseStep/modifiableWingParameters.relativeStepSize(parameter) < RELATIVE_CUTOFF;
              penalty = penaltyWithReverseStep;
              modifiableWingParameters = withReverseStep;
              modifiableWingParameters.reverseStep(parameter);
              System.out.print('.');
            }
            else
            {
              relativeChangeIsSmall = false;
              modifiableWingParameters.halfStep(parameter);
              System.out.print('x');
            }
          }
        }

        remainingSteps--;
      }
      while (remainingSteps > 0 && !relativeChangeIsSmall);

      System.out.println();
      if (!relativeChangeIsSmall)
      {
        System.out.println("not converged");
      }
      System.out.println("------------- Final result after " + (REMAINING_STEPS_START - remainingSteps) + " steps");
      System.out.println("total drag: " + penalty.getTotalDrag() + " total bending: " + penalty.getTotalBending());
      System.out.println(modifiableWingParameters.toVerboseString());
    }
  }

  public static double getRandomOffset(int remainingSteps)
  {
    // The randomness ist there in order to not get stuck in a shallow local minimum.
    // The start value of 0.1 seems to give good results.
    // For convergence, it feels better if randomness disappears eventually at the end.
    double result = random.nextDouble();
    if (remainingSteps > 150)
    {
      result *= 0.1;
    }
    else if (remainingSteps > 100)
    {
      result *= 0.01;
    }
    else if (remainingSteps > 50)
    {
      result *= 0.001;
    }
    else
    {
      result = 0d;
    }
    return result;
  }

  private static Penalty getPenalty(
      List<QuantityRelation> profilePolars,
      ModifiableWingParameters modifiableWingParameters)
  {
    ValueSet valueSet = null;
    try {
      valueSet = getValueSetForFullWing(modifiableWingParameters);
      AveragedProfilePolar averagedProfilePolar = new AveragedProfilePolar(
          profilePolars,
          valueSet.getKnownQuantityValue(PhysicalQuantity.VELOCITY).getValue());
      HalfFoilGeometry halfFoilGeometry = TrapezoidalHalfFoilGeometry.fromModifiableWingParameters(modifiableWingParameters);
      valueSet.getQuantityRelations().add(averagedProfilePolar.average(halfFoilGeometry));
      combinedCalculator.calculate(valueSet, null, -1);

      Double totalBending = valueSet.getKnownQuantityValue(PhysicalQuantity.BENDING).getValue();
      PhysicalQuantityValue profileDragValue = valueSet.getKnownQuantityValue(PhysicalQuantity.PROFILE_DRAG);
      PhysicalQuantityValue inducedDragValue = valueSet.getKnownQuantityValue(PhysicalQuantity.INDUCED_DRAG);
      if (profileDragValue == null || inducedDragValue == null)
      {
        return Penalty.builder()
            .innerChord(valueSet.getKnownQuantityValue(PhysicalQuantity.WING_INNER_CHORD).getValue())
            .outerChord(valueSet.getKnownQuantityValue(PhysicalQuantity.WING_OUTER_CHORD).getValue())
            .span(valueSet.getKnownQuantityValue(PhysicalQuantity.WING_SPAN_IN_MEDIUM).getValue())
            .totalBending(totalBending)
            .build();
      }
      double totalDrag = profileDragValue.getValue() + inducedDragValue.getValue();
      return Penalty.builder()
          .innerChord(valueSet.getKnownQuantityValue(PhysicalQuantity.WING_INNER_CHORD).getValue())
          .outerChord(valueSet.getKnownQuantityValue(PhysicalQuantity.WING_OUTER_CHORD).getValue())
          .span(valueSet.getKnownQuantityValue(PhysicalQuantity.WING_SPAN_IN_MEDIUM).getValue())
          .totalDrag(totalDrag)
          .totalBending(totalBending)
          .build();
    }
    catch (RuntimeException e)
    {
      printState(valueSet, modifiableWingParameters);
      throw e;
    }
  }

  private static ValueSet getValueSetForFullWing(ModifiableWingParameters modifiableWingParameters)
  {
    ValueSet valueSet = getConstantsValueSet();
    valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(
        PhysicalQuantity.WING_SPAN_IN_MEDIUM,
        modifiableWingParameters.getWingSpan().getValue()));
    valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(
        PhysicalQuantity.AREA_IN_MEDIUM,
        modifiableWingParameters.getArea()));
    valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(
        PhysicalQuantity.WING_INNER_CHORD,
        modifiableWingParameters.getInnerChord().getValue()));
    valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(
        PhysicalQuantity.WING_OUTER_CHORD,
        modifiableWingParameters.getOuterChord().getValue()));
    valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(
        PhysicalQuantity.NORMALIZED_SECOND_MOMENT_OF_AREA,
        modifiableWingParameters.getNormalizedSecondMomentOfArea()));
    return valueSet;
  }

  private static ValueSet getConstantsValueSet()
  {
    ValueSet valueSet = new SimpleValueSet("foil", "foil");
    valueSet.setStartValueNoOverwrite(MaterialConstants.DENSITY_WATER);
    valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(PhysicalQuantity.TEMPERATURE, 20));
    valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(PhysicalQuantity.NCRIT, 9));
    valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(PhysicalQuantity.VELOCITY, VELOCITY));
    valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(
        PhysicalQuantity.MODULUS_OF_ELASTICITY,
        PhysicalQuantity.MODULUS_OF_ELASTICITY.getFixedValue()));

    valueSet.getQuantityRelations().add(kinematicViscosityRelation);
    valueSet.setStartValueNoOverwrite(new SimplePhysicalQuantityValue(
        PhysicalQuantity.LIFT,
        LIFT));
    return valueSet;
  }

  public static void printState(ValueSet ValueSet, ModifiableWingParameters modifiableWingParameters)
  {
    System.out.println("modifiableWingParameters: " + modifiableWingParameters);
    System.out.println("ValueSet: " + ValueSet);
  }

  private static double getMinimalReynoldsNumber(List<QuantityRelation> profilePolars)
  {
    Double result = null;
    for (QuantityRelation profilePolar : profilePolars)
    {
      double reynoldsNumber = profilePolar.getFixedQuantities().getValue(PhysicalQuantity.REYNOLDS_NUMBER);
      if (result == null || result > reynoldsNumber)
      {
        result = reynoldsNumber;
      }
    }
    return result;
  }

  private static double getMaximalReynoldsNumber(List<QuantityRelation> profilePolars)
  {
    Double result = null;
    for (QuantityRelation profilePolar : profilePolars)
    {
      double reynoldsNumber = profilePolar.getFixedQuantities().getValue(PhysicalQuantity.REYNOLDS_NUMBER);
      if (result == null || result < reynoldsNumber)
      {
        result = reynoldsNumber;
      }
    }
    return result;
  }

  private static double getProfileChordFromReynoldsNumber(double reynoldsNumber)
  {
    ValueSet valueSet = getConstantsValueSet();
    valueSet.setStartValueNoOverwrite(
        new SimplePhysicalQuantityValue(PhysicalQuantity.REYNOLDS_NUMBER, reynoldsNumber));
    combinedCalculator.calculate(valueSet, PhysicalQuantity.WING_CHORD, -1);
    return valueSet.getKnownQuantityValue(PhysicalQuantity.WING_CHORD).getValue();
  }

  public void matrix() throws IOException
  {
    PrintWriter writer = new PrintWriter(new FileWriter("result.csv"));
    writer.write("velocity: " + VELOCITY + "m/s, Lift: " + LIFT + "N, matrixSteps: " + MATRIX_STEPS + "\r\n");
    writer.write("profile name; total drag[N]; total bending[m]; inner chord[m]; outer chord[m]; span [m]\r\n");

    List<String> profileNames = profileSelector.getProfileNames(PROFILE_DIRECTORY);
    Penalty smallestOverallPenalty = null;
    String profileNameForSmallestOverallPenalty = null;

    for (String profileName : profileNames)
    {
//      if (!profileName.equals("fx61163-il"))
//      {
//        continue;
//      }
      if (!initForProfile(profileName))
      {
        continue;
      }

      Penalty smallestPenaltyForProfile = null;
      for (int innerChordIndex = 0; innerChordIndex <= MATRIX_STEPS; innerChordIndex++)
      {
        double innerChord = modifiableWingParameters.getInnerChord().getLowerLimit()
            + (modifiableWingParameters.getInnerChord().getUpperLimit() - modifiableWingParameters.getInnerChord().getLowerLimit()) * innerChordIndex / MATRIX_STEPS;
        System.out.printf("IC%.3f ", innerChord);
        modifiableWingParameters.getInnerChord().setValue(innerChord);
        Penalty smallestPenaltyForInnerChord = null;
        for (int outerChordIndex = 0; outerChordIndex <= innerChordIndex; outerChordIndex++)
        {
          modifiableWingParameters.getOuterChord().setValue(
              modifiableWingParameters.getOuterChord().getLowerLimit()
                  + (modifiableWingParameters.getOuterChord().getUpperLimit() - modifiableWingParameters.getOuterChord().getLowerLimit()) * outerChordIndex / MATRIX_STEPS);
          Penalty smallestPenaltyForOuterChord = null;
          for (int wingSpanIndex = 0; wingSpanIndex <= MATRIX_STEPS; wingSpanIndex++)
          {
            modifiableWingParameters.getWingSpan().setValue(
                modifiableWingParameters.getWingSpan().getLowerLimit()
                    + (modifiableWingParameters.getWingSpan().getUpperLimit() - modifiableWingParameters.getWingSpan().getLowerLimit()) * wingSpanIndex / MATRIX_STEPS);
            Penalty penalty = getPenalty(profilePolars, modifiableWingParameters);
            smallestPenaltyForOuterChord = penalty.getSmallerPenalty(smallestPenaltyForOuterChord);
            if (penalty.bendingTooLarge() || wingSpanIndex == MATRIX_STEPS)
            {
              System.out.printf(" D%.1f", smallestPenaltyForOuterChord.getTotalDrag());
              break;
            }
          }
          smallestPenaltyForInnerChord = smallestPenaltyForOuterChord.getSmallerPenalty(smallestPenaltyForInnerChord);
        }
        smallestPenaltyForProfile = smallestPenaltyForInnerChord.getSmallerPenalty(smallestPenaltyForProfile);
        System.out.println();
      }
      System.out.println("-----------");
      System.out.println("total drag: " + smallestPenaltyForProfile.getTotalDrag() + " total bending: " + smallestPenaltyForProfile.getTotalBending());
      System.out.println("inner chord: " + smallestPenaltyForProfile.getInnerChord() + " outer chord: " + smallestPenaltyForProfile.getOuterChord() + " span: " + smallestPenaltyForProfile.getSpan());
      writer.printf(profileName + ";%.3f;%.5f;%.3f;%.3f;%.3f;\r\n",
          smallestPenaltyForProfile.getTotalDrag(),
          smallestPenaltyForProfile.getTotalBending(),
          smallestPenaltyForProfile.getInnerChord(),
          smallestPenaltyForProfile.getOuterChord(),
          smallestPenaltyForProfile.getSpan());
      writer.flush();

      if (smallestOverallPenalty == null || smallestOverallPenalty.getPenalty() > smallestPenaltyForProfile.getPenalty())
      {
        smallestOverallPenalty = smallestPenaltyForProfile;
        profileNameForSmallestOverallPenalty = profileName;
      }
    }
    System.out.println("==================================");
    System.out.println(profileNameForSmallestOverallPenalty);
    System.out.println("total drag: " + smallestOverallPenalty.getTotalDrag() + " total bending: " + smallestOverallPenalty.getTotalBending());
    System.out.println("inner chord: " + smallestOverallPenalty.getInnerChord() + " outer chord: " + smallestOverallPenalty.getOuterChord() + " span: " + smallestOverallPenalty.getSpan());
    writer.close();
  }
}
