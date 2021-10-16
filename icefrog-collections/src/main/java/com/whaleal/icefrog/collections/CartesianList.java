

package com.whaleal.icefrog.collections;



import javax.annotation.CheckForNull;
import java.util.AbstractList;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

import static com.whaleal.icefrog.core.lang.Preconditions.checkElementIndex;
import static com.whaleal.icefrog.core.math.MathUtil.checkedMultiply;

/**
 * Implementation of {@link Lists#cartesianProduct(List)}.
 *
 * @author Louis Wasserman
 */


final class CartesianList<E> extends AbstractList<List<E>> implements RandomAccess {

  private final transient ImmutableList<List<E>> axes;
  private final transient int[] axesSizeProduct;

  static <E> List<List<E>> create(List<? extends List<? extends E>> lists) {
    ImmutableList.Builder<List<E>> axesBuilder = new ImmutableList.Builder<>(lists.size());
    for (List<? extends E> list : lists) {
      List<E> copy = ImmutableList.copyOf(list);
      if (copy.isEmpty()) {
        return ImmutableList.of();
      }
      axesBuilder.add(copy);
    }
    return new CartesianList<>(axesBuilder.build());
  }

  CartesianList(ImmutableList<List<E>> axes) {
    this.axes = axes;
    int[] axesSizeProduct = new int[axes.size() + 1];
    axesSizeProduct[axes.size()] = 1;
    try {
      for (int i = axes.size() - 1; i >= 0; i--) {
        axesSizeProduct[i] = checkedMultiply(axesSizeProduct[i + 1], axes.get(i).size());
      }
    } catch (ArithmeticException e) {
      throw new IllegalArgumentException(
          "Cartesian product too large; must have size at most Integer.MAX_VALUE");
    }
    this.axesSizeProduct = axesSizeProduct;
  }

  private int getAxisIndexForProductIndex(int index, int axis) {
    return (index / axesSizeProduct[axis + 1]) % axes.get(axis).size();
  }

  @Override
  public int indexOf(@CheckForNull Object o) {
    if (!(o instanceof List)) {
      return -1;
    }
    List<?> list = (List<?>) o;
    if (list.size() != axes.size()) {
      return -1;
    }
    ListIterator<?> itr = list.listIterator();
    int computedIndex = 0;
    while (itr.hasNext()) {
      int axisIndex = itr.nextIndex();
      int elemIndex = axes.get(axisIndex).indexOf(itr.next());
      if (elemIndex == -1) {
        return -1;
      }
      computedIndex += elemIndex * axesSizeProduct[axisIndex + 1];
    }
    return computedIndex;
  }

  @Override
  public int lastIndexOf(@CheckForNull Object o) {
    if (!(o instanceof List)) {
      return -1;
    }
    List<?> list = (List<?>) o;
    if (list.size() != axes.size()) {
      return -1;
    }
    ListIterator<?> itr = list.listIterator();
    int computedIndex = 0;
    while (itr.hasNext()) {
      int axisIndex = itr.nextIndex();
      int elemIndex = axes.get(axisIndex).lastIndexOf(itr.next());
      if (elemIndex == -1) {
        return -1;
      }
      computedIndex += elemIndex * axesSizeProduct[axisIndex + 1];
    }
    return computedIndex;
  }

  @Override
  public ImmutableList<E> get(int index) {
    checkElementIndex(index, size());
    return new ImmutableList<E>() {

      @Override
      public int size() {
        return axes.size();
      }

      @Override
      public E get(int axis) {
        checkElementIndex(axis, size());
        int axisIndex = getAxisIndexForProductIndex(index, axis);
        return axes.get(axis).get(axisIndex);
      }

      @Override
      boolean isPartialView() {
        return true;
      }
    };
  }

  @Override
  public int size() {
    return axesSizeProduct[0];
  }

  @Override
  public boolean contains(@CheckForNull Object object) {
    if (!(object instanceof List)) {
      return false;
    }
    List<?> list = (List<?>) object;
    if (list.size() != axes.size()) {
      return false;
    }
    int i = 0;
    for (Object o : list) {
      if (!axes.get(i).contains(o)) {
        return false;
      }
      i++;
    }
    return true;
  }
}
