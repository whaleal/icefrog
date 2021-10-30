

package com.whaleal.icefrog.collections;


import static com.whaleal.icefrog.collections.Lists.transform;
import static com.whaleal.icefrog.collections.Sets.difference;
import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;


import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.whaleal.icefrog.core.collection.CollUtil;
import com.whaleal.icefrog.core.text.StrJoiner;
import com.whaleal.icefrog.core.util.ObjectUtil;
import junit.framework.TestCase;

/**
 * Tests that all {@code public static} methods "inherited" from superclasses are "overridden" in
 * each immutable-collection class. This ensures, for example, that a call written "{@code
 * ImmutableSortedSet.copyOf()}" cannot secretly be a call to {@code ImmutableSet.copyOf()}.
 *
 *
 */
public class FauxveridesTest extends TestCase {


  public void testImmutableListMultimap() {
    doHasAllFauxveridesTest(ImmutableListMultimap.class, ImmutableMultimap.class);
  }

  public void testImmutableSetMultimap() {
    doHasAllFauxveridesTest(ImmutableSetMultimap.class, ImmutableMultimap.class);
  }

  public void testImmutableSortedMap() {
    doHasAllFauxveridesTest(ImmutableSortedMap.class, ImmutableMap.class);
  }

  public void testImmutableSortedSet() {
    doHasAllFauxveridesTest(ImmutableSortedSet.class, ImmutableSet.class);
  }

  public void testImmutableSortedMultiset() {
    doHasAllFauxveridesTest(ImmutableSortedMultiset.class, ImmutableMultiset.class);
  }

  /*
   * Demonstrate that ClassCastException is possible when calling
   * ImmutableSorted{Set,Map}.copyOf(), whose type parameters we are unable to
   * restrict (see ImmutableSortedSetFauxverideShim).
   */

  public void testImmutableSortedMapCopyOfMap() {
    Map<Object, Object> original =
        ImmutableMap.of(new Object(), new Object(), new Object(), new Object());

    try {
      ImmutableSortedMap.copyOf(original);
      fail();
    } catch (ClassCastException expected) {
    }
  }

  public void testImmutableSortedSetCopyOfIterable() {
    Set<Object> original = ImmutableSet.of(new Object(), new Object());

    try {
      ImmutableSortedSet.copyOf(original);
      fail();
    } catch (ClassCastException expected) {
    }
  }

  public void testImmutableSortedSetCopyOfIterator() {
    Set<Object> original = ImmutableSet.of(new Object(), new Object());

    try {
      ImmutableSortedSet.copyOf(original.iterator());
      fail();
    } catch (ClassCastException expected) {
    }
  }

  private void doHasAllFauxveridesTest(Class<?> descendant, Class<?> ancestor) {
    Set<MethodSignature> required = getAllRequiredToFauxveride(ancestor);
    Set<MethodSignature> found = getAllFauxveridden(descendant, ancestor);
    Set<MethodSignature> missing = ImmutableSortedSet.copyOf(difference(required, found));
    if (!missing.isEmpty()) {
      fail(
          rootLocaleFormat(
              "%s should hide the public static methods declared in %s: %s",
              descendant.getSimpleName(), ancestor.getSimpleName(), missing));
    }
  }

  private static Set<MethodSignature> getAllRequiredToFauxveride(Class<?> ancestor) {
    return getPublicStaticMethodsBetween(ancestor, Object.class);
  }

  private static Set<MethodSignature> getAllFauxveridden(Class<?> descendant, Class<?> ancestor) {
    return getPublicStaticMethodsBetween(descendant, ancestor);
  }

  private static Set<MethodSignature> getPublicStaticMethodsBetween(
      Class<?> descendant, Class<?> ancestor) {
    Set<MethodSignature> methods = CollUtil.newHashSet();
    for (Class<?> clazz : getClassesBetween(descendant, ancestor)) {
      methods.addAll(getPublicStaticMethods(clazz));
    }
    return methods;
  }

