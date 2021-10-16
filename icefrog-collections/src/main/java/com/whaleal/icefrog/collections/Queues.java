

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;


import com.whaleal.icefrog.core.lang.Preconditions;
import java.util.*;
import java.util.concurrent.*;


/**
 * Static utility methods pertaining to {@link Queue} and {@link Deque} instances. Also see this
 * class's counterparts {@link Lists}, {@link Sets}, and {@link MapUtil}.
 *
 * @author Kurt Alfred Kluever
 * 
 */


public final class Queues {
  private Queues() {}

  // ArrayBlockingQueue

  /**
   * Creates an empty {@code ArrayBlockingQueue} with the given (fixed) capacity and nonfair access
   * policy.
   */
 // ArrayBlockingQueue
  public static <E> ArrayBlockingQueue<E> newArrayBlockingQueue(int capacity) {
    return new ArrayBlockingQueue<E>(capacity);
  }

  // ArrayDeque

  /**
   * Creates an empty {@code ArrayDeque}.
   *
   * 
   */
  public static <E> ArrayDeque<E> newArrayDeque() {
    return new ArrayDeque<E>();
  }

  /**
   * Creates an {@code ArrayDeque} containing the elements of the specified iterable, in the order
   * they are returned by the iterable's iterator.
   *
   * 
   */
  public static <E> ArrayDeque<E> newArrayDeque(Iterable<? extends E> elements) {
    if (elements instanceof Collection) {
      return new ArrayDeque<E>((Collection<? extends E>) elements);
    }
    ArrayDeque<E> deque = new ArrayDeque<E>();
    Iterables.addAll(deque, elements);
    return deque;
  }

  // ConcurrentLinkedQueue

  /** Creates an empty {@code ConcurrentLinkedQueue}. */
 // ConcurrentLinkedQueue
  public static <E> ConcurrentLinkedQueue<E> newConcurrentLinkedQueue() {
    return new ConcurrentLinkedQueue<E>();
  }

  /**
   * Creates a {@code ConcurrentLinkedQueue} containing the elements of the specified iterable, in
   * the order they are returned by the iterable's iterator.
   */
 // ConcurrentLinkedQueue
  public static <E> ConcurrentLinkedQueue<E> newConcurrentLinkedQueue(
      Iterable<? extends E> elements) {
    if (elements instanceof Collection) {
      return new ConcurrentLinkedQueue<E>((Collection<? extends E>) elements);
    }
    ConcurrentLinkedQueue<E> queue = new ConcurrentLinkedQueue<E>();
    Iterables.addAll(queue, elements);
    return queue;
  }

  // LinkedBlockingDeque

  /**
   * Creates an empty {@code LinkedBlockingDeque} with a capacity of {@link Integer#MAX_VALUE}.
   *
   * 
   */
 // LinkedBlockingDeque
  public static <E> LinkedBlockingDeque<E> newLinkedBlockingDeque() {
    return new LinkedBlockingDeque<E>();
  }

  /**
   * Creates an empty {@code LinkedBlockingDeque} with the given (fixed) capacity.
   *
   * @throws IllegalArgumentException if {@code capacity} is less than 1
   * 
   */
 // LinkedBlockingDeque
  public static <E> LinkedBlockingDeque<E> newLinkedBlockingDeque(int capacity) {
    return new LinkedBlockingDeque<E>(capacity);
  }

  /**
   * Creates a {@code LinkedBlockingDeque} with a capacity of {@link Integer#MAX_VALUE}, containing
   * the elements of the specified iterable, in the order they are returned by the iterable's
   * iterator.
   *
   * 
   */
 // LinkedBlockingDeque
  public static <E> LinkedBlockingDeque<E> newLinkedBlockingDeque(Iterable<? extends E> elements) {
    if (elements instanceof Collection) {
      return new LinkedBlockingDeque<E>((Collection<? extends E>) elements);
    }
    LinkedBlockingDeque<E> deque = new LinkedBlockingDeque<E>();
    Iterables.addAll(deque, elements);
    return deque;
  }

