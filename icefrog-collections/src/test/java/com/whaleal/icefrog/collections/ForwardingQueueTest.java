

package com.whaleal.icefrog.collections;







import java.util.Collection;
import java.util.Queue;

import com.whaleal.icefrog.collections.ForwardingQueue;
import com.whaleal.icefrog.collections.Lists;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests for {@code ForwardingQueue}.
 *
 * @author Robert Konigsberg
 * 
 */
public class ForwardingQueueTest extends TestCase {

  static final class StandardImplForwardingQueue<T> extends ForwardingQueue<T> {
    private final Queue<T> backingQueue;

    StandardImplForwardingQueue(Queue<T> backingQueue) {
      this.backingQueue = backingQueue;
    }

    @Override
    protected Queue<T> delegate() {
      return backingQueue;
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
      return standardAddAll(collection);
    }

    @Override
    public void clear() {
      standardClear();
    }

    @Override
    public boolean contains(Object object) {
      return standardContains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
      return standardContainsAll(collection);
    }

    @Override
    public boolean remove(Object object) {
      return standardRemove(object);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
      return standardRemoveAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
      return standardRetainAll(collection);
    }

    @Override
    public Object[] toArray() {
      return standardToArray();
    }

    @Override
    public <T> T[] toArray(T[] array) {
      return standardToArray(array);
    }

    @Override
    public String toString() {
      return standardToString();
    }

    @Override
    public boolean offer(T o) {
      return standardOffer(o);
    }

    @Override
    public T peek() {
      return standardPeek();
    }

    @Override
    public T poll() {
      return standardPoll();
    }
  }


  private static <T> Queue<T> wrap(final Queue<T> delegate) {
    return new ForwardingQueue<T>() {
      @Override
      protected Queue<T> delegate() {
        return delegate;
      }
    };
  }
}
