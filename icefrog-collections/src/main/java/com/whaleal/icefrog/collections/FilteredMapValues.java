package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import com.whaleal.icefrog.core.lang.Predicate ;

/**
 * @author wh
 */
final class FilteredMapValues<
        K extends Object, V extends Object >
        extends Values< K, V > {
    final Map< K, V > unfiltered;
    final Predicate< ? super Map.Entry< K, V > > predicate;

    FilteredMapValues(
            Map< K, V > filteredMap, Map< K, V > unfiltered, Predicate< ? super Map.Entry< K, V > > predicate ) {
        super(filteredMap);
        this.unfiltered = unfiltered;
        this.predicate = predicate;
    }

    @Override
    public boolean remove( @CheckForNull Object o ) {
        Iterator< Map.Entry< K, V > > entryItr = unfiltered.entrySet().iterator();
        while (entryItr.hasNext()) {
            Map.Entry< K, V > entry = entryItr.next();
            if (predicate.apply(entry) && Objects.equal(entry.getValue(), o)) {
                entryItr.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removeAll( Collection< ? > collection ) {
        Iterator< Map.Entry< K, V > > entryItr = unfiltered.entrySet().iterator();
        boolean result = false;
        while (entryItr.hasNext()) {
            Map.Entry< K, V > entry = entryItr.next();
            if (predicate.apply(entry) && collection.contains(entry.getValue())) {
                entryItr.remove();
                result = true;
            }
        }
        return result;
    }

    @Override
    public boolean retainAll( Collection< ? > collection ) {
        Iterator< Map.Entry< K, V > > entryItr = unfiltered.entrySet().iterator();
        boolean result = false;
        while (entryItr.hasNext()) {
            Map.Entry< K, V > entry = entryItr.next();
            if (predicate.apply(entry) && !collection.contains(entry.getValue())) {
                entryItr.remove();
                result = true;
            }
        }
        return result;
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