  private static Set<MethodSignature> getPublicStaticMethods(Class<?> clazz) {
    Set<MethodSignature> publicStaticMethods =CollUtil.newHashSet();

    for (Method method : clazz.getDeclaredMethods()) {
      int modifiers = method.getModifiers();
      if (isPublic(modifiers) && isStatic(modifiers)) {
        publicStaticMethods.add(new MethodSignature(method));
      }
    }

    return publicStaticMethods;
  }

  /** [descendant, ancestor) */
  private static Set<Class<?>> getClassesBetween(Class<?> descendant, Class<?> ancestor) {
    Set<Class<?>> classes = CollUtil.newHashSet();

    while (!descendant.equals(ancestor)) {
      classes.add(descendant);
      descendant = descendant.getSuperclass();
    }

    return classes;
  }

  /**
   * Not really a signature -- just the parts that affect whether one method is a fauxveride of a
   * method from an ancestor class.
   *
   * <p>See JLS 8.4.2 for the definition of the related "override-equivalent."
   */
  private static final class MethodSignature implements Comparable<MethodSignature> {
    final String name;
    final List<Class<?>> parameterTypes;
    final TypeSignature typeSignature;

    MethodSignature(Method method) {
      name = method.getName();
      parameterTypes = Arrays.asList(method.getParameterTypes());
      typeSignature = new TypeSignature(method.getTypeParameters());
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof MethodSignature) {
        MethodSignature other = (MethodSignature) obj;
        return name.equals(other.name)
            && parameterTypes.equals(other.parameterTypes)
            && typeSignature.equals(other.typeSignature);
      }

      return false;
    }

    @Override
    public int hashCode() {
      return ObjectUtil.hashCode(name, parameterTypes, typeSignature);
    }

    @Override
    public String toString() {
      return rootLocaleFormat("%s%s(%s)", typeSignature, name, getTypesString(parameterTypes));
    }

    @Override
    public int compareTo(MethodSignature o) {
      return toString().compareTo(o.toString());
    }
  }

  private static final class TypeSignature {
    final List<TypeParameterSignature> parameterSignatures;

    TypeSignature(TypeVariable<Method>[] parameters) {
      parameterSignatures =
          transform(
              Arrays.asList(parameters),
              new Function<TypeVariable<?>, TypeParameterSignature>() {
                @Override
                public TypeParameterSignature apply(TypeVariable<?> from) {
                  return new TypeParameterSignature(from);
                }
              });
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof TypeSignature) {
        TypeSignature other = (TypeSignature) obj;
        return parameterSignatures.equals(other.parameterSignatures);
      }

      return false;
    }

    @Override
    public int hashCode() {
      return parameterSignatures.hashCode();
    }

    @Override
    public String toString() {
      return (parameterSignatures.isEmpty())
          ? ""
          : "<" + StrJoiner.of(", ").append(parameterSignatures) + "> ";
    }
  }

  private static final class TypeParameterSignature {
    final String name;
    final List<Type> bounds;

    TypeParameterSignature(TypeVariable<?> typeParameter) {
      name = typeParameter.getName();
      bounds = Arrays.asList(typeParameter.getBounds());
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof TypeParameterSignature) {
        TypeParameterSignature other = (TypeParameterSignature) obj;
        /*
         * The name is here only for display purposes; <E extends Number> and <T
         * extends Number> are equivalent.
         */
        return bounds.equals(other.bounds);
      }

      return false;
    }

    @Override
    public int hashCode() {
      return bounds.hashCode();
    }

    @Override
    public String toString() {
      return (bounds.equals(ImmutableList.of(Object.class)))
          ? name
          : name + " extends " + getTypesString(bounds);
    }
  }

  private static String getTypesString(List<? extends Type> types) {
    List<String> names = transform(types, SIMPLE_NAME_GETTER);
    return StrJoiner.of(", ").append(names).toString();
  }

  private static final Function<Type, String> SIMPLE_NAME_GETTER =
      new Function<Type, String>() {
        @Override
        public String apply(Type from) {
          if (from instanceof Class) {
            return ((Class<?>) from).getSimpleName();
          }
          return from.toString();
        }
      };

  private static String rootLocaleFormat(String format, Object... args) {
    return String.format(Locale.ROOT, format, args);
  }
}
