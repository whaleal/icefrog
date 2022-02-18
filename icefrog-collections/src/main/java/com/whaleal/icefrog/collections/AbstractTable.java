package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.collection.SpliteratorUtil;
import com.whaleal.icefrog.core.map.MapUtil;

import javax.annotation.CheckForNull;
import java.util.*;


/**
 * Skeletal, implementation-agnostic implementation of the {@link Table} interface.
 */


abstract class AbstractTable<
        R extends Object, C extends Object, V extends Object>
        implements Table<R, C, V> {

    @CheckForNull
    private transient Set<Cell<R, C, V>> cellSet;
    @CheckForNull
    private transient Collection<V> values;

    @Override
    public boolean containsRow( @CheckForNull Object rowKey ) {
        return MapUtil.safeContainsKey(rowMap(), rowKey);
    }

    @Override
    public boolean containsColumn( @CheckForNull Object columnKey ) {
        return MapUtil.safeContainsKey(columnMap(), columnKey);
    }

    @Override
    public Set<R> rowKeySet() {
        return rowMap().keySet();
    }

    @Override
    public Set<C> columnKeySet() {
        return columnMap().keySet();
    }

    @Override
    public boolean containsValue( @CheckForNull Object value ) {
        for (Map<C, V> row : rowMap().values()) {
            if (row.containsValue(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean contains( @CheckForNull Object rowKey, @CheckForNull Object columnKey ) {
        Map<C, V> row = MapUtil.safeGet(rowMap(), rowKey);
        return row != null && MapUtil.safeContainsKey(row, columnKey);
    }

    @Override
    @CheckForNull
    public V get( @CheckForNull Object rowKey, @CheckForNull Object columnKey ) {
        Map<C, V> row = MapUtil.safeGet(rowMap(), rowKey);
        return (row == null) ? null : MapUtil.safeGet(row, columnKey);
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public void clear() {
        Iterators.clear(cellSet().iterator());
    }

    @Override
    @CheckForNull
    public V remove( @CheckForNull Object rowKey, @CheckForNull Object columnKey ) {
        Map<C, V> row = MapUtil.safeGet(rowMap(), rowKey);
        return (row == null) ? null : MapUtil.safeRemove(row, columnKey);
    }

    @Override
    @CheckForNull
    public V put(
            @ParametricNullness R rowKey, @ParametricNullness C columnKey, @ParametricNullness V value ) {
        return row(rowKey).put(columnKey, value);
    }

    @Override
    public void putAll( Table<? extends R, ? extends C, ? extends V> table ) {
        for (Cell<? extends R, ? extends C, ? extends V> cell : table.cellSet()) {
            put(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
        }
    }

    @Override
    public Set<Cell<R, C, V>> cellSet() {
        Set<Cell<R, C, V>> result = cellSet;
        return (result == null) ? cellSet = createCellSet() : result;
    }

    Set<Cell<R, C, V>> createCellSet() {
        return new CellSet();
    }

    abstract Iterator<Cell<R, C, V>> cellIterator();

    abstract Spliterator<Cell<R, C, V>> cellSpliterator();

    @Override
    public Collection<V> values() {
        Collection<V> result = values;
        return (result == null) ? values = createValues() : result;
    }

    Collection<V> createValues() {
        return new Values();
    }

    Iterator<V> valuesIterator() {
        return new com.whaleal.icefrog.core.collection.TransIter<Cell<R, C, V>, V>(cellSet().iterator(),x ->x .getValue());

    }

    Spliterator<V> valuesSpliterator() {
        return SpliteratorUtil.map(cellSpliterator(), Cell::getValue);
    }

    @Override
    public boolean equals( @CheckForNull Object obj ) {
        return Tables.equalsImpl(this, obj);
    }

    @Override
    public int hashCode() {
        return cellSet().hashCode();
    }

    /**
     * Returns the string representation {@code rowMap().toString()}.
     */
    @Override
    public String toString() {
        return rowMap().toString();
    }

    class CellSet extends AbstractSet<Cell<R, C, V>> {
        @Override
        public boolean contains( @CheckForNull Object o ) {
            if (o instanceof Cell) {
                Cell<?, ?, ?> cell = (Cell<?, ?, ?>) o;
                Map<C, V> row = MapUtil.safeGet(rowMap(), cell.getRowKey());
                return row != null
                        && Collections2.safeContains(
                        row.entrySet(), new ImmutableEntry(cell.getColumnKey(), cell.getValue()));

            }
            return false;
        }

        @Override
        public boolean remove( @CheckForNull Object o ) {
            if (o instanceof Cell) {
                Cell<?, ?, ?> cell = (Cell<?, ?, ?>) o;
                Map<C, V> row = MapUtil.safeGet(rowMap(), cell.getRowKey());
                return row != null
                        && Collections2.safeRemove(
                        row.entrySet(), new ImmutableEntry(cell.getColumnKey(), cell.getValue()));
            }
            return false;
        }

        @Override
        public void clear() {
            AbstractTable.this.clear();
        }

        @Override
        public Iterator<Cell<R, C, V>> iterator() {
            return cellIterator();
        }

        @Override
        public Spliterator<Cell<R, C, V>> spliterator() {
            return cellSpliterator();
        }

        @Override
        public int size() {
            return AbstractTable.this.size();
        }
    }

    class Values extends AbstractCollection<V> {
        @Override
        public Iterator<V> iterator() {
            return valuesIterator();
        }

        @Override
        public Spliterator<V> spliterator() {
            return valuesSpliterator();
        }

        @Override
        public boolean contains( @CheckForNull Object o ) {
            return containsValue(o);
        }

        @Override
        public void clear() {
            AbstractTable.this.clear();
        }

        @Override
        public int size() {
            return AbstractTable.this.size();
        }
    }
}
