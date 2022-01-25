package com.whaleal.icefrog.collections;

import java.util.AbstractList;

import static com.whaleal.icefrog.core.lang.Precondition.checkElementIndex;

/**
 * @author wh
 */
public  final class CharSequenceAsList extends AbstractList<Character> {
    private final CharSequence sequence;

    public CharSequenceAsList( CharSequence sequence ) {
        this.sequence = sequence;
    }

    @Override
    public Character get( int index ) {
        checkElementIndex(index, size()); // for GWT
        return sequence.charAt(index);
    }

    @Override
    public int size() {
        return sequence.length();
    }
}
