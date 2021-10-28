

package com.whaleal.icefrog.collections;


import junit.framework.TestCase;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Tests for {@link ForwardingMultiset}.
 *
 * @author Hayward Chan
 * 
 */
public class ForwardingMultisetTest extends TestCase {

  static final class StandardImplForwardingMultiset<T> extends ForwardingMultiset<T> {
    private final Multiset<T> backingCollection;

    StandardImplForwardingMultiset(Multiset<T> backingMultiset) {
      this.backingCollection = backingMultiset;
    }

    @Override
    protected Multiset<T> delegate() {
      return backingCollection;
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
      return standardAddAll(collection);
    }

    @Override
    public boolean add(T element) {
      return standardAdd(element);
    }

    @Override
    public void clear() {
      standardClear();
    }

    @Override
    public int count(Object element) {
      return standardCount(element);
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
    public boolean equals(Object object) {
      return standardEquals(object);
    }

    @Override
    public int hashCode() {
      return standardHashCode();
    }

    @Override
    public boolean setCount(T element, int oldCount, int newCount) {
      return standardSetCount(element, oldCount, newCount);
    }

    @Override
    public int setCount(T element, int count) {
      return standardSetCount(element, count);
    }

    @Override
    public Set<T> elementSet() {
      return new StandardElementSet();
    }

    @Override
    public Iterator<T> iterator() {
      return standardIterator();
    }

    @Override
    public boolean isEmpty() {
      return standardIsEmpty();
    }

    @Override
    public int size() {
      return standardSize();
    }
  }


  private static <T> Multiset<T> wrap(final Multiset<T> delegate) {
    return new ForwardingMultiset<T>() {
      @Override
      protected Multiset<T> delegate() {
        return delegate;
      }
    };
  }
}
