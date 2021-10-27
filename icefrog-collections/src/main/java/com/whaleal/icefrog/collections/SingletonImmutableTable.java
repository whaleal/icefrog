

package com.whaleal.icefrog.collections;

import java.util.Map;

import static com.whaleal.icefrog.core.lang.Preconditions.checkNotNull;

/**
 * An implementation of {@link ImmutableTable} that holds a single cell.
 *
 *
 */


class SingletonImmutableTable<R, C, V> extends ImmutableTable<R, C, V> {
  final R singleRowKey;
  final C singleColumnKey;
  final V singleValue;

  SingletonImmutableTable(R rowKey, C columnKey, V value) {
    this.singleRowKey = checkNotNull(rowKey);
    this.singleColumnKey = checkNotNull(columnKey);
    this.singleValue = checkNotNull(value);
  }

  SingletonImmutableTable(Cell<R, C, V> cell) {
    this(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
  }

  @Override
  public ImmutableMap<R, V> column(C columnKey) {
    checkNotNull(columnKey);
    return containsColumn(columnKey)
        ? ImmutableMap.of(singleRowKey, singleValue)
        : ImmutableMap.of();
  }

  @Override
  public ImmutableMap<C, Map<R, V>> columnMap() {
    return ImmutableMap.of(singleColumnKey, ImmutableMap.of(singleRowKey, singleValue));
  }

  @Override
  public ImmutableMap<R, Map<C, V>> rowMap() {
    return ImmutableMap.of(singleRowKey, ImmutableMap.of(singleColumnKey, singleValue));
  }

  @Override
  public int size() {
    return 1;
  }

  @Override
  ImmutableSet<Cell<R, C, V>> createCellSet() {
    return ImmutableSet.of(cellOf(singleRowKey, singleColumnKey, singleValue));
  }

  @Override
  ImmutableCollection<V> createValues() {
    return ImmutableSet.of(singleValue);
  }

  @Override
  SerializedForm createSerializedForm() {
    return SerializedForm.create(this, new int[] {0}, new int[] {0});
  }
}
