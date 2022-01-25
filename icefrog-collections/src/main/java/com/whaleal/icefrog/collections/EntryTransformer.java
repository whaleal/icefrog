package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.util.ObjectUtil;

import java.util.Map;

/**
 * A transformation of the value of a key-value pair, using both key and value as inputs. To apply
 * the transformation to a map, use .
 *
 * @param <K>  the key type of the input and output entries
 * @param <V1> the value type of the input entry
 * @param <V2> the value type of the output entry
 */
@FunctionalInterface
public interface EntryTransformer<
        K extends Object, V1 extends Object, V2 extends Object > {
    /**
     * Determines an output value based on a key-value pair. This method is <i>generally
     * expected</i>, but not absolutely required, to have the following properties:
     *
     * <ul>
     *   <li>Its execution does not cause any observable side effects.
     *   <li>The computation is <i>consistent with equals</i>; that is, {@link ObjectUtil#equal
     *       ObjectUtil.equal}{@code (k1, k2) &&} {@link ObjectUtil#equal}{@code (v1, v2)} implies that
     *       {@code ObjectUtil.equal(transformer.transform(k1, v1), transformer.transform(k2, v2))}.
     * </ul>
     *
     * @throws NullPointerException if the key or value is null and this transformer does not accept
     *                              null arguments
     */
    V2 transformEntry( K key, V1 value );
}
