package com.whaleal.icefrog.collections;


/**
 * A dummy superclass of {@link ImmutableMultimap} that can be instanceof'd without ProGuard
 * retaining additional implementation details of {@link ImmutableMultimap}.
 */


abstract class BaseImmutableMultimap<K, V> extends AbstractMultimap<K, V> {
}
