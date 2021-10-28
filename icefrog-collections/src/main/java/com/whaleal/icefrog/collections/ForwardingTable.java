

package com.whaleal.icefrog.collections;



import javax.annotation.CheckForNull;
import java.util.Collection;
import java.util.Map;
import java.util.Set;


/**
 * A table which forwards all its method calls to another table. Subclasses should override one or
 * more methods to modify the behavior of the backing map as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 *
 *
 * 
 */


public abstract class ForwardingTable<
        R extends Object, C extends Object, V extends Object>
    extends ForwardingObject implements Table<R, C, V> {
  /** Constructor for use by subclasses. */
  protected ForwardingTable() {}

  @Override
  protected abstract Table<R, C, V> delegate();

  @Override
  public Set<Cell<R, C, V>> cellSet() {
    return delegate().cellSet();
  }

  @Override
  public void clear() {
    delegate().clear();
  }

  @Override
  public Map<R, V> column(@ParametricNullness C columnKey) {
    return delegate().column(columnKey);
  }

  @Override
  public Set<C> columnKeySet() {
    return delegate().columnKeySet();
  }

  @Override
  public Map<C, Map<R, V>> columnMap() {
    return delegate().columnMap();
  }

  @Override
  public boolean contains(@CheckForNull Object rowKey, @CheckForNull Object columnKey) {
    return delegate().contains(rowKey, columnKey);
  }

  @Override
  public boolean containsColumn(@CheckForNull Object columnKey) {
    return delegate().containsColumn(columnKey);
  }

  @Override
  public boolean containsRow(@CheckForNull Object rowKey) {
    return delegate().containsRow(rowKey);
  }

  @Override
  public boolean containsValue(@CheckForNull Object value) {
    return delegate().containsValue(value);
  }

  @Override
  @CheckForNull
  public V get(@CheckForNull Object rowKey, @CheckForNull Object columnKey) {
    return delegate().get(rowKey, columnKey);
  }

  @Override
  public boolean isEmpty() {
    return delegate().isEmpty();
  }

  
  @Override
  @CheckForNull
  public V put(
      @ParametricNullness R rowKey, @ParametricNullness C columnKey, @ParametricNullness V value) {
    return delegate().put(rowKey, columnKey, value);
  }

  @Override
  public void putAll(Table<? extends R, ? extends C, ? extends V> table) {
    delegate().putAll(table);
  }

  
  @Override
  @CheckForNull
  public V remove(@CheckForNull Object rowKey, @CheckForNull Object columnKey) {
    return delegate().remove(rowKey, columnKey);
  }

  @Override
  public Map<C, V> row(@ParametricNullness R rowKey) {
    return delegate().row(rowKey);
  }

  @Override
  public Set<R> rowKeySet() {
    return delegate().rowKeySet();
  }

  @Override
  public Map<R, Map<C, V>> rowMap() {
    return delegate().rowMap();
  }

  @Override
  public int size() {
    return delegate().size();
  }

  @Override
  public Collection<V> values() {
    return delegate().values();
  }

  @Override
  public boolean equals(@CheckForNull Object obj) {
    return (obj == this) || delegate().equals(obj);
  }

  @Override
  public int hashCode() {
    return delegate().hashCode();
  }
}
