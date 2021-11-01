

package com.whaleal.icefrog.collections;


import junit.framework.TestCase;
import org.junit.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Tests for {@link UnmodifiableIterator}.
 *
 *
 */

public class UnmodifiableIteratorTest extends TestCase {
    @Test
    public void test(){
    }

  @SuppressWarnings("DoNotCall")
  public void testRemove() {
    final String[] array = {"a", "b", "c"};

    Iterator<String> iterator =
        new UnmodifiableIterator<String>() {
          int i;

          @Override
          public boolean hasNext() {
            return i < array.length;
          }

          @Override
          public String next() {
            if (!hasNext()) {
              throw new NoSuchElementException();
            }
            return array[i++];
          }
        };

    assertTrue(iterator.hasNext());
    assertEquals("a", iterator.next());
    try {
      iterator.remove();
      fail();
    } catch (UnsupportedOperationException expected) {
    }
  }
}
