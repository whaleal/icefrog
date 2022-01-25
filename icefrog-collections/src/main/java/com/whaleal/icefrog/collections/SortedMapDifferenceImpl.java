package com.whaleal.icefrog.collections;

import java.util.SortedMap;

/**
 * @author wh
 */
static class SortedMapDifferenceImpl< K extends Object, V extends Object >
        extends MapDifferenceImpl< K, V > implements SortedMapDifference< K, V > {
    SortedMapDifferenceImpl(
            SortedMap< K, V > onlyOnLeft,
            SortedMap< K, V > onlyOnRight,
            SortedMap< K, V > onBoth,
            SortedMap< K, ValueDifference< V > > differences ) {
        super(onlyOnLeft, onlyOnRight, onBoth, differences);
    }

    @Override
    public SortedMap< K, ValueDifference< V > > entriesDiffering() {
        return (SortedMap< K, ValueDifference< V > >) super.entriesDiffering();
    }

    @Override
    public SortedMap< K, V > entriesInCommon() {
        return (SortedMap< K, V >) super.entriesInCommon();
    }

    @Override
    public SortedMap< K, V > entriesOnlyOnLeft() {
        return (SortedMap< K, V >) super.entriesOnlyOnLeft();
    }

    @Override
    public SortedMap< K, V > entriesOnlyOnRight() {
        return (SortedMap< K, V >) super.entriesOnlyOnRight();
    }
}
