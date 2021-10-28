

package com.whaleal.icefrog.collections;

import java.util.Random;

/**
 * Utility class for being able to seed a {@link Random} value with a passed in seed from a
 * benchmark parameter.
 *
 * <p>TODO: Remove this class once Caliper has a better way.
 *
 * @author Nicholaus Shupe
 */
public final class SpecialRandom extends Random {
  public static SpecialRandom valueOf(String s) {
    return (s.length() == 0) ? new SpecialRandom() : new SpecialRandom(Long.parseLong(s));
  }

  private final boolean hasSeed;
  private final long seed;

  public SpecialRandom() {
    this.hasSeed = false;
    this.seed = 0;
  }

  public SpecialRandom(long seed) {
    super(seed);
    this.hasSeed = true;
    this.seed = seed;
  }

  @Override
  public String toString() {
    return hasSeed ? "(seed:" + seed : "(default seed)";
  }

  private static final long serialVersionUID = 0;
}
