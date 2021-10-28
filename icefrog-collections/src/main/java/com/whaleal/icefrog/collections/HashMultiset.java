

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;






/**
 * Multiset implementation backed by a {@link HashMap}.
 *
 *
 *
 * 
 */


public final class HashMultiset<E extends Object> extends AbstractMapBasedMultiset<E> {

  /** Creates a new, empty {@code HashMultiset} using the default initial capacity. */
  public static <E extends Object> HashMultiset<E> create() {
    return new HashMultiset<E>();
  }

  /**
   * Creates a new, empty {@code HashMultiset} with the specified expected number of distinct
   * elements.
   *
   * @param distinctElements the expected number of distinct elements
   * @throws IllegalArgumentException if {@code distinctElements} is negative
   */
  public static <E extends Object> HashMultiset<E> create(int distinctElements) {
    return new HashMultiset<E>(distinctElements);
  }

  /**
   * Creates a new {@code HashMultiset} containing the specified elements.
   *
   * <p>This implementation is highly efficient when {@code elements} is itself a {@link Multiset}.
   *
   * @param elements the elements that the multiset should contain
   */
  public static <E extends Object> HashMultiset<E> create(
      Iterable<? extends E> elements) {
    HashMultiset<E> multiset = create(Multisets.inferDistinctElements(elements));
    Iterables.addAll(multiset, elements);
    return multiset;
  }

  private HashMultiset() {
    super(new HashMap<E, Count>());
  }

  private HashMultiset(int distinctElements) {
    super(MapUtil.newHashMap(distinctElements));
  }

  /**
   * @serialData the number of distinct elements, the first element, its count, the second element,
   *     its count, and so on
   */
 // java.io.ObjectOutputStream
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    Serialization.writeMultiset(this, stream);
  }

 // java.io.ObjectInputStream
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    int distinctElements = Serialization.readCount(stream);
    setBackingMap(MapUtil.newHashMap());
    Serialization.populateMultiset(this, stream, distinctElements);
  }

 // Not needed in emulated source.
  private static final long serialVersionUID = 0;
}
