

package com.whaleal.icefrog.collections;



import java.util.Collection;
import java.util.Map;

/**
 * A dummy superclass to support GWT serialization of the element types of an {@link
 * ArrayListValueMap}. The GWT supersource for this class contains a field for each type.
 *
 * <p>For details about this hack, see {@code GwtSerializationDependencies}, which takes the same
 * approach but with a subclass rather than a superclass.
 *
 * <p>TODO(cpovirk): Consider applying this subclass approach to our other types.
 */

abstract class ArrayListMultimapGwtSerializationDependencies<K, V>
    extends AbstractListMultimap<K, V> {
  ArrayListMultimapGwtSerializationDependencies(Map<K, Collection<V>> map) {
    super(map);
  }
  // TODO(cpovirk): Maybe I should have just one shared superclass for AbstractMultimap itself?
}
