package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.collection.AbstractIterator;

import javax.annotation.CheckForNull;
import java.util.Iterator;
import java.util.Map;

import static java.util.Objects.requireNonNull;


/**
 * A {@code RegularImmutableTable} optimized for dense data.
 */


final class DenseImmutableTable<R, C, V> extends RegularImmutableTable<R, C, V> {
    private final ImmutableMap<R, Integer> rowKeyToIndex;
    private final ImmutableMap<C, Integer> columnKeyToIndex;
    private final ImmutableMap<R, ImmutableMap<C, V>> rowMap;
    private final ImmutableMap<C, ImmutableMap<R, V>> columnMap;

    @SuppressWarnings("Immutable") // We don't modify this after construction.
    private final int[] rowCounts;

    @SuppressWarnings("Immutable") // We don't modify this after construction.
    private final int[] columnCounts;

    @SuppressWarnings("Immutable") // We don't modify this after construction.
    private final V[][] values;

    // For each cell in iteration order, the index of that cell's row key in the row key list.
    @SuppressWarnings("Immutable") // We don't modify this after construction.
    private final int[] cellRowIndices;

    // For each cell in iteration order, the index of that cell's column key in the column key list.
    @SuppressWarnings("Immutable") // We don't modify this after construction.
    private final int[] cellColumnIndices;

    DenseImmutableTable(
            ImmutableList<Cell<R, C, V>> cellList,
            ImmutableSet<R> rowSpace,
            ImmutableSet<C> columnSpace ) {
        @SuppressWarnings("unchecked")

        V[][] array = (V[][]) new Object[rowSpace.size()][columnSpace.size()];
        this.values = array;
        this.rowKeyToIndex = ImmutableMap.indexMap(rowSpace);
        this.columnKeyToIndex = ImmutableMap.indexMap(columnSpace);
        rowCounts = new int[rowKeyToIndex.size()];
        columnCounts = new int[columnKeyToIndex.size()];
        int[] cellRowIndices = new int[cellList.size()];
        int[] cellColumnIndices = new int[cellList.size()];
        for (int i = 0; i < cellList.size(); i++) {
            Cell<R, C, V> cell = cellList.get(i);
            R rowKey = cell.getRowKey();
            C columnKey = cell.getColumnKey();
            // The requireNonNull calls are safe because we construct the indexes with indexMap.
            int rowIndex = requireNonNull(rowKeyToIndex.get(rowKey));
            int columnIndex = requireNonNull(columnKeyToIndex.get(columnKey));
            V existingValue = values[rowIndex][columnIndex];
            checkNoDuplicate(rowKey, columnKey, existingValue, cell.getValue());
            values[rowIndex][columnIndex] = cell.getValue();
            rowCounts[rowIndex]++;
            columnCounts[columnIndex]++;
            cellRowIndices[i] = rowIndex;
            cellColumnIndices[i] = columnIndex;
        }
        this.cellRowIndices = cellRowIndices;
        this.cellColumnIndices = cellColumnIndices;
        this.rowMap = new RowMap();
        this.columnMap = new ColumnMap();
    }

    @Override
    public ImmutableMap<C, Map<R, V>> columnMap() {
        // Casts without copying.
        ImmutableMap<C, ImmutableMap<R, V>> columnMap = this.columnMap;
        return ImmutableMap.copyOf(columnMap);
    }

    @Override
    public ImmutableMap<R, Map<C, V>> rowMap() {
        // Casts without copying.
        ImmutableMap<R, ImmutableMap<C, V>> rowMap = this.rowMap;
        return ImmutableMap.copyOf(rowMap);
    }

    @Override
    @CheckForNull
    public V get( @CheckForNull Object rowKey, @CheckForNull Object columnKey ) {
        Integer rowIndex = rowKeyToIndex.get(rowKey);
        Integer columnIndex = columnKeyToIndex.get(columnKey);
        return ((rowIndex == null) || (columnIndex == null)) ? null : values[rowIndex][columnIndex];
    }

    @Override
    public int size() {
        return cellRowIndices.length;
    }

