package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.collection.SpliteratorUtil;

import javax.annotation.CheckForNull;
import java.util.*;
import java.util.function.Consumer;

import static com.whaleal.icefrog.core.lang.Precondition.checkNonnegative;
import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * @author wh
 */
class CKeys< K extends Object, V extends Object >
        extends AbstractMultiset< K > {
    final Multimap< K, V > multimap;

    CKeys( Multimap< K, V > multimap ) {
        this.multimap = multimap;
    }

    @Override
    Iterator< Entry< K > > entryIterator() {
        return new TransIter< Map.Entry< K, Collection< V > >, Entry< K > >(
                multimap.asMap().entrySet().iterator()) {
            @Override
            Entry< K > transform( final Map.Entry< K, Collection< V > > backingEntry ) {
                return new Multisets.AbstractEntry< K >() {
                    @Override
                    @ParametricNullness
                    public K getElement() {
                        return backingEntry.getKey();
                    }

                    @Override
                    public int getCount() {
                        return backingEntry.getValue().size();
                    }
                };
            }
        };
    }

    @Override
    public Spliterator< K > spliterator() {
        return SpliteratorUtil.map(multimap.entries().spliterator(), Map.Entry::getKey);
    }

    @Override
    public void forEach( Consumer< ? super K > consumer ) {
        checkNotNull(consumer);
        multimap.entries().forEach(entry -> consumer.accept(entry.getKey()));
    }

    @Override
    int distinctElements() {
        return multimap.asMap().size();
    }

    @Override
    public int size() {
        return multimap.size();
    }

    @Override
    public boolean contains( @CheckForNull Object element ) {
        return multimap.containsKey(element);
    }

    @Override
    public Iterator< K > iterator() {
        //  new TransIter
        return MapUtil.keyIterator(multimap.entries().iterator());
    }

    @Override
    public int count( @CheckForNull Object element ) {
        Collection< V > values = MapUtil.safeGet(multimap.asMap(), element);
        return (values == null) ? 0 : values.size();
    }

    @Override
    public int remove( @CheckForNull Object element, int occurrences ) {
        checkNonnegative(occurrences, "occurrences");
        if (occurrences == 0) {
            return count(element);
        }

        Collection< V > values = MapUtil.safeGet(multimap.asMap(), element);

        if (values == null) {
            return 0;
        }

        int oldCount = values.size();
        if (occurrences >= oldCount) {
            values.clear();
        } else {
            Iterator< V > iterator = values.iterator();
            for (int i = 0; i < occurrences; i++) {
                iterator.next();
                iterator.remove();
            }
        }
        return oldCount;
    }

    @Override
    public void clear() {
        multimap.clear();
    }

    @Override
    public Set< K > elementSet() {
        return multimap.keySet();
    }

    @Override
    Iterator< K > elementIterator() {
        throw new AssertionError("should never be called");
    }
}
