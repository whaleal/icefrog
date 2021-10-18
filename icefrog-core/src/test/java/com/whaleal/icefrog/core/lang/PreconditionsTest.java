package com.whaleal.icefrog.core.lang;

import org.junit.Test;

public class AssertTest {

	@Test
	public void isNullTest() {
		String a = null;
		Preconditions.isNull(a);
	}

	@Test
	public void notNullTest() {
		String a = null;
		Preconditions.isNull(a);
	}

	@Test(expected = IllegalArgumentException.class)
	public void isTrueTest() {
		int i = 0;
		//noinspection ConstantConditions
		Preconditions.isTrue(i > 0, IllegalArgumentException::new);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void isTrueTest2() {
		int i = -1;
		//noinspection ConstantConditions
		Preconditions.isTrue(i >= 0, IndexOutOfBoundsException::new);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void isTrueTest3() {
		int i = -1;
		//noinspection ConstantConditions
		Preconditions.isTrue(i > 0, () -> new IndexOutOfBoundsException("relation message to return"));
	}
}
