package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.whaleal.icefrog.core.lang.Precondition.checkNonnegative;

/**
 * Implementation of {@link Table} using linked hash tables. This guarantees predictable iteration
 * order of the various views.
 *
 * <p>The views returned by {@link #column}, {@link #columnKeySet()}, and {@link #columnMap()} have
 * iterators that don't support {@code remove()}. Otherwise, all optional operations are supported.
 * Null row keys, columns keys, and values are not supported.
 *
 * <p>Lookups by row key are often faster than lookups by column key, because the data is stored in
 * a {@code Map<R, Map<C, V>>}. A method call like {@code column(columnKey).get(rowKey)} still runs
 * quickly, since the row key is provided. However, {@code column(columnKey).size()} takes longer,
 * since an iteration across all row keys occurs.
 *
 * <p>Note that this implementation is not synchronized. If multiple threads access this table
 * concurrently and one of the threads modifies the table, it must be synchronized externally.
 *
 * <p>See the Guava User Guide article on <a href=
 * "https://github.com/google/guava/wiki/NewCollectionTypesExplained#table"> {@code Table}</a>.
 */


public class HashBasedTable<R, C, V> extends StandardTable<R, C, V> {
    private static final long serialVersionUID = 0;

    HashBasedTable( Map<R, Map<C, V>> backingMap, Factory<C, V> factory ) {
        super(backingMap, factory);
    }

    /**
     * Creates an empty {@code HashBasedTable}.
     */
    public static <R, C, V> HashBasedTable<R, C, V> create() {
        return new HashBasedTable<>(new LinkedHashMap<R, Map<C, V>>(), new Factory<C, V>(0));
    }

    /**
     * Creates an empty {@code HashBasedTable} with the specified map sizes.
     *
     * @param expectedRows        the expected number of distinct row keys
     * @param expectedCellsPerRow the expected number of column key / value mappings in each row
     * @throws IllegalArgumentException if {@code expectedRows} or {@code expectedCellsPerRow} is
     *                                  negative
     */
    public static <R, C, V> HashBasedTable<R, C, V> create(
            int expectedRows, int expectedCellsPerRow ) {
        checkNonnegative(expectedCellsPerRow, "expectedCellsPerRow");
        Map<R, Map<C, V>> backingMap = MapUtil.newHashMap(expectedRows, true);
        return new HashBasedTable<>(backingMap, new Factory<C, V>(expectedCellsPerRow));
    }

    /**
     * Creates a {@code HashBasedTable} with the same mappings as the specified table.
     *
     * @param table the table to copy
     * @throws NullPointerException if any of the row keys, column keys, or values in {@code table} is
     *                              null
     */
    public static <R, C, V> HashBasedTable<R, C, V> create(
            Table<? extends R, ? extends C, ? extends V> table ) {
        HashBasedTable<R, C, V> result = create();
        result.putAll(table);
        return result;
    }

    private static class Factory<C, V> implements Supplier<Map<C, V>>, Serializable {
        private static final long serialVersionUID = 0;
        final int expectedSize;

        Factory( int expectedSize ) {
            this.expectedSize = expectedSize;
        }

        @Override
        public Map<C, V> get() {
            return MapUtil.newHashMap(expectedSize, true);
        }
    }
}
