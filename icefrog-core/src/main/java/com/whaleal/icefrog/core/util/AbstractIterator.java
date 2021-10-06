package com.whaleal.icefrog.core.util;


import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.whaleal.icefrog.core.util.Preconditions.checkState;

/**
 * Note this class is a copy of {@link AbstractIterator} (for dependency
 * reasons).
 */
abstract class AbstractIterator<T extends Object> implements Iterator<T> {
	private State state = State.NOT_READY;
	private T next;

	protected AbstractIterator() {
	}

	protected abstract T computeNext();

	protected final T endOfData() {
		state = State.DONE;
		return null;
	}

	@Override
	public final boolean hasNext() {
		checkState(state != State.FAILED);
		switch (state) {
			case DONE:
				return false;
			case READY:
				return true;
			default:
		}
		return tryToComputeNext();
	}

	private boolean tryToComputeNext() {
		state = State.FAILED; // temporary pessimism
		next = computeNext();
		if (state != State.DONE) {
			state = State.READY;
			return true;
		}
		return false;
	}

	@Override
	public final T next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		state = State.NOT_READY;
		// Safe because hasNext() ensures that tryToComputeNext() has put a T into `next`.
		T result = next;
		next = null;
		return result;
	}

	@Override
	public final void remove() {
		throw new UnsupportedOperationException();
	}

	private enum State {
		READY,
		NOT_READY,
		DONE,
		FAILED,
	}
}