  // LinkedBlockingQueue

  /** Creates an empty {@code LinkedBlockingQueue} with a capacity of {@link Integer#MAX_VALUE}. */
 // LinkedBlockingQueue
  public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue() {
    return new LinkedBlockingQueue<E>();
  }

  /**
   * Creates an empty {@code LinkedBlockingQueue} with the given (fixed) capacity.
   *
   * @throws IllegalArgumentException if {@code capacity} is less than 1
   */
 // LinkedBlockingQueue
  public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue(int capacity) {
    return new LinkedBlockingQueue<E>(capacity);
  }

  /**
   * Creates a {@code LinkedBlockingQueue} with a capacity of {@link Integer#MAX_VALUE}, containing
   * the elements of the specified iterable, in the order they are returned by the iterable's
   * iterator.
   *
   * @param elements the elements that the queue should contain, in order
   * @return a new {@code LinkedBlockingQueue} containing those elements
   */
 // LinkedBlockingQueue
  public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue(Iterable<? extends E> elements) {
    if (elements instanceof Collection) {
      return new LinkedBlockingQueue<E>((Collection<? extends E>) elements);
    }
    LinkedBlockingQueue<E> queue = new LinkedBlockingQueue<E>();
    Iterables.addAll(queue, elements);
    return queue;
  }

  // LinkedList: see {@link com.google.common.collect.Lists}

  // PriorityBlockingQueue

  /**
   * Creates an empty {@code PriorityBlockingQueue} with the ordering given by its elements' natural
   * ordering.
   *
   * 
   *     in 15.0)
   */
 // PriorityBlockingQueue
  public static <E extends Comparable> PriorityBlockingQueue<E> newPriorityBlockingQueue() {
    return new PriorityBlockingQueue<E>();
  }

  /**
   * Creates a {@code PriorityBlockingQueue} containing the given elements.
   *
   * <p><b>Note:</b> If the specified iterable is a {@code SortedSet} or a {@code PriorityQueue},
   * this priority queue will be ordered according to the same ordering.
   *
   * 
   *     in 15.0)
   */
 // PriorityBlockingQueue
  public static <E extends Comparable> PriorityBlockingQueue<E> newPriorityBlockingQueue(
      Iterable<? extends E> elements) {
    if (elements instanceof Collection) {
      return new PriorityBlockingQueue<E>((Collection<? extends E>) elements);
    }
    PriorityBlockingQueue<E> queue = new PriorityBlockingQueue<E>();
    Iterables.addAll(queue, elements);
    return queue;
  }

  // PriorityQueue

  /**
   * Creates an empty {@code PriorityQueue} with the ordering given by its elements' natural
   * ordering.
   *
   * 
   *     in 15.0)
   */
  public static <E extends Comparable> PriorityQueue<E> newPriorityQueue() {
    return new PriorityQueue<E>();
  }

  /**
   * Creates a {@code PriorityQueue} containing the given elements.
   *
   * <p><b>Note:</b> If the specified iterable is a {@code SortedSet} or a {@code PriorityQueue},
   * this priority queue will be ordered according to the same ordering.
   *
   * 
   *     in 15.0)
   */
  public static <E extends Comparable> PriorityQueue<E> newPriorityQueue(
      Iterable<? extends E> elements) {
    if (elements instanceof Collection) {
      return new PriorityQueue<E>((Collection<? extends E>) elements);
    }
    PriorityQueue<E> queue = new PriorityQueue<E>();
    Iterables.addAll(queue, elements);
    return queue;
  }

  // SynchronousQueue

  /** Creates an empty {@code SynchronousQueue} with nonfair access policy. */
 // SynchronousQueue
  public static <E> SynchronousQueue<E> newSynchronousQueue() {
    return new SynchronousQueue<E>();
  }

