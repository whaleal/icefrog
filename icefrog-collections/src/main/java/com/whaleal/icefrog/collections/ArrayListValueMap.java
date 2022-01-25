package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;
import com.whaleal.icefrog.core.map.multi.CollectionValueMap;
import com.whaleal.icefrog.core.map.multi.ListValueMap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

import static com.whaleal.icefrog.core.lang.Precondition.checkNonnegative;


/**
 * Implementation of {@code Multimap} that uses an {@code ArrayList} to store the values for a given
 * key. A {@link HashMap} associates each key with an {@link ArrayList} of values.
 *
 * <p>When iterating through the collections supplied by this class, the ordering of values for a
 * given key agrees with the order in which the values were added.
 *
 * <p>This multimap allows duplicate key-value pairs. After adding a new key-value pair equal to an
 * existing key-value pair, the {@code ArrayListMultimap} will contain entries for both the new
 * value and the old value.
 *
 * <p>Keys and values may be null. All optional multimap methods are supported, and all returned
 * views are modifiable.
 *
 * <p>The lists returned by {@link #get}, {@link #remove(Object)}, and {@link #replaceValues} all
 * implement {@link java.util.RandomAccess}.
 *
 * <p>This class is not threadsafe when any concurrent operations update the multimap. Concurrent
 * read operations will work correctly. To allow concurrent update operations, wrap your multimap
 * with a call to {@link MultimapUtil#synchronizedListMultimap}.
 *
 * <p>See the Guava User Guide article on <a href=
 * "https://github.com/google/guava/wiki/NewCollectionTypesExplained#multimap"> {@code
 * Multimap}</a>.
 */


public final class ArrayListValueMap<K extends Object, V extends Object>
        extends ListValueMap<K, V> {

}
