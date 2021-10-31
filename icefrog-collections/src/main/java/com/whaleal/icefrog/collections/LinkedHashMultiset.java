

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.collection.IterUtil;
import com.whaleal.icefrog.core.map.MapUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;





/**
 * A {@code Multiset} implementation with predictable iteration order. Its iterator orders elements
 * according to when the first occurrence of the element was added. When the multiset contains
 * multiple instances of an element, those instances are consecutive in the iteration order. If all
 * occurrences of an element are removed, after which that element is added to the multiset, the
 * element will appear at the end of the iteration.
 *
 * <p>See the Guava User Guide article on <a href=
 * "https://github.com/google/guava/wiki/NewCollectionTypesExplained#multiset"> {@code
 * Multiset}</a>.
 *
 *
 *
 * 
 */


public final class LinkedHashMultiset<E extends Object>
    extends AbstractMapBasedMultiset<E> {

  /** Creates a new, empty {@code LinkedHashMultiset} using the default initial capacity. */
  public static <E extends Object> LinkedHashMultiset<E> create() {
    return new LinkedHashMultiset<E>();
  }

  /**
   * Creates a new, empty {@code LinkedHashMultiset} with the specified expected number of distinct
   * elements.
   *
   * @param distinctElements the expected number of distinct elements
   * @throws IllegalArgumentException if {@code distinctElements} is negative
   */
  public static <E extends Object> LinkedHashMultiset<E> create(int distinctElements) {
    return new LinkedHashMultiset<E>(distinctElements);
  }

  /**
   * Creates a new {@code LinkedHashMultiset} containing the specified elements.
   *
   * <p>This implementation is highly efficient when {@code elements} is itself a {@link Multiset}.
   *
   * @param elements the elements that the multiset should contain
   */
  public static <E extends Object> LinkedHashMultiset<E> create(
      Iterable<? extends E> elements) {
    LinkedHashMultiset<E> multiset = create(Multisets.inferDistinctElements(elements));
    IterUtil.addAll(multiset, elements);
    return multiset;
  }

  private LinkedHashMultiset() {
    super(new LinkedHashMap<E, Count>());
  }

  private LinkedHashMultiset(int distinctElements) {
    super(MapUtil.newHashMap(distinctElements,true));
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
    setBackingMap(new LinkedHashMap<E, Count>());
    Serialization.populateMultiset(this, stream, distinctElements);
  }

 // not needed in emulated source
  private static final long serialVersionUID = 0;
}
