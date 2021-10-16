

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.Objects.requireNonNull;

/** A {@code RegularImmutableTable} optimized for sparse data. */



final class SparseImmutableTable<R, C, V> extends RegularImmutableTable<R, C, V> {
  static final ImmutableTable<Object, Object, Object> EMPTY =
      new SparseImmutableTable<>(
          ImmutableList.of(), ImmutableSet.of(), ImmutableSet.of());

  private final ImmutableMap<R, ImmutableMap<C, V>> rowMap;
  private final ImmutableMap<C, ImmutableMap<R, V>> columnMap;

  // For each cell in iteration order, the index of that cell's row key in the row key list.
  @SuppressWarnings("Immutable") // We don't modify this after construction.
  private final int[] cellRowIndices;

  // For each cell in iteration order, the index of that cell's column key in the list of column
  // keys present in that row.
  @SuppressWarnings("Immutable") // We don't modify this after construction.
  private final int[] cellColumnInRowIndices;

  SparseImmutableTable(
      ImmutableList<Cell<R, C, V>> cellList,
      ImmutableSet<R> rowSpace,
      ImmutableSet<C> columnSpace) {
    Map<R, Integer> rowIndex = ImmutableMap.indexMap(rowSpace);
    Map<R, Map<C, V>> rows = MapUtil.newHashMap(true);
    for (R row : rowSpace) {
      rows.put(row, new LinkedHashMap<C, V>());
    }
    Map<C, Map<R, V>> columns = MapUtil.newHashMap(true);
    for (C col : columnSpace) {
      columns.put(col, new LinkedHashMap<R, V>());
    }
    int[] cellRowIndices = new int[cellList.size()];
    int[] cellColumnInRowIndices = new int[cellList.size()];
    for (int i = 0; i < cellList.size(); i++) {
      Cell<R, C, V> cell = cellList.get(i);
      R rowKey = cell.getRowKey();
      C columnKey = cell.getColumnKey();
      V value = cell.getValue();

      /*
       * These requireNonNull calls are safe because we construct the MapUtil to hold all the provided
       * cells.
       */
      cellRowIndices[i] = requireNonNull(rowIndex.get(rowKey));
      Map<C, V> thisRow = requireNonNull(rows.get(rowKey));
      cellColumnInRowIndices[i] = thisRow.size();
      V oldValue = thisRow.put(columnKey, value);
      checkNoDuplicate(rowKey, columnKey, oldValue, value);
      requireNonNull(columns.get(columnKey)).put(rowKey, value);
    }
    this.cellRowIndices = cellRowIndices;
    this.cellColumnInRowIndices = cellColumnInRowIndices;
    ImmutableMap.Builder<R, ImmutableMap<C, V>> rowBuilder =
        new ImmutableMap.Builder<>(rows.size());
    for (Entry<R, Map<C, V>> row : rows.entrySet()) {
      rowBuilder.put(row.getKey(), ImmutableMap.copyOf(row.getValue()));
    }
    this.rowMap = rowBuilder.build();

    ImmutableMap.Builder<C, ImmutableMap<R, V>> columnBuilder =
        new ImmutableMap.Builder<>(columns.size());
    for (Entry<C, Map<R, V>> col : columns.entrySet()) {
      columnBuilder.put(col.getKey(), ImmutableMap.copyOf(col.getValue()));
    }
    this.columnMap = columnBuilder.build();
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
  public int size() {
    return cellRowIndices.length;
  }

  @Override
  Cell<R, C, V> getCell(int index) {
    int rowIndex = cellRowIndices[index];
    Entry<R, ImmutableMap<C, V>> rowEntry = rowMap.entrySet().asList().get(rowIndex);
    ImmutableMap<C, V> row = rowEntry.getValue();
    int columnIndex = cellColumnInRowIndices[index];
    Entry<C, V> colEntry = row.entrySet().asList().get(columnIndex);
    return cellOf(rowEntry.getKey(), colEntry.getKey(), colEntry.getValue());
  }

  @Override
  V getValue(int index) {
    int rowIndex = cellRowIndices[index];
    ImmutableMap<C, V> row = rowMap.values().asList().get(rowIndex);
    int columnIndex = cellColumnInRowIndices[index];
    return row.values().asList().get(columnIndex);
  }

  @Override
  ImmutableTable.SerializedForm createSerializedForm() {
    Map<C, Integer> columnKeyToIndex = ImmutableMap.indexMap(columnKeySet());
    int[] cellColumnIndices = new int[cellSet().size()];
    int i = 0;
    for (Cell<R, C, V> cell : cellSet()) {
      // requireNonNull is safe because the cell exists in the table.
      cellColumnIndices[i++] = requireNonNull(columnKeyToIndex.get(cell.getColumnKey()));
    }
    return ImmutableTable.SerializedForm.create(this, cellRowIndices, cellColumnIndices);
  }
}