  /**
   * Drains the queue as {@link BlockingQueue#drainTo(Collection, int)}, but if the requested {@code
   * numElements} elements are not available, it will wait for them up to the specified timeout.
   *
   * @param q the blocking queue to be drained
   * @param buffer where to add the transferred elements
   * @param numElements the number of elements to be waited for
   * @param timeout how long to wait before giving up
   * @return the number of elements transferred
   * @throws InterruptedException if interrupted while waiting
   * 
   */


 // BlockingQueue
  public static <E> int drain(
      BlockingQueue<E> q, Collection<? super E> buffer, int numElements, java.time.Duration timeout)
      throws InterruptedException {
    // TODO(b/126049426): Consider using saturateToNanos(timeout) instead.
    return drain(q, buffer, numElements, timeout.toNanos(), TimeUnit.NANOSECONDS);
  }

  /**
   * Drains the queue as {@link BlockingQueue#drainTo(Collection, int)}, but if the requested {@code
   * numElements} elements are not available, it will wait for them up to the specified timeout.
   *
   * @param q the blocking queue to be drained
   * @param buffer where to add the transferred elements
   * @param numElements the number of elements to be waited for
   * @param timeout how long to wait before giving up, in units of {@code unit}
   * @param unit a {@code TimeUnit} determining how to interpret the timeout parameter
   * @return the number of elements transferred
   * @throws InterruptedException if interrupted while waiting
   */


 // BlockingQueue
  @SuppressWarnings("GoodTime") // should accept a java.time.Duration
  public static <E> int drain(
      BlockingQueue<E> q,
      Collection<? super E> buffer,
      int numElements,
      long timeout,
      TimeUnit unit)
      throws InterruptedException {
    Preconditions.checkNotNull(buffer);
    /*
     * This code performs one System.nanoTime() more than necessary, and in return, the time to
     * execute Queue#drainTo is not added *on top* of waiting for the timeout (which could make
     * the timeout arbitrarily inaccurate, given a queue that is slow to drain).
     */
    long deadline = System.nanoTime() + unit.toNanos(timeout);
    int added = 0;
    while (added < numElements) {
      // we could rely solely on #poll, but #drainTo might be more efficient when there are multiple
      // elements already available (e.g. LinkedBlockingQueue#drainTo locks only once)
      added += q.drainTo(buffer, numElements - added);
      if (added < numElements) { // not enough elements immediately available; will have to poll
        E e = q.poll(deadline - System.nanoTime(), TimeUnit.NANOSECONDS);
        if (e == null) {
          break; // we already waited enough, and there are no more elements in sight
        }
        buffer.add(e);
        added++;
      }
    }
    return added;
  }

  /**
   * Drains the queue as, but with a
   * different behavior in case it is interrupted while waiting. In that case, the operation will
   * continue as usual, and in the end the thread's interruption status will be set (no {@code
   * InterruptedException} is thrown).
   *
   * @param q the blocking queue to be drained
   * @param buffer where to add the transferred elements
   * @param numElements the number of elements to be waited for
   * @param timeout how long to wait before giving up
   * @return the number of elements transferred
   * 
   */


 // BlockingQueue
  public static <E> int drainUninterruptibly(
      BlockingQueue<E> q,
      Collection<? super E> buffer,
      int numElements,
      java.time.Duration timeout) {
    // TODO(b/126049426): Consider using saturateToNanos(timeout) instead.
    return drainUninterruptibly(q, buffer, numElements, timeout.toNanos(), TimeUnit.NANOSECONDS);
  }

  /**
   * Drains the queue as {@linkplain #drain(BlockingQueue, Collection, int, long, TimeUnit)}, but
   * with a different behavior in case it is interrupted while waiting. In that case, the operation
   * will continue as usual, and in the end the thread's interruption status will be set (no {@code
   * InterruptedException} is thrown).
   *
   * @param q the blocking queue to be drained
   * @param buffer where to add the transferred elements
   * @param numElements the number of elements to be waited for
   * @param timeout how long to wait before giving up, in units of {@code unit}
   * @param unit a {@code TimeUnit} determining how to interpret the timeout parameter
   * @return the number of elements transferred
   */


