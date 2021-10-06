
package com.whaleal.icefrog.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>The class to which this annotation is applied is immutable.  This means that its state cannot be seen to change by callers, which
 * implies that</p>
 * <ul>
 *     <li> all public fields are final, </li>
 *     <li> all public final reference fields refer to other immutable objects, and </li>
 *     <li> constructors and methods do not publish references to any internal state which is potentially mutable by the
 *          implementation. </li>
 * </ul>
 * <p>Immutable objects may still have internal mutable state for purposes of performance optimization; some state
 * variables may be lazily computed, so long as they are computed from immutable state and that callers cannot tell the difference. </p>
 *
 * <p>Immutable objects are inherently thread-safe; they may be passed between threads or published without synchronization.</p>
 *
 * @author wh
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Immutable {
}
