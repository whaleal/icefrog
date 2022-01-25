package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;

import static com.whaleal.icefrog.core.lang.Precondition.*;
import static com.whaleal.icefrog.core.lang.Precondition.checkElementIndex;

/**
 * @author wh
 */
public final class StringAsImmutableList extends ImmutableList<Character> {

    private final String string;

    StringAsImmutableList( String string ) {
        this.string = string;
    }

    @Override
    public int indexOf( @CheckForNull Object object ) {
        return (object instanceof Character) ? string.indexOf((Character) object) : -1;
    }

    @Override
    public int lastIndexOf( @CheckForNull Object object ) {
        return (object instanceof Character) ? string.lastIndexOf((Character) object) : -1;
    }

    @Override
    public ImmutableList<Character> subList( int fromIndex, int toIndex ) {
        checkPositionIndexes(fromIndex, toIndex, size()); // for GWT
        return new StringAsImmutableList(checkNotNull(string.substring(fromIndex, toIndex)));
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    public Character get( int index ) {
        checkElementIndex(index, size()); // for GWT
        return string.charAt(index);
    }

    @Override
    public int size() {
        return string.length();
    }
}



