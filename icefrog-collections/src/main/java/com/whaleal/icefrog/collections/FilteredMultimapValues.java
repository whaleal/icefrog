

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.collection.IterUtil;
import com.whaleal.icefrog.core.map.MapUtil;
import com.whaleal.icefrog.core.util.ObjectUtil;
import com.whaleal.icefrog.core.lang.Predicate;
import com.whaleal.icefrog.core.util.Predicates;

import javax.annotation.CheckForNull;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import static com.whaleal.icefrog.core.lang.Preconditions.checkNotNull;


/**
 * Implementation for {@link FilteredMultimap#values()}.
 *
 * 
 */


final class FilteredMultimapValues<K extends Object, V extends Object>
    extends AbstractCollection<V> {
   private final FilteredMultimap<K, V> multimap;

  FilteredMultimapValues(FilteredMultimap<K, V> multimap) {
    this.multimap = checkNotNull(multimap);
  }

  @Override
  public Iterator<V> iterator() {
    return Maps.valueIterator(multimap.entries().iterator());
  }

  @Override
  public boolean contains(@CheckForNull Object o) {
    return multimap.containsValue(o);
  }

  @Override
  public int size() {
    return multimap.size();
  }

  @Override
  public boolean remove(@CheckForNull Object o) {
    Predicate<? super Entry<K, V>> entryPredicate = multimap.entryPredicate();
    for (Iterator<Entry<K, V>> unfilteredItr = multimap.unfiltered().entries().iterator();
        unfilteredItr.hasNext(); ) {
      Entry<K, V> entry = unfilteredItr.next();
      if (entryPredicate.apply(entry) && ObjectUtil.equal(entry.getValue(), o)) {
        unfilteredItr.remove();
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return IterUtil.removeIf(
        multimap.unfiltered().entries(),
        // explicit <Entry<K, V>> is required to build with JDK6
        Predicates.and(
            multimap.entryPredicate(), MapUtil.valuePredicateOnEntries(Predicates.in(c))));
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return IterUtil.removeIf(
        multimap.unfiltered().entries(),
        // explicit <Entry<K, V>> is required to build with JDK6
        Predicates.and(
            multimap.entryPredicate(),
            MapUtil.valuePredicateOnEntries(Predicates.not(Predicates.in(c)))));
  }

  @Override
  public void clear() {
    multimap.clear();
  }
}
