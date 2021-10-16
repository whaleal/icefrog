

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;


import javax.annotation.CheckForNull;
import java.util.Map;


/**
 * An object representing the differences between two MapUtil.
 *
 * @author Kevin Bourrillion
 * 
 */



public interface MapDifference<K extends Object, V extends Object> {
  /**
   * Returns {@code true} if there are no differences between the two MapUtil; that is, if the MapUtil are
   * equal.
   */
  boolean areEqual();

  /**
   * Returns an unmodifiable map containing the entries from the left map whose keys are not present
   * in the right map.
   */
  Map<K, V> entriesOnlyOnLeft();

  /**
   * Returns an unmodifiable map containing the entries from the right map whose keys are not
   * present in the left map.
   */
  Map<K, V> entriesOnlyOnRight();

  /**
   * Returns an unmodifiable map containing the entries that appear in both MapUtil; that is, the
   * intersection of the two MapUtil.
   */
  Map<K, V> entriesInCommon();

  /**
   * Returns an unmodifiable map describing keys that appear in both MapUtil, but with different
   * values.
   */
  Map<K, ValueDifference<V>> entriesDiffering();

  /**
   * Compares the specified object with this instance for equality. Returns {@code true} if the
   * given object is also a {@code MapDifference} and the values returned by the {@link
   * #entriesOnlyOnLeft()}, {@link #entriesOnlyOnRight()}, {@link #entriesInCommon()} and {@link
   * #entriesDiffering()} of the two instances are equal.
   */
  @Override
  boolean equals(@CheckForNull Object object);

  /**
   * Returns the hash code for this instance. This is defined as the hash code of
   *
   * <pre>{@code
   * Arrays.asList(entriesOnlyOnLeft(), entriesOnlyOnRight(),
   *     entriesInCommon(), entriesDiffering())
   * }</pre>
   */
  @Override
  int hashCode();

  /**
   * A difference between the mappings from two MapUtil with the same key. The {@link #leftValue} and
   * {@link #rightValue} are not equal, and one but not both of them may be null.
   *
   * 
   */

  interface ValueDifference<V extends Object> {
    /** Returns the value from the left map (possibly null). */
    @ParametricNullness
    V leftValue();

    /** Returns the value from the right map (possibly null). */
    @ParametricNullness
    V rightValue();

    /**
     * Two instances are considered equal if their {@link #leftValue()} values are equal and their
     * {@link #rightValue()} values are also equal.
     */
    @Override
    boolean equals(@CheckForNull Object other);

    /**
     * The hash code equals the value {@code Arrays.asList(leftValue(), rightValue()).hashCode()}.
     */
    @Override
    int hashCode();
  }
}
