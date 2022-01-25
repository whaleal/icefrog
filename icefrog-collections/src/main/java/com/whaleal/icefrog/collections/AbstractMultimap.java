/*
 * Copyright (C) 2012 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.whaleal.icefrog.collections;




import com.whaleal.icefrog.core.collection.IterUtil;

import javax.annotation.CheckForNull;
import java.util.*;
import java.util.Map.Entry;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;


/**
 * A skeleton {@code Multimap} implementation, not necessarily in terms of a {@code Map}.
 *
 * @author Louis Wasserman
 */

@ElementTypesAreNonnullByDefault
abstract class AbstractMultimap<K extends  Object, V extends  Object>
    implements Multimap<K, V> {
  
  public boolean isEmpty() {
    return size() == 0;
  }

  
  public boolean containsValue(@CheckForNull Object value) {
    for (Collection<V> collection : asMap().values()) {
      if (collection.contains(value)) {
        return true;
      }
    }

    return false;
  }

  
  public boolean containsEntry(@CheckForNull Object key, @CheckForNull Object value) {
    Collection<V> collection = asMap().get(key);
    return collection != null && collection.contains(value);
  }

  
  
  public boolean remove(@CheckForNull Object key, @CheckForNull Object value) {
    Collection<V> collection = asMap().get(key);
    return collection != null && collection.remove(value);
  }

  
  
  public boolean put(@ParametricNullness K key, @ParametricNullness V value) {
    return get(key).add(value);
  }

  
  
  public boolean putAll(@ParametricNullness K key, Iterable<? extends V> values) {
    checkNotNull(values);
    // make sure we only call values.iterator() once
    // and we only call get(key) if values is nonempty
    if (values instanceof Collection) {
      Collection<? extends V> valueCollection = (Collection<? extends V>) values;
      return !valueCollection.isEmpty() && get(key).addAll(valueCollection);
    } else {
      Iterator<? extends V> valueItr = values.iterator();
      return valueItr.hasNext() && IterUtil.addAll(get(key), valueItr);
    }
  }

  
  
  public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
    boolean changed = false;
    for (Entry<? extends K, ? extends V> entry : multimap.entries()) {
      changed |= put(entry.getKey(), entry.getValue());
    }
    return changed;
  }

  
  
  public Collection<V> replaceValues(@ParametricNullness K key, Iterable<? extends V> values) {
    checkNotNull(values);
    Collection<V> result = removeAll(key);
    putAll(key, values);
    return result;
  }

   @CheckForNull private transient Collection<Entry<K, V>> entries;

  
  public Collection<Entry<K, V>> entries() {
    Collection<Entry<K, V>> result = entries;
    return (result == null) ? entries = createEntries() : result;
  }

  abstract Collection<Entry<K, V>> createEntries();


  class Entries extends AbsEntries<K, V> {
    
    Multimap<K, V> multimap() {
      return AbstractMultimap.this;
    }

    
    public Iterator<Entry<K, V>> iterator() {
      return entryIterator();
    }

    
    public Spliterator<Entry<K, V>> spliterator() {
      return entrySpliterator();
    }
  }


  class EntrySet extends Entries implements Set<Entry<K, V>> {
    
    public int hashCode() {
      return SetUtil.hashCodeImpl(this);
    }

    
    public boolean equals(@CheckForNull Object obj) {
      return SetUtil.equalsImpl(this, obj);
    }
  }

  abstract Iterator<Entry<K, V>> entryIterator();

  Spliterator<Entry<K, V>> entrySpliterator() {
    return Spliterators.spliterator(
        entryIterator(), size(), (this instanceof SetMultimap) ? Spliterator.DISTINCT : 0);
  }

   @CheckForNull private transient Set<K> keySet;

  
  public Set<K> keySet() {
    Set<K> result = keySet;
    return (result == null) ? keySet = createKeySet() : result;
  }

  abstract Set<K> createKeySet();

   @CheckForNull private transient Multiset<K> keys;

  
  public Multiset<K> keys() {
    Multiset<K> result = keys;
    return (result == null) ? keys = createKeys() : result;
  }

  abstract Multiset<K> createKeys();

   @CheckForNull private transient Collection<V> values;

  
  public Collection<V> values() {
    Collection<V> result = values;
    return (result == null) ? values = createValues() : result;
  }

  abstract Collection<V> createValues();


  class Values extends AbstractCollection<V> {
    
    public Iterator<V> iterator() {
      return valueIterator();
    }

    
    public Spliterator<V> spliterator() {
      return valueSpliterator();
    }

    
    public int size() {
      return AbstractMultimap.this.size();
    }

    
    public boolean contains(@CheckForNull Object o) {
      return AbstractMultimap.this.containsValue(o);
    }

    
    public void clear() {
      AbstractMultimap.this.clear();
    }
  }

  Iterator<V> valueIterator() {
    return MapUtil.valueIterator(entries().iterator());
  }

  Spliterator<V> valueSpliterator() {
    return Spliterators.spliterator(valueIterator(), size(), 0);
  }

   @CheckForNull private transient Map<K, Collection<V>> asMap;

  
  public Map<K, Collection<V>> asMap() {
    Map<K, Collection<V>> result = asMap;
    return (result == null) ? asMap = createAsMap() : result;
  }

  abstract Map<K, Collection<V>> createAsMap();

  // Comparison and hashing

  
  public boolean equals(@CheckForNull Object object) {
    return MultimapUtil.equalsImpl(this, object);
  }

  /**
   * Returns the hash code for this multimap.
   *
   * <p>The hash code of a multimap is defined as the hash code of the map view, as returned by
   * {@link Multimap#asMap}.
   *
   * @see Map#hashCode
   */
  
  public int hashCode() {
    return asMap().hashCode();
  }

  /**
   * Returns a string representation of the multimap, generated by calling {@code toString} on the
   * map returned by {@link Multimap#asMap}.
   *
   * @return a string representation of the multimap
   */
  
  public String toString() {
    return asMap().toString();
  }
}
