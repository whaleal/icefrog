package com.whaleal.icefrog.collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * @author wh
 */
class CustomListMultimap< K extends Object, V extends Object >
        extends AbstractListMultimap< K, V > {
    // java serialization not supported
    private static final long serialVersionUID = 0;
    transient Supplier< ? extends List< V > > factory;

    CustomListMultimap( Map< K, Collection< V > > map, Supplier< ? extends List< V > > factory ) {
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
    protected List< V > createCollection() {
        return factory.get();
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
        factory = (Supplier< ? extends List< V > >) stream.readObject();
        Map< K, Collection< V > > map = (Map< K, Collection< V > >) stream.readObject();
        setMap(map);
    }
}
