package com.github.thomasfox.sailboatcalculator.iterate;

import java.io.File;
import java.util.Iterator;

import com.github.thomasfox.sailboatcalculator.profile.ProfileGeometry;
import com.github.thomasfox.sailboatcalculator.profile.ProfileSelector;

public class ProfileIterator implements Iterator<ProfileGeometry>
{
  private final ProfileSelector profileSelector = new ProfileSelector();

  private final File profileDirectory;

  private final Iterator<String> profileNameIterator;

  public ProfileIterator(File profileDirectory)
  {
    this.profileDirectory = profileDirectory;
    profileNameIterator = profileSelector.getProfileNames(profileDirectory).iterator();
  }

  @Override
  public boolean hasNext()
  {
    return profileNameIterator.hasNext();
  }

  @Override
  public ProfileGeometry next()
  {
    String profileName = profileNameIterator.next();
    return profileSelector.loadProfile(profileDirectory, profileName);
  }

}
