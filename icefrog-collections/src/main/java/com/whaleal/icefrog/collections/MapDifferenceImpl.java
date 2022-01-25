package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.util.ObjectUtil;

import javax.annotation.CheckForNull;
import java.util.Map;

/**
 * @author wh
 */
static class MapDifferenceImpl< K extends Object, V extends Object >
        implements MapDifference< K, V > {
    final Map< K, V > onlyOnLeft;
    final Map< K, V > onlyOnRight;
    final Map< K, V > onBoth;
    final Map< K, ValueDifference< V > > differences;

    MapDifferenceImpl(
            Map< K, V > onlyOnLeft,
            Map< K, V > onlyOnRight,
            Map< K, V > onBoth,
            Map< K, ValueDifference< V > > differences ) {
        this.onlyOnLeft = unmodifiableMap(onlyOnLeft);
        this.onlyOnRight = unmodifiableMap(onlyOnRight);
        this.onBoth = unmodifiableMap(onBoth);
        this.differences = unmodifiableMap(differences);
    }

    @Override
    public boolean areEqual() {
        return onlyOnLeft.isEmpty() && onlyOnRight.isEmpty() && differences.isEmpty();
    }

    @Override
    public Map< K, V > entriesOnlyOnLeft() {
        return onlyOnLeft;
    }

    @Override
    public Map< K, V > entriesOnlyOnRight() {
        return onlyOnRight;
    }

    @Override
    public Map< K, V > entriesInCommon() {
        return onBoth;
    }

    @Override
    public Map< K, ValueDifference< V > > entriesDiffering() {
        return differences;
    }

    @Override
    public boolean equals( @CheckForNull Object object ) {
        if (object == this) {
            return true;
        }
        if (object instanceof MapDifference) {
            MapDifference< ?, ? > other = (MapDifference< ?, ? >) object;
            return entriesOnlyOnLeft().equals(other.entriesOnlyOnLeft())
                    && entriesOnlyOnRight().equals(other.entriesOnlyOnRight())
                    && entriesInCommon().equals(other.entriesInCommon())
                    && entriesDiffering().equals(other.entriesDiffering());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ObjectUtil.hashCode(
                entriesOnlyOnLeft(), entriesOnlyOnRight(), entriesInCommon(), entriesDiffering());
    }

    @Override
    public String toString() {
        if (areEqual()) {
            return "equal";
        }

        StringBuilder result = new StringBuilder("not equal");
        if (!onlyOnLeft.isEmpty()) {
            result.append(": only on left=").append(onlyOnLeft);
        }
        if (!onlyOnRight.isEmpty()) {
            result.append(": only on right=").append(onlyOnRight);
        }
        if (!differences.isEmpty()) {
            result.append(": value differences=").append(differences);
        }
        return result.toString();
    }
}
