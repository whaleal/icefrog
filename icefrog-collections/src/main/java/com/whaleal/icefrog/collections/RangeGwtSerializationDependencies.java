

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;




import java.io.Serializable;

/**
 * A dummy superclass to support GWT serialization of the element type of a {@link Range}. The GWT
 * supersource for this class contains a field of type {@code C}.
 *
 * <p>For details about this hack, see {@code GwtSerializationDependencies}, which takes the same
 * approach but with a subclass rather than a superclass.
 *
 * <p>TODO(cpovirk): Consider applying this subclass approach to our other types.
 */

abstract class RangeGwtSerializationDependencies<C extends Comparable> implements Serializable {}
