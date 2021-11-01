package com.whaleal.icefrog.collections;

/**
 * Indicates whether an endpoint of some range is contained in the range itself ("closed") or not
 * ("open"). If a range is unbounded on a side, it is neither open nor closed on that side; the
 * bound simply does not exist.
 */


public enum BoundType {
    /**
     * The endpoint value <i>is not</i> considered part of the set ("exclusive").
     */
    OPEN(false),
    CLOSED(true);

    final boolean inclusive;

    BoundType( boolean inclusive ) {
        this.inclusive = inclusive;
    }

    /**
     * Returns the bound type corresponding to a boolean value for inclusivity.
     */
    static BoundType forBoolean( boolean inclusive ) {
        return inclusive ? CLOSED : OPEN;
    }
}
