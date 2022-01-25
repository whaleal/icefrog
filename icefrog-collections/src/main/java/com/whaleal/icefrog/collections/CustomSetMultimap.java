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
class CustomSetMultimap< K extends Object, V extends Object >
        extends AbstractSetMultimap< K, V > {
    // not needed in emulated source
    private static final long serialVersionUID = 0;
    transient Supplier< ? extends Set< V > > factory;

    CustomSetMultimap( Map< K, Collection< V > > map, Supplier< ? extends Set< V > > factory ) {
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
    protected Set< V > createCollection() {
        return factory.get();
    }

    @Override
    < E extends Object > Collection< E > unmodifiableCollectionSubclass(
            Collection< E > collection ) {
        if (collection instanceof NavigableSet) {
            return SetUtil.unmodifiableNavigableSet((NavigableSet< E >) collection);
        } else if (collection instanceof SortedSet) {
            return Collections.unmodifiableSortedSet((SortedSet< E >) collection);
        } else {
            return Collections.unmodifiableSet((Set< E >) collection);
        }
    }

    @Override
    Collection< V > wrapCollection( @ParametricNullness K key, Collection< V > collection ) {
        if (collection instanceof NavigableSet) {
            return new WrappedNavigableSet(key, (NavigableSet< V >) collection, null);
        } else if (collection instanceof SortedSet) {
            return new WrappedSortedSet(key, (SortedSet< V >) collection, null);
        } else {
            return new WrappedSet(key, (Set< V >) collection);
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
        factory = (Supplier< ? extends Set< V > >) stream.readObject();
        Map< K, Collection< V > > map = (Map< K, Collection< V > >) stream.readObject();
        setMap(map);
    }
}