 // BlockingQueue
  @SuppressWarnings("GoodTime") // should accept a java.time.Duration
  public static <E> int drainUninterruptibly(
      BlockingQueue<E> q,
      Collection<? super E> buffer,
      int numElements,
      long timeout,
      TimeUnit unit) {
    Preconditions.checkNotNull(buffer);
    long deadline = System.nanoTime() + unit.toNanos(timeout);
    int added = 0;
    boolean interrupted = false;
    try {
      while (added < numElements) {
        // we could rely solely on #poll, but #drainTo might be more efficient when there are
        // multiple elements already available (e.g. LinkedBlockingQueue#drainTo locks only once)
        added += q.drainTo(buffer, numElements - added);
        if (added < numElements) { // not enough elements immediately available; will have to poll
          E e; // written exactly once, by a successful (uninterrupted) invocation of #poll
          while (true) {
            try {
              e = q.poll(deadline - System.nanoTime(), TimeUnit.NANOSECONDS);
              break;
            } catch (InterruptedException ex) {
              interrupted = true; // note interruption and retry
            }
          }
          if (e == null) {
            break; // we already waited enough, and there are no more elements in sight
          }
          buffer.add(e);
          added++;
        }
      }
    } finally {
      if (interrupted) {
        Thread.currentThread().interrupt();
      }
    }
    return added;
  }

  /**
   * Returns a synchronized (thread-safe) queue backed by the specified queue. In order to guarantee
   * serial access, it is critical that <b>all</b> access to the backing queue is accomplished
   * through the returned queue.
   *
   * <p>It is imperative that the user manually synchronize on the returned queue when accessing the
   * queue's iterator:
   *
   * <pre>{@code
   * Queue<E> queue = Queues.synchronizedQueue(MinMaxPriorityQueue.<E>create());
   * ...
   * queue.add(element);  // Needn't be in synchronized block
   * ...
   * synchronized (queue) {  // Must synchronize on queue!
   *   Iterator<E> i = queue.iterator(); // Must be in synchronized block
   *   while (i.hasNext()) {
   *     foo(i.next());
   *   }
   * }
   * }</pre>
   *
   * <p>Failure to follow this advice may result in non-deterministic behavior.
   *
   * <p>The returned queue will be serializable if the specified queue is serializable.
   *
   * @param queue the queue to be wrapped in a synchronized view
   * @return a synchronized view of the specified queue
   * 
   */
  public static <E extends Object> Queue<E> synchronizedQueue(Queue<E> queue) {
    return Synchronized.queue(queue, null);
  }

  /**
   * Returns a synchronized (thread-safe) deque backed by the specified deque. In order to guarantee
   * serial access, it is critical that <b>all</b> access to the backing deque is accomplished
   * through the returned deque.
   *
   * <p>It is imperative that the user manually synchronize on the returned deque when accessing any
   * of the deque's iterators:
   *
   * <pre>{@code
   * Deque<E> deque = Queues.synchronizedDeque(Queues.<E>newArrayDeque());
   * ...
   * deque.add(element);  // Needn't be in synchronized block
   * ...
   * synchronized (deque) {  // Must synchronize on deque!
   *   Iterator<E> i = deque.iterator(); // Must be in synchronized block
   *   while (i.hasNext()) {
   *     foo(i.next());
   *   }
   * }
   * }</pre>
   *
   * <p>Failure to follow this advice may result in non-deterministic behavior.
   *
   * <p>The returned deque will be serializable if the specified deque is serializable.
   *
   * @param deque the deque to be wrapped in a synchronized view
   * @return a synchronized view of the specified deque
   * 
   */
  public static <E extends Object> Deque<E> synchronizedDeque(Deque<E> deque) {
    return Synchronized.deque(deque, null);
  }
}
