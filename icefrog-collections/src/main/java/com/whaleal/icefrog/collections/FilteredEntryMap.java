package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import static com.whaleal.icefrog.core.lang.Precondition.checkArgument;

/**
 * @author wh
 */
class FilteredEntryMap< K extends Object, V extends Object >
        extends AbstractFilteredMap< K, V > {
    /**
     * Entries in this set satisfy the predicate, but they don't validate the input to {@code
     * Entry.setValue()}.
     */
    final Set< Entry< K, V > > filteredEntrySet;

    FilteredEntryMap( Map< K, V > unfiltered, Predicate< ? super Entry< K, V > > entryPredicate ) {
        super(unfiltered, entryPredicate);
        filteredEntrySet = SetUtil.filter(unfiltered.entrySet(), predicate);
    }

    @Override
    protected Set< Entry< K, V > > createEntrySet() {
        return new EntrySet();
    }

    @WeakOuter
    private class EntrySet extends ForwardingSet< Entry< K, V > > {
        @Override
        protected Set< Entry< K, V > > delegate() {
            return filteredEntrySet;
        }

        @Override
        public Iterator< Entry< K, V > > iterator() {
            return new TransformedIterator< Entry< K, V >, Entry< K, V > >(filteredEntrySet.iterator()) {
                @Override
                Entry< K, V > transform( final Entry< K, V > entry ) {
                    return new ForwardingMapEntry< K, V >() {
                        @Override
                        protected Entry< K, V > delegate() {
                            return entry;
                        }

                        @Override
                        @ParametricNullness
                        public V setValue( @ParametricNullness V newValue ) {
                            checkArgument(apply(getKey(), newValue));
                            return super.setValue(newValue);
                        }
                    };
                }
            };
        }
    }

    @Override
    Set< K > createKeySet() {
        return new KeySet();
    }

    static < K extends Object, V extends Object > boolean removeAllKeys(
            Map< K, V > map, Predicate< ? super Entry< K, V > > entryPredicate, Collection< ? > keyCollection ) {
        Iterator< Entry< K, V > > entryItr = map.entrySet().iterator();
        boolean result = false;
        while (entryItr.hasNext()) {
            Entry< K, V > entry = entryItr.next();
            if (entryPredicate.apply(entry) && keyCollection.contains(entry.getKey())) {
                entryItr.remove();
                result = true;
            }
        }
        return result;
    }

    static < K extends Object, V extends Object > boolean retainAllKeys(
            Map< K, V > map, Predicate< ? super Entry< K, V > > entryPredicate, Collection< ? > keyCollection ) {
        Iterator< Entry< K, V > > entryItr = map.entrySet().iterator();
        boolean result = false;
        while (entryItr.hasNext()) {
            Entry< K, V > entry = entryItr.next();
            if (entryPredicate.apply(entry) && !keyCollection.contains(entry.getKey())) {
                entryItr.remove();
                result = true;
            }
        }
        return result;
    }

    @WeakOuter
    class KeySet extends CKeySet< K, V > {
        KeySet() {
            super(FilteredEntryMap.this);
        }

        @Override
        public boolean remove( @CheckForNull Object o ) {
            if (containsKey(o)) {
                unfiltered.remove(o);
                return true;
            }
            return false;
        }

        @Override
        public boolean removeAll( Collection< ? > collection ) {
            return removeAllKeys(unfiltered, predicate, collection);
        }

        @Override
        public boolean retainAll( Collection< ? > collection ) {
            return retainAllKeys(unfiltered, predicate, collection);
        }

        @Override
        public Object[] toArray() {
            // creating an ArrayList so filtering happens once
            return Lists.newArrayList(iterator()).toArray();
        }

        @Override
        @SuppressWarnings("nullness") // b/192354773 in our checker affects toArray declarations
        public < T extends Object > T[] toArray( T[] array ) {
            return Lists.newArrayList(iterator()).toArray(array);
        }
    }
}
