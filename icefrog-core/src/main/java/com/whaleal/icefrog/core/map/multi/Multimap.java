package com.whaleal.icefrog.core.map.multi;

import javax.annotation.CheckForNull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * A collection that MapUtil keys to values, similar to {@link Map}, but in which each key may be
 * associated with <i>multiple</i> values. You can visualize the contents of a multimap either as a
 * map from keys to <i>nonempty</i> collections of values:
 *
 * <ul>
 *   <li>a &rarr; 1, 2
 *   <li>b &rarr; 3
 * </ul>
 * <p>
 * ... or as a single "flattened" collection of key-value pairs:
 *
 * <ul>
 *   <li>a &rarr; 1
 *   <li>a &rarr; 2
 *   <li>b &rarr; 3
 * </ul>
 *
 * <p><b>Important:</b> although the first interpretation resembles how most Multimaps are
 * <i>implemented</i>, the design of the {@code Multimap} API is based on the <i>second</i> form.
 * So, using the multimap shown above as an example, the {@link Map#size} is {@code 3}, not {@code 2},
 * and the {@link Map#values} collection is {@code [1, 2, 3]}, not {@code [[1, 2], [3]]}. For those
 * times when the first style is more useful, use the multimap's {@link Map} view (or create a
 * {@code Map<K, Collection<V>>} in the first place).
 *
 * <h3>Example</h3>
 *
 * <p>The following code:
 *
 * <pre>{@code
 * ListMultimap<String, String> multimap = ArrayListMultimap.create();
 * for (President pres : US_PRESIDENTS_IN_ORDER) {
 *   multimap.put(pres.firstName(), pres.lastName());
 * }
 * for (String firstName : multimap.keySet()) {
 *   List<String> lastNames = multimap.get(firstName);
 *   out.println(firstName + ": " + lastNames);
 * }
 * }</pre>
 * <p>
 * ... produces output such as:
 *
 * <pre>{@code
 * Zachary: [Taylor]
 * John: [Adams, Adams, Tyler, Kennedy]  // Remember, Quincy!
 * George: [Washington, Bush, Bush]
 * Grover: [Cleveland, Cleveland]        // Two, non-consecutive terms, rep'ing NJ!
 * ...
 * }</pre>
 *
 * <h3>Views</h3>
 *
 * <p>Much of the power of the multimap API comes from the <i>view collections</i> it provides.
 * These always reflect the latest state of the multimap itself. When they support modification, the
 * changes are <i>write-through</i> (they automatically update the backing multimap). These view
 * collections are:
 *
 * <ul>
 *
 *   <li>{@link Map #keySet()}, {@link Map#keySet}, {@link Map#values}, {@link Map#entrySet}, which are similar to the
 *       corresponding view collections of {@link Map}
 *   <li>and, notably, even the collection returned by {@link Map#get get(key)} is an active view of
 *       the values corresponding to {@code key}
 * </ul>
 *
 * <p>The collections returned by the {@link #replaceValues replaceValues} and {@link #removeAll
 * removeAll} methods, which contain values that have just been removed from the multimap, are
 * naturally <i>not</i> views.
 *
 * <h3>Subinterfaces</h3>
 *
 * <p>Instead of using the {@code Multimap} interface directly, prefer the subinterfaces
 * . These take their names from the fact that the collections
 * they return from {@code get} behave like (and, of course, implement) {@link List} and {@link
 * Set}, respectively.
 *
 * <p>For example, the "presidents" code snippet above used a {@code ListMultimap}; if it had used a
 * {@code SetMultimap} instead, two presidents would have vanished, and last names might or might
 * not appear in chronological order.
 *
 * <p><b>Warning:</b> instances of type {@code Multimap} may not implement {@link Object#equals} in
 * the way you expect. Multimaps containing the same key-value pairs, even in the same order, may or
 * may not be equal and may or may not have the same {@code hashCode}. The recommended subinterfaces
 * provide much stronger guarantees.
 *
 * <h3>Comparison to a map of collections</h3>
 *
 * <p>Multimaps are commonly used in places where a {@code Map<K, Collection<V>>} would otherwise
 * have appeared. The differences include:
 *
 * <ul>
 *   <li>There is no need to populate an empty collection before adding an entry with {@link Map#put
 *       put}.
 *   <li>{@code get} never returns {@code null}, only an empty collection.
 *   <li>A key is contained in the multimap if and only if it MapUtil to at least one value. Any
 *       operation that causes a key to have zero associated values has the effect of
 *       <i>removing</i> that key from the multimap.
 *   <li>The total entry count is available as {@link Map#size}.
 *   <li>Many complex operations become easier; for example, {@code
 *       Collections.min(multimap.values())} finds the smallest value across all keys.
 * </ul>
 *
 * <h3>Implementations</h3>
 *
 *
 *
 * <h3>Other Notes</h3>
 *
 * <p>As with {@code Map}, the behavior of a {@code Multimap} is not specified if key objects
 * already present in the multimap change in a manner that affects {@code equals} comparisons. Use
 * caution if mutable objects are used as keys in a {@code Multimap}.
 *
 * <p>All methods that modify the multimap are optional. The view collections returned by the
 * multimap may or may not be modifiable. Any modification method that is not supported will throw
 * {@link UnsupportedOperationException}.
 *
 * <p>See the Guava User Guide article on <a href=
 * "https://github.com/google/guava/wiki/NewCollectionTypesExplained#multimap"> {@code
 * Multimap}</a>.
 */


public interface Multimap< K , V  > {


    /**
     * Returns {@code true} if this multimap contains at least one key-value pair with the key {@code
     * key} and the value {@code value}.
     */

    boolean containsEntry(
            Object key,
            Object value );


    /**
     *
     * 将多个value  依次添加到key  所对应的 Value  中
     * <pre>{@code
     * for (V value : values) {
     *   put(key, value);
     * }
     * }</pre>
     *
     *
     */
    boolean putAll( K key, Iterable<  V > values );

    /**
     * Stores a collection of values with the same key, replacing any existing values for that key.
     *
     * <p>If {@code values} is empty, this is equivalent to {@link #(Object) removeAll(key)}.
     *
     * @return the collection of replaced values, or an empty collection if no values were previously
     * associated with the key. The collection <i>may</i> be modifiable, but updating it will have
     * no effect on the multimap.
     */

    Collection< V > replaceValues( K key, Iterable<  V > values );


}
