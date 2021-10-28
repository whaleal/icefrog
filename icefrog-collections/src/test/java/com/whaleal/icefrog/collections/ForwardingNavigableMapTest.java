

package com.whaleal.icefrog.collections;


import junit.framework.TestCase;

import java.util.*;

/**
 * Tests for {@code ForwardingNavigableMap}.
 *
 * @author Robert Konigsberg
 * 
 */
public class ForwardingNavigableMapTest extends TestCase {
  static class StandardImplForwardingNavigableMap<K, V> extends ForwardingNavigableMap<K, V> {
    private final NavigableMap<K, V> backingMap;

    StandardImplForwardingNavigableMap(NavigableMap<K, V> backingMap) {
      this.backingMap = backingMap;
    }

    @Override
    protected NavigableMap<K, V> delegate() {
      return backingMap;
    }

    @Override
    public boolean containsKey(Object key) {
      return standardContainsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
      return standardContainsValue(value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
      standardPutAll(map);
    }

    @Override
    public V remove(Object object) {
      return standardRemove(object);
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
    public Set<K> keySet() {
      /*
       * We can't use StandardKeySet, as NavigableMapTestSuiteBuilder assumes that our keySet is a
       * NavigableSet. We test StandardKeySet in the superclass, so it's still covered.
       */
      return navigableKeySet();
    }

    @Override
    public Collection<V> values() {
      return new StandardValues();
    }

    @Override
    public String toString() {
      return standardToString();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
      return new StandardEntrySet() {
        @Override
        public Iterator<Entry<K, V>> iterator() {
          return backingMap.entrySet().iterator();
        }
      };
    }

    @Override
    public void clear() {
      standardClear();
    }

    @Override
    public boolean isEmpty() {
      return standardIsEmpty();
    }

    @Override
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
      return standardSubMap(fromKey, toKey);
    }

    @Override
    public Entry<K, V> lowerEntry(K key) {
      return standardLowerEntry(key);
    }

    @Override
    public K lowerKey(K key) {
      return standardLowerKey(key);
    }

    @Override
    public Entry<K, V> floorEntry(K key) {
      return standardFloorEntry(key);
    }

    @Override
    public K floorKey(K key) {
      return standardFloorKey(key);
    }

    @Override
    public Entry<K, V> ceilingEntry(K key) {
      return standardCeilingEntry(key);
    }

    @Override
    public K ceilingKey(K key) {
      return standardCeilingKey(key);
    }

    @Override
    public Entry<K, V> higherEntry(K key) {
      return standardHigherEntry(key);
    }

    @Override
    public K higherKey(K key) {
      return standardHigherKey(key);
    }

    @Override
    public Entry<K, V> firstEntry() {
      return standardFirstEntry();
    }

    /*
     * We can't override lastEntry to delegate to standardLastEntry, as it would create an infinite
     * loop. Instead, we test standardLastEntry manually below.
     */

    @Override
    public Entry<K, V> pollFirstEntry() {
      return standardPollFirstEntry();
    }

    @Override
    public Entry<K, V> pollLastEntry() {
      return standardPollLastEntry();
    }

    @Override
    public NavigableMap<K, V> descendingMap() {
      return new StandardDescendingMap();
    }

    @Override
    public NavigableSet<K> navigableKeySet() {
      return new StandardNavigableKeySet();
    }

    @Override
    public NavigableSet<K> descendingKeySet() {
      return standardDescendingKeySet();
    }

    @Override
    public K firstKey() {
      return standardFirstKey();
    }

    @Override
    public SortedMap<K, V> headMap(K toKey) {
      return standardHeadMap(toKey);
    }

    @Override
    public K lastKey() {
      return standardLastKey();
    }

    @Override
    public SortedMap<K, V> tailMap(K fromKey) {
      return standardTailMap(fromKey);
    }
  }

  static class StandardLastEntryForwardingNavigableMap<K, V> extends ForwardingNavigableMap<K, V> {
    private final NavigableMap<K, V> backingMap;

    StandardLastEntryForwardingNavigableMap(NavigableMap<K, V> backingMap) {
      this.backingMap = backingMap;
    }

    @Override
    protected NavigableMap<K, V> delegate() {
      return backingMap;
    }

    @Override
    public Entry<K, V> lastEntry() {
      return standardLastEntry();
    }
  }

 

  private static <K, V> NavigableMap<K, V> wrap(final NavigableMap<K, V> delegate) {
    return new ForwardingNavigableMap<K, V>() {
      @Override
      protected NavigableMap<K, V> delegate() {
        return delegate;
      }
    };
  }
}
