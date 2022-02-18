package com.whaleal.icefrog.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;


/**
 * Collectors utilities for {@code common.collect.Table} internals.
 */


final class TableCollectors {

    private TableCollectors() {
    }

    static <T extends Object, R, C, V>
    Collector<T, ?, ImmutableTable<R, C, V>> toImmutableTable(
            Function<? super T, ? extends R> rowFunction,
            Function<? super T, ? extends C> columnFunction,
            Function<? super T, ? extends V> valueFunction ) {
        checkNotNull(rowFunction, "rowFunction");
        checkNotNull(columnFunction, "columnFunction");
        checkNotNull(valueFunction, "valueFunction");
        return Collector.of(
                (Supplier<ImmutableTable.Builder<R, C, V>>) ImmutableTable.Builder::new,
                ( builder, t ) ->
                        builder.put(rowFunction.apply(t), columnFunction.apply(t), valueFunction.apply(t)),
                ImmutableTable.Builder::combine,
                ImmutableTable.Builder::build);
    }

    static <T extends Object, R, C, V>
    Collector<T, ?, ImmutableTable<R, C, V>> toImmutableTable(
            Function<? super T, ? extends R> rowFunction,
            Function<? super T, ? extends C> columnFunction,
            Function<? super T, ? extends V> valueFunction,
            BinaryOperator<V> mergeFunction ) {

        checkNotNull(rowFunction, "rowFunction");
        checkNotNull(columnFunction, "columnFunction");
        checkNotNull(valueFunction, "valueFunction");
        checkNotNull(mergeFunction, "mergeFunction");

        /*
         * No mutable Table exactly matches the insertion order behavior of ImmutableTable.Builder, but
         * the Builder can't efficiently support merging of duplicate values.  Getting around this
         * requires some work.
         */

        return Collector.of(
                ImmutableTableCollectorState<R, C, V>::new,
                ( state, input ) ->
                        state.put(
                                rowFunction.apply(input),
                                columnFunction.apply(input),
                                valueFunction.apply(input),
                                mergeFunction),
                ( s1, s2 ) -> s1.combine(s2, mergeFunction),
                state -> state.toTable());
    }

    static <
            T extends Object,
            R extends Object,
            C extends Object,
            V extends Object,
            I extends Table<R, C, V>>
    Collector<T, ?, I> toTable(
            Function<? super T, ? extends R> rowFunction,
            Function<? super T, ? extends C> columnFunction,
            Function<? super T, ? extends V> valueFunction,
            Supplier<I> tableSupplier ) {
        return toTable(
                rowFunction,
                columnFunction,
                valueFunction,
                ( v1, v2 ) -> {
                    throw new IllegalStateException("Conflicting values " + v1 + " and " + v2);
                },
                tableSupplier);
    }

    static <
            T extends Object,
            R extends Object,
            C extends Object,
            V extends Object,
            I extends Table<R, C, V>>
    Collector<T, ?, I> toTable(
            Function<? super T, ? extends R> rowFunction,
            Function<? super T, ? extends C> columnFunction,
            Function<? super T, ? extends V> valueFunction,
            BinaryOperator<V> mergeFunction,
            Supplier<I> tableSupplier ) {
        checkNotNull(rowFunction);
        checkNotNull(columnFunction);
        checkNotNull(valueFunction);
        checkNotNull(mergeFunction);
        checkNotNull(tableSupplier);
        return Collector.of(
                tableSupplier,
                ( table, input ) ->
                        mergeTables(
                                table,
                                rowFunction.apply(input),
                                columnFunction.apply(input),
                                valueFunction.apply(input),
                                mergeFunction),
                ( table1, table2 ) -> {
                    for (Table.Cell<R, C, V> cell2 : table2.cellSet()) {
                        mergeTables(
                                table1, cell2.getRowKey(), cell2.getColumnKey(), cell2.getValue(), mergeFunction);
                    }
                    return table1;
                });
    }

    private static <
            R extends Object, C extends Object, V extends Object>
    void mergeTables(
            Table<R, C, V> table,
            @ParametricNullness R row,
            @ParametricNullness C column,
            @ParametricNullness V value,
            BinaryOperator<V> mergeFunction ) {
        checkNotNull(value);
        V oldValue = table.get(row, column);
        if (oldValue == null) {
            table.put(row, column, value);
        } else {
            V newValue = mergeFunction.apply(oldValue, value);
            if (newValue == null) {
                table.remove(row, column);
            } else {
                table.put(row, column, newValue);
            }
        }
    }

    private static final class ImmutableTableCollectorState<R, C, V> {
        final List<MutableCell<R, C, V>> insertionOrder = new ArrayList<>();
        final Table<R, C, MutableCell<R, C, V>> table = HashBasedTable.create();

        void put( R row, C column, V value, BinaryOperator<V> merger ) {
            MutableCell<R, C, V> oldCell = table.get(row, column);
            if (oldCell == null) {
                MutableCell<R, C, V> cell = new MutableCell<>(row, column, value);
                insertionOrder.add(cell);
                table.put(row, column, cell);
            } else {
                oldCell.merge(value, merger);
            }
        }

        ImmutableTableCollectorState<R, C, V> combine(
                ImmutableTableCollectorState<R, C, V> other, BinaryOperator<V> merger ) {
            for (MutableCell<R, C, V> cell : other.insertionOrder) {
                put(cell.getRowKey(), cell.getColumnKey(), cell.getValue(), merger);
            }
            return this;
        }

        ImmutableTable<R, C, V> toTable() {
            return ImmutableTable.copyOf(insertionOrder);
        }
    }

    private static final class MutableCell<R, C, V> extends Tables.AbstractCell<R, C, V> {
        private final R row;
        private final C column;
        private V value;

        MutableCell( R row, C column, V value ) {
            this.row = checkNotNull(row, "row");
            this.column = checkNotNull(column, "column");
            this.value = checkNotNull(value, "value");
        }

        @Override
        public R getRowKey() {
            return row;
        }

        @Override
        public C getColumnKey() {
            return column;
        }

        @Override
        public V getValue() {
            return value;
        }

        void merge( V value, BinaryOperator<V> mergeFunction ) {
            checkNotNull(value, "value");
            this.value = checkNotNull(mergeFunction.apply(this.value, value), "mergeFunction.apply");
        }
    }
}