    @Override
    Cell<R, C, V> getCell( int index ) {
        int rowIndex = cellRowIndices[index];
        int columnIndex = cellColumnIndices[index];
        R rowKey = rowKeySet().asList().get(rowIndex);
        C columnKey = columnKeySet().asList().get(columnIndex);
        // requireNonNull is safe because we use indexes that were populated by the constructor.
        V value = requireNonNull(values[rowIndex][columnIndex]);
        return cellOf(rowKey, columnKey, value);
    }

    @Override
    V getValue( int index ) {
        // requireNonNull is safe because we use indexes that were populated by the constructor.
        return requireNonNull(values[cellRowIndices[index]][cellColumnIndices[index]]);
    }

    @Override
    ImmutableTable.SerializedForm createSerializedForm() {
        return ImmutableTable.SerializedForm.create(this, cellRowIndices, cellColumnIndices);
    }

    /**
     * An immutable map implementation backed by an indexed nullable array.
     */
    private abstract static class ImmutableArrayMap<K, V> extends ImmutableMap.IteratorBasedImmutableMap<K, V> {
        private final int size;

        ImmutableArrayMap( int size ) {
            this.size = size;
        }

        abstract ImmutableMap<K, Integer> keyToIndex();

        // True if getValue never returns null.
        private boolean isFull() {
            return size == keyToIndex().size();
        }

        K getKey( int index ) {
            return keyToIndex().keySet().asList().get(index);
        }

        @CheckForNull
        abstract V getValue( int keyIndex );

        @Override
        ImmutableSet<K> createKeySet() {
            return isFull() ? keyToIndex().keySet() : super.createKeySet();
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        @CheckForNull
        public V get( @CheckForNull Object key ) {
            Integer keyIndex = keyToIndex().get(key);
            return (keyIndex == null) ? null : getValue(keyIndex);
        }

        @Override
        Iterator<Entry<K, V>> entryIterator() {
            return new AbstractIterator<Entry<K, V>>() {
                private final int maxIndex = keyToIndex().size();
                private int index = -1;

                @Override
                @CheckForNull
                protected Map.Entry<K, V> computeNext() {
                    for (index++; index < maxIndex; index++) {
                        V value = getValue(index);
                        if (value != null) {
                            return new ImmutableEntry(getKey(index), value);
                        }
                    }
                    return endOfData();
                }
            };
        }
    }

    private final class Row extends ImmutableArrayMap<C, V> {
        private final int rowIndex;

        Row( int rowIndex ) {
            super(rowCounts[rowIndex]);
            this.rowIndex = rowIndex;
        }

        @Override
        ImmutableMap<C, Integer> keyToIndex() {
            return columnKeyToIndex;
        }

        @Override
        @CheckForNull
        V getValue( int keyIndex ) {
            return values[rowIndex][keyIndex];
        }

        @Override
        boolean isPartialView() {
            return true;
        }
    }

    private final class Column extends ImmutableArrayMap<R, V> {
        private final int columnIndex;

        Column( int columnIndex ) {
            super(columnCounts[columnIndex]);
            this.columnIndex = columnIndex;
        }

        @Override
        ImmutableMap<R, Integer> keyToIndex() {
            return rowKeyToIndex;
        }

        @Override
        @CheckForNull
        V getValue( int keyIndex ) {
            return values[keyIndex][columnIndex];
        }

        @Override
        boolean isPartialView() {
            return true;
        }
    }

    private final class RowMap extends ImmutableArrayMap<R, ImmutableMap<C, V>> {
        private RowMap() {
            super(rowCounts.length);
        }

        @Override
        ImmutableMap<R, Integer> keyToIndex() {
            return rowKeyToIndex;
        }

        @Override
        ImmutableMap<C, V> getValue( int keyIndex ) {
            return new Row(keyIndex);
        }

        @Override
        boolean isPartialView() {
            return false;
        }
    }

    private final class ColumnMap extends ImmutableArrayMap<C, ImmutableMap<R, V>> {
        private ColumnMap() {
            super(columnCounts.length);
        }

        @Override
        ImmutableMap<C, Integer> keyToIndex() {
            return columnKeyToIndex;
        }

        @Override
        ImmutableMap<R, V> getValue( int keyIndex ) {
            return new Column(keyIndex);
        }

        @Override
        boolean isPartialView() {
            return false;
        }
    }
}
