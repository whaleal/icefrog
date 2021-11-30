package com.whaleal.icefrog.collections;

/**
 * Implementation of {@link ImmutableListMultimap} with no entries.
 */


class EmptyImmutableListMultimap extends ImmutableListMultimap<Object, Object> {
    static final EmptyImmutableListMultimap INSTANCE = new EmptyImmutableListMultimap();
    private static final long serialVersionUID = 0;

    private EmptyImmutableListMultimap() {
        super(ImmutableMap.of(), 0);
    }

    private Object readResolve() {
        return INSTANCE; // preserve singleton property
    }
}
