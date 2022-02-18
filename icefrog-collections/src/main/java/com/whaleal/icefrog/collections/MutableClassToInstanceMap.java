package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.collection.SpliteratorUtil;
import com.whaleal.icefrog.core.util.ClassUtil;
import com.whaleal.icefrog.core.collection.TransIter;

import javax.annotation.CheckForNull;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;


/**
 * A mutable class-to-instance map backed by an arbitrary user-provided map. See also
 *
 * <p>See the Guava User Guide article on <a href=
 * "https://github.com/google/guava/wiki/NewCollectionTypesExplained#classtoinstancemap">{@code
 * ClassToInstanceMap}</a>.
 *
 * <p>This implementation <i>does</i> support null values, despite how it is annotated; see
 * discussion at {@link ClassToInstanceMap}.
 */

@SuppressWarnings("serial") // using writeReplace instead of standard serialization

public final class MutableClassToInstanceMap<B> extends ForwardingMap<Class<? extends B>, B>
        implements ClassToInstanceMap<B>, Serializable {

    private final Map<Class<? extends B>, B> delegate;

    private MutableClassToInstanceMap( Map<Class<? extends B>, B> delegate ) {
        this.delegate = checkNotNull(delegate);
    }

    /**
     * Returns a new {@code MutableClassToInstanceMap} instance backed by a {@link HashMap} using the
     * default initial capacity and load factor.
     */
    public static <B> MutableClassToInstanceMap<B> create() {
        return new MutableClassToInstanceMap<B>(new HashMap<Class<? extends B>, B>());
    }

    /**
     * Returns a new {@code MutableClassToInstanceMap} instance backed by a given empty {@code
     * backingMap}. The caller surrenders control of the backing map, and thus should not allow any
     * direct references to it to remain accessible.
     */
    public static <B> MutableClassToInstanceMap<B> create( Map<Class<? extends B>, B> backingMap ) {
        return new MutableClassToInstanceMap<B>(backingMap);
    }

    /**
     * Wraps the {@code setValue} implementation of an {@code Entry} to enforce the class constraint.
     */
    private static <B> Entry<Class<? extends B>, B> checkedEntry(
            final Entry<Class<? extends B>, B> entry ) {
        return new ForwardingMapEntry<Class<? extends B>, B>() {
            @Override
            protected Entry<Class<? extends B>, B> delegate() {
                return entry;
            }

            @Override
            public B setValue( B value ) {
                return super.setValue(cast(getKey(), value));
            }
        };
    }

    @CheckForNull
    private static <B, T extends B> T cast( Class<T> type, @CheckForNull B value ) {
        return ClassUtil.getWrapperTypeIfPrimitive(type).cast(value);
    }

    @Override
    protected Map<Class<? extends B>, B> delegate() {
        return delegate;
    }

    @Override
    public Set<Entry<Class<? extends B>, B>> entrySet() {
        return new ForwardingSet<Entry<Class<? extends B>, B>>() {

            @Override
            protected Set<Entry<Class<? extends B>, B>> delegate() {
                return MutableClassToInstanceMap.this.delegate().entrySet();
            }

            @Override
            public Spliterator<Entry<Class<? extends B>, B>> spliterator() {
                return SpliteratorUtil.map(
                        delegate().spliterator(), MutableClassToInstanceMap::checkedEntry);
            }

            @Override
            public Iterator<Entry<Class<? extends B>, B>> iterator() {
                return new TransIter<Entry<Class<? extends B>, B>, Entry<Class<? extends B>, B>>(
                        delegate().iterator(),new Function<Entry<Class<? extends B>, B> , Entry<Class<? extends B>, B>>(){

                    @Override
                    public Entry< Class< ? extends B >, B > apply( Entry< Class< ? extends B >, B > from ) {
                        return checkedEntry(from);
                    }
                }) ;
            }

            @Override
            public Object[] toArray() {
                /*
                 * standardToArray returns `Object[]` rather than `Object[]` but only because it
                 * can be used with collections that may contain null. This collection is a collection of
                 * non-null Entry objects (Entry objects that might contain null values but are not
                 * themselves null), so we can treat it as a plain `Object[]`.
                 */
                @SuppressWarnings("nullness")
                Object[] result = standardToArray();
                return result;
            }

            @Override
            @SuppressWarnings("nullness") // b/192354773 in our checker affects toArray declarations
            public <T extends Object> T[] toArray( T[] array ) {
                return standardToArray(array);
            }
        };
    }

    @Override

    @CheckForNull
    public B put( Class<? extends B> key, B value ) {
        return super.put(key, cast(key, value));
    }

    @Override
    public void putAll( Map<? extends Class<? extends B>, ? extends B> map ) {
        Map<Class<? extends B>, B> copy = new LinkedHashMap<>(map);
        for (Entry<? extends Class<? extends B>, B> entry : copy.entrySet()) {
            cast(entry.getKey(), entry.getValue());
        }
        super.putAll(copy);
    }

    @Override
    @CheckForNull
    public <T extends B> T putInstance( Class<T> type, T value ) {
        return cast(type, put(type, value));
    }

    @Override
    @CheckForNull
    public <T extends B> T getInstance( Class<T> type ) {
        return cast(type, get(type));
    }

    private Object writeReplace() {
        return new SerializedForm(delegate());
    }

    /**
     * Serialized form of the map, to avoid serializing the constraint.
     */
    private static final class SerializedForm<B> implements Serializable {
        private static final long serialVersionUID = 0;
        private final Map<Class<? extends B>, B> backingMap;

        SerializedForm( Map<Class<? extends B>, B> backingMap ) {
            this.backingMap = backingMap;
        }

        Object readResolve() {
            return create(backingMap);
        }
    }
}
