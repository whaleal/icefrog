package com.whaleal.icefrog.core.map;

import javax.annotation.CheckForNull;
import java.util.Map;
import java.util.function.Function;

/**
 * @author wh
 */
public enum EntryFunction implements Function< Map.Entry< ?, ? >, Object > {
    KEY {
        @Override
        @CheckForNull
        public Object apply( Map.Entry< ?, ? > entry ) {
            return entry.getKey();
        }
    },
    VALUE {
        @Override
        @CheckForNull
        public Object apply( Map.Entry< ?, ? > entry ) {
            return entry.getValue();
        }
    };
}
