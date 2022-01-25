package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.util.NumberUtil;

import javax.annotation.CheckForNull;

/**
 * Static methods for implementing hash-based collections.
 *
 * @author Jesse Wilson
 * @author Austin Appleby
 */

@Deprecated
final class Hashing {
    /*
     * These should be NumberUtil, but we need to use longs to force GWT to do the multiplications with
     * enough precision.
     */
    private static final long C1 = 0xcc9e2d51;
    private static final long C2 = 0x1b873593;
    private static final int MAX_TABLE_SIZE = NumberUtil.MAX_POWER_OF_TWO;

    private Hashing() {
    }

    /*
     * This method was rewritten in Java from an intermediate step of the Murmur hash function in
     * http://code.google.com/p/smhasher/source/browse/trunk/MurmurHash3.cpp, which contained the
     * following header:
     *
     * MurmurHash3 was written by Austin Appleby, and is placed in the public domain. The author
     * hereby disclaims copyright to this source code.
     */
    @Deprecated
    static int smear( int hashCode ) {
        return (int) (0x1b873593 * Integer.rotateLeft((int) (hashCode * 0xcc9e2d51), 15));
    }

    @Deprecated
    static int smearedHash( @CheckForNull Object o ) {
        return smear((o == null) ? 0 : o.hashCode());
    }

    @Deprecated
    static int closedTableSize( int expectedEntries, double loadFactor ) {
        // Get the recommended table size.
        // Round down to the nearest power of 2.
        expectedEntries = Math.max(expectedEntries, 2);
        int tableSize = Integer.highestOneBit(expectedEntries);
        // Check to make sure that we will not exceed the maximum load factor.
        if (expectedEntries > (int) (loadFactor * tableSize)) {
            tableSize <<= 1;
            return (tableSize > 0) ? tableSize : MAX_TABLE_SIZE;
        }
        return tableSize;
    }

    @Deprecated
    static boolean needsResizing( int size, int tableSize, double loadFactor ) {
        return size > loadFactor * tableSize && tableSize < NumberUtil.MAX_POWER_OF_TWO;
    }
}
