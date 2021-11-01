

package com.whaleal.icefrog.collections;

import java.util.Collection;
import java.util.Map;

/**
 * A dummy superclass to support GWT serialization of the element types of a {@link HashMultimap}.
 * The GWT supersource for this class contains a field for each type.
 *
 * <p>For details about this hack, see {@code GwtSerializationDependencies}, which takes the same
 * approach but with a subclass rather than a superclass.
 *
 * <p>TODO(cpovirk): Consider applying this subclass approach to our other types.
 */

abstract class HashMultimapGwtSerializationDependencies<K, V> extends AbstractSetMultimap<K, V> {
  HashMultimapGwtSerializationDependencies(Map<K, Collection<V>> map) {
    super(map);
  }
}
