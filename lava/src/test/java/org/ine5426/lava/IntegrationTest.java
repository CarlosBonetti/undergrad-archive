package org.ine5426.lava;

import org.ine5426.lava.utils.SourceBuilder;
import org.junit.Test;

public class IntegrationTest extends LavaTest {
	// @formatter:off
	
	@Test
	public void testRecursiveFunctionCall() {
		assertOutput(new SourceBuilder()
		.line("int fibonacci(int n):")
		.indent()
			.line("if (n == 0):")
			.indent()
				.line("return 0")
			.dedent()
			.line("if (n == 1):")
			.indent()
				.line("return 1")
			.dedent()
			.line("return fibonacci(n - 1) + fibonacci(n - 2)")
		.dedent()
		.line("println(fibonacci(2))")		
		.line("println(fibonacci(10))")
		.toString(), "1\n55\n");
	}
	
	// @formatter:on
}
