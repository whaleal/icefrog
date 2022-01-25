

package com.whaleal.icefrog.collections;


import com.whaleal.icefrog.core.util.ObjectUtil;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.whaleal.icefrog.core.lang.Precondition.checkArgument;


/**
 * Unit test for {@link AbstractMultiset}.
 *
 *
 * 
 */
@SuppressWarnings("serial") // No serialization is used in this test

public class SimpleAbstractMultisetTest extends TestCase {

  @Test
  public void test(){
  }

  public void testFastAddAllMultiset() {
    final AtomicInteger addCalls = new AtomicInteger();
    Multiset<String> multiset =
        new NoRemoveMultiset<String>() {
          @Override
          public int add(String element, int occurrences) {
            addCalls.incrementAndGet();
            return super.add(element, occurrences);
          }
        };
    ImmutableMultiset<String> adds =
        new ImmutableMultiset.Builder<String>().addCopies("x", 10).build();
    multiset.addAll(adds);
    assertEquals(1, addCalls.get());
  }

  public void testRemoveUnsupported() {
    Multiset<String> multiset = new NoRemoveMultiset<>();
    multiset.add("a");
    try {
      multiset.remove("a");
      fail();
    } catch (UnsupportedOperationException expected) {
    }
    assertTrue(multiset.contains("a"));
  }

  private static class NoRemoveMultiset<E> extends AbstractMultiset<E> implements Serializable {
    final Map<E, Integer> backingMap = MapUtil.newHashMap();

    @Override
    public int size() {
      return Multisets.linearTimeSizeImpl(this);
    }

    @Override
    public void clear() {
      throw new UnsupportedOperationException();
    }

    @Override
    public int count(Object element) {
      for (Entry<E> entry : entrySet()) {
        if (ObjectUtil.equal(entry.getElement(), element)) {
          return entry.getCount();
        }
      }
      return 0;
    }

    @Override
    public int add(E element, int occurrences) {
      checkArgument(occurrences >= 0);
      Integer frequency = backingMap.get(element);
      if (frequency == null) {
        frequency = 0;
      }
      if (occurrences == 0) {
        return frequency;
      }
      checkArgument(occurrences <= Integer.MAX_VALUE - frequency);
      backingMap.put(element, frequency + occurrences);
      return frequency;
    }

    @Override
    Iterator<E> elementIterator() {
      return Multisets.elementIterator(entryIterator());
    }

    @Override
    Iterator<Entry<E>> entryIterator() {
      final Iterator<Map.Entry<E, Integer>> backingEntries = backingMap.entrySet().iterator();
      return new UnmodifiableIterator<Entry<E>>() {
        @Override
        public boolean hasNext() {
          return backingEntries.hasNext();
        }

        @Override
        public Entry<E> next() {
          final Map.Entry<E, Integer> mapEntry = backingEntries.next();
          return new Multisets.AbstractEntry<E>() {
            @Override
            public E getElement() {
              return mapEntry.getKey();
            }

            @Override
            public int getCount() {
              Integer frequency = backingMap.get(getElement());
              return (frequency == null) ? 0 : frequency;
            }
          };
        }
      };
    }

    @Override
    public Iterator<E> iterator() {
      return Multisets.iteratorImpl(this);
    }

    @Override
    int distinctElements() {
      return backingMap.size();
    }
  }
}
