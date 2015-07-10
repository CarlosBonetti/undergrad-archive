package com.bonaguiar.formais2.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

public class Assert {
	public static <T> void assertCollectionEquals(Collection<T> expected, Collection<T> actual) {
		try {
			assertTrue(expected.containsAll(actual));
			assertTrue(actual.containsAll(expected));
		} catch (AssertionError e) {
			throw new AssertionError("Collections diverge. Expected <" + expected + "> but was <" + actual + ">");
		}

		try {
			assertEquals(expected.size(), actual.size());
		} catch (AssertionError e) {
			throw new AssertionError("Collection sizes diverge. Expected <" + expected + "> but was <" + actual + ">");
		}
	}
}
