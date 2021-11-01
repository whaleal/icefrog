

package com.whaleal.icefrog.collections;


import junit.framework.TestCase;
import org.junit.Test;

import java.util.Collection;
import java.util.NavigableSet;
import java.util.SortedSet;

/**
 * Tests for {@code ForwardingNavigableSet}.
 *
 * 
 */
public class ForwardingNavigableSetTest extends TestCase {

  @Test
  public void test(){

  }

  static class StandardImplForwardingNavigableSet<T> extends ForwardingNavigableSet<T> {
    private final NavigableSet<T> backingSet;

    StandardImplForwardingNavigableSet(NavigableSet<T> backingSet) {
      this.backingSet = backingSet;
    }

    @Override
    protected NavigableSet<T> delegate() {
      return backingSet;
    }

    @Override
    public boolean equals(Object object) {
      return standardEquals(object);
    }

    @Override
    public int hashCode() {
      return standardHashCode();
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
      return standardAddAll(collection);
    }

    @Override
    public void clear() {
      standardClear();
    }

    @Override
    public boolean contains(Object object) {
      return standardContains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
      return standardContainsAll(collection);
    }

    @Override
    public boolean remove(Object object) {
      return standardRemove(object);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
      return standardRemoveAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
      return standardRetainAll(collection);
    }

    @Override
    public Object[] toArray() {
      return standardToArray();
    }

    @Override
    public <T> T[] toArray(T[] array) {
      return standardToArray(array);
    }

    @Override
    public String toString() {
      return standardToString();
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
      return standardSubSet(fromElement, toElement);
    }

    @Override
    public T lower(T e) {
      return standardLower(e);
    }

    @Override
    public T floor(T e) {
      return standardFloor(e);
    }

    @Override
    public T ceiling(T e) {
      return standardCeiling(e);
    }

    @Override
    public T higher(T e) {
      return standardHigher(e);
    }

    @Override
    public T pollFirst() {
      return standardPollFirst();
    }

    @Override
    public T pollLast() {
      return standardPollLast();
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
      return standardHeadSet(toElement);
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
      return standardTailSet(fromElement);
    }
  }

  private static <T> NavigableSet<T> wrap(final NavigableSet<T> delegate) {
    return new ForwardingNavigableSet<T>() {
      @Override
      protected NavigableSet<T> delegate() {
        return delegate;
      }
    };
  }
}
