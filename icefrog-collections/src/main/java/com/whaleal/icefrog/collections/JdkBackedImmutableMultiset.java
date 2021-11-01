

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;
import com.whaleal.icefrog.core.util.NumberUtil;

import javax.annotation.CheckForNull;
import java.util.Collection;
import java.util.Map;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * An implementation of ImmutableMultiset backed by a JDK Map and a list of entries. Used to protect
 * against hash flooding attacks.
 *
 * 
 */


final class JdkBackedImmutableMultiset<E> extends ImmutableMultiset<E> {
  private final Map<E, Integer> delegateMap;
  private final ImmutableList<Entry<E>> entries;
  private final long size;

  static <E> ImmutableMultiset<E> create(Collection<? extends Entry<? extends E>> entries) {
    @SuppressWarnings("unchecked")
    Entry<E>[] entriesArray = entries.toArray(new Entry[0]);
    Map<E, Integer> delegateMap = MapUtil.newHashMap(entriesArray.length);
    long size = 0;
    for (int i = 0; i < entriesArray.length; i++) {
      Entry<E> entry = entriesArray[i];
      int count = entry.getCount();
      size += count;
      E element = checkNotNull(entry.getElement());
      delegateMap.put(element, count);
      if (!(entry instanceof Multisets.ImmutableEntry)) {
        entriesArray[i] = Multisets.immutableEntry(element, count);
      }
    }
    return new JdkBackedImmutableMultiset<>(
        delegateMap, ImmutableList.asImmutableList(entriesArray), size);
  }

  private JdkBackedImmutableMultiset(
      Map<E, Integer> delegateMap, ImmutableList<Entry<E>> entries, long size) {
    this.delegateMap = delegateMap;
    this.entries = entries;
    this.size = size;
  }

  @Override
  public int count(@CheckForNull Object element) {
    return delegateMap.getOrDefault(element, 0);
  }

  @CheckForNull private transient ImmutableSet<E> elementSet;

  @Override
  public ImmutableSet<E> elementSet() {
    ImmutableSet<E> result = elementSet;
    return (result == null) ? elementSet = new ElementSet<>(entries, this) : result;
  }

  @Override
  Entry<E> getEntry(int index) {
    return entries.get(index);
  }

  @Override
  boolean isPartialView() {
    return false;
  }

  @Override
  public int size() {
    return (int)NumberUtil.saturatedCast(size ,Integer.class);
  }
}
