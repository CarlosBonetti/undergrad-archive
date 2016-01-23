package org.ine5426.lava.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SourceBuilderTest {

	@Test
	public void testLine() {
		SourceBuilder builder = new SourceBuilder();
		builder.line("first line");
		assertEquals("first line" + System.lineSeparator(), builder.toString());

		builder.line("")
				.line("third line");

		assertEquals("first line" + System.lineSeparator()
				+ System.lineSeparator()
				+ "third line" + System.lineSeparator(), builder.toString());
	}

	@Test
	public void testIndent() {
		SourceBuilder builder = new SourceBuilder()
		.indent()
		.line("Test");

		assertEquals("\tTest" + System.lineSeparator(), builder.toString());

		builder.indent().indent().line("Test 2");
		assertEquals("\tTest" + System.lineSeparator()
				+ "\t\t\tTest 2" + System.lineSeparator(), builder.toString());
	}

	@Test
	public void testDedent() {
		SourceBuilder builder = new SourceBuilder()
		.indent()
				.line("Test 1")
		.dedent()
		.line("Test 2");

		assertEquals("\tTest 1" + System.lineSeparator() +
				"Test 2" + System.lineSeparator(), builder.toString());

		builder = new SourceBuilder().dedent().dedent().dedent()
				.line("Test 1")
				.indent()
				.line("Test 2");

		assertEquals("Test 1" + System.lineSeparator() +
				"\tTest 2" + System.lineSeparator(), builder.toString());
	}

}
