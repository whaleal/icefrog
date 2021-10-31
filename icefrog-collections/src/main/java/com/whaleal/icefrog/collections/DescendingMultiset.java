

package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;


/**
 * A skeleton implementation of a descending multiset. Only needs {@code forwardMultiset()} and
 * {@code entryIterator()}.
 *
 * 
 */


abstract class DescendingMultiset<E extends Object> extends ForwardingMultiset<E>
    implements SortedMultiset<E> {
  abstract SortedMultiset<E> forwardMultiset();

  @CheckForNull private transient Comparator<? super E> comparator;

  @Override
  public Comparator<? super E> comparator() {
    Comparator<? super E> result = comparator;
    if (result == null) {
      return comparator = Ordering.from(forwardMultiset().comparator()).reverse();
    }
    return result;
  }

  @CheckForNull private transient NavigableSet<E> elementSet;

  @Override
  public NavigableSet<E> elementSet() {
    NavigableSet<E> result = elementSet;
    if (result == null) {
      return elementSet = new SortedMultisets.NavigableElementSet<>(this);
    }
    return result;
  }

  @Override
  @CheckForNull
  public Multiset.Entry<E> pollFirstEntry() {
    return forwardMultiset().pollLastEntry();
  }

  @Override
  @CheckForNull
  public Multiset.Entry<E> pollLastEntry() {
    return forwardMultiset().pollFirstEntry();
  }

  @Override
  public SortedMultiset<E> headMultiset(@ParametricNullness E toElement, BoundType boundType) {
    return forwardMultiset().tailMultiset(toElement, boundType).descendingMultiset();
  }

  @Override
  public SortedMultiset<E> subMultiset(
      @ParametricNullness E fromElement,
      BoundType fromBoundType,
      @ParametricNullness E toElement,
      BoundType toBoundType) {
    return forwardMultiset()
        .subMultiset(toElement, toBoundType, fromElement, fromBoundType)
        .descendingMultiset();
  }

  @Override
  public SortedMultiset<E> tailMultiset(@ParametricNullness E fromElement, BoundType boundType) {
    return forwardMultiset().headMultiset(fromElement, boundType).descendingMultiset();
  }

  @Override
  protected Multiset<E> delegate() {
    return forwardMultiset();
  }

  @Override
  public SortedMultiset<E> descendingMultiset() {
    return forwardMultiset();
  }

  @Override
  @CheckForNull
  public Multiset.Entry<E> firstEntry() {
    return forwardMultiset().lastEntry();
  }

  @Override
  @CheckForNull
  public Multiset.Entry<E> lastEntry() {
    return forwardMultiset().firstEntry();
  }

  abstract Iterator<Multiset.Entry<E>> entryIterator();

  @CheckForNull private transient Set<Multiset.Entry<E>> entrySet;

  @Override
  public Set<Multiset.Entry<E>> entrySet() {
    Set<Multiset.Entry<E>> result = entrySet;
    return (result == null) ? entrySet = createEntrySet() : result;
  }

  Set<Multiset.Entry<E>> createEntrySet() {

    class EntrySetImpl extends Multisets.EntrySet<E> {
      @Override
      Multiset<E> multiset() {
        return DescendingMultiset.this;
      }

      @Override
      public Iterator<Multiset.Entry<E>> iterator() {
        return entryIterator();
      }

      @Override
      public int size() {
        return forwardMultiset().entrySet().size();
      }
    }
    return new EntrySetImpl();
  }

  @Override
  public Iterator<E> iterator() {
    return Multisets.iteratorImpl(this);
  }

  @Override
  public Object[] toArray() {
    return standardToArray();
  }

  @Override
  @SuppressWarnings("nullness") // b/192354773 in our checker affects toArray declarations
  public <T extends Object> T[] toArray(T[] array) {
    return standardToArray(array);
  }

  @Override
  public String toString() {
    return entrySet().toString();
  }
}
