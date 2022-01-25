package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.function.Supplier;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * @author wh
 */
public class CustomSortedSetMultimap<
        K extends Object, V extends Object >
        extends AbstractSortedSetMultimap< K, V > {
    // not needed in emulated source
    private static final long serialVersionUID = 0;
    transient Supplier< ? extends SortedSet< V > > factory;
    @CheckForNull
    transient Comparator< ? super V > valueComparator;

    CustomSortedSetMultimap( Map< K, Collection< V > > map, Supplier< ? extends SortedSet< V > > factory ) {
        super(map);
        this.factory = checkNotNull(factory);
        valueComparator = factory.get().comparator();
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
    protected SortedSet< V > createCollection() {
        return factory.get();
    }

    @Override
    @CheckForNull
    public Comparator< ? super V > valueComparator() {
        return valueComparator;
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
        factory = (Supplier< ? extends SortedSet< V > >) stream.readObject();
        valueComparator = factory.get().comparator();
        Map< K, Collection< V > > map = (Map< K, Collection< V > >) stream.readObject();
        setMap(map);
    }
}
