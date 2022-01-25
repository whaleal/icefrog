package com.whaleal.icefrog.collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.function.Supplier;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * @author wh
 */
class CustomMultimap< K extends Object, V extends Object >
        extends AbstractMapBasedMultimap< K, V > {
    // java serialization not supported
    private static final long serialVersionUID = 0;
    transient Supplier< ? extends Collection< V > > factory;

    CustomMultimap( Map< K, Collection< V > > map, Supplier< ? extends Collection< V > > factory ) {
        super(map);
        this.factory = checkNotNull(factory);
    }

    @Override
    Set< K > createKeySet() {
        return createMaybeNavigableKeySet();
    }

    @Override
    Map< K, Collection< V > > createAsMap() {
        return createMaybeNavigableAsMap();
    }

    @Override
    protected Collection< V > createCollection() {
        return factory.get();
    }

    @Override
    < E extends Object > Collection< E > unmodifiableCollectionSubclass(
            Collection< E > collection ) {
        if (collection instanceof NavigableSet) {
            return SetUtil.unmodifiableNavigableSet((NavigableSet< E >) collection);
        } else if (collection instanceof SortedSet) {
            return Collections.unmodifiableSortedSet((SortedSet< E >) collection);
        } else if (collection instanceof Set) {
            return Collections.unmodifiableSet((Set< E >) collection);
        } else if (collection instanceof List) {
            return Collections.unmodifiableList((List< E >) collection);
        } else {
            return Collections.unmodifiableCollection(collection);
        }
    }

    // can't use Serialization writeMultimap and populateMultimap methods since
    // there's no way to generate the empty backing map.

    @Override
    Collection< V > wrapCollection( @ParametricNullness K key, Collection< V > collection ) {
        if (collection instanceof List) {
            return wrapList(key, (List< V >) collection, null);
        } else if (collection instanceof NavigableSet) {
            return new WrappedNavigableSet(key, (NavigableSet< V >) collection, null);
        } else if (collection instanceof SortedSet) {
            return new WrappedSortedSet(key, (SortedSet< V >) collection, null);
        } else if (collection instanceof Set) {
            return new WrappedSet(key, (Set< V >) collection);
        } else {
            return new WrappedCollection(key, collection, null);
        }
    }

    /**
     * @serialData the factory and the backing map
     */
    // java.io.ObjectOutputStream
    private void writeObject( ObjectOutputStream stream ) throws IOException {
        stream.defaultWriteObject();
        stream.writeObject(factory);
        stream.writeObject(backingMap());
    }

    // java.io.ObjectInputStream
    @SuppressWarnings("unchecked") // reading data stored by writeObject
    private void readObject( ObjectInputStream stream ) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        factory = (Supplier< ? extends Collection< V > >) stream.readObject();
        Map< K, Collection< V > > map = (Map< K, Collection< V > >) stream.readObject();
        setMap(map);
    }
}
