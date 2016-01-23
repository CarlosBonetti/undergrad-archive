package org.ine5426.lava;

import org.ine5426.lava.compiler.exceptions.RedeclaredClassException;
import org.ine5426.lava.compiler.exceptions.UndefinedTypeException;
import org.ine5426.lava.utils.SourceBuilder;
import org.junit.Test;

public class ClassTest extends LavaTest {
	// @formatter:off

	@Test
	public void testClassAlreadyDefined() throws Exception {
		expectedException.expect(RedeclaredClassException.class);
		expectedException.expectMessage("Test");
		expectedException.expectMessage("3:6");
		expectedException.expectMessage("already defined");
		
		Runner.compileAndRun(new SourceBuilder()
				.line("class Test:")
				.indent()
					.line("pass")
				.dedent()
				.line("class Test:")
				.indent()
					.line("pass")
				.dedent()
				.line("println(\"a\")")
				.toString());
	}
	
	@Test
	public void testNewOperator() {
		assertOutput(new SourceBuilder()
			.line("class Test:")
			.indent()
				.line("pass")
			.dedent()
			.line("new Test()")
			.toString(), "");
	}
	
	@Test
	public void testNewOperatorAssigment() {
		assertOutput(new SourceBuilder()
			.line("class Test:")
			.indent()
				.line("pass")
			.dedent()
			.line("Test a = new Test()")
			.toString(), "");
	}
	
	@Test
	public void testNewOperatorUndefinedType() throws Exception {
		expectedException.expect(UndefinedTypeException.class);
		expectedException.expectMessage("Test");
		expectedException.expectMessage("1:4");
		expectedException.expectMessage("not defined");
		
		Runner.compileAndRun("new Test()");
	}
	
	@Test
	public void testNewOperatorAssigmentUndefinedType() throws Exception {
		expectedException.expect(UndefinedTypeException.class);
		expectedException.expectMessage("Test");
		expectedException.expectMessage("1:0");
		expectedException.expectMessage("not defined");
		
		Runner.compileAndRun("Test a = new Test()");
	}
	
	@Test
	public void testMethodInvocation() {
		assertOutput(new SourceBuilder()
			.line("class Test:")
			.indent()
				.line("void a(string b):")
				.indent()
					.line("println(b)")
				.dedent()
			.dedent()
			.line("Test test = new Test()")			
			.line("test.a(\"yaay!\")")
			.toString(), "yaay!\n");
	}
	
	@Test
	public void testMethodInvocationAsExpression() {
		assertOutput(new SourceBuilder()
			.line("class Test:")
			.indent()
				.line("int add(int a, int b):")
				.indent()
					.line("return a + b")
				.dedent()
			.dedent()
			.line("Test test = new Test()")			
			.line("println(test.add(1, 2))")
			.toString(), "3\n");
	}
	
	@Test
	public void objectAsParameterAdnReturnTypes() {
		assertOutput(new SourceBuilder()
		.line("class Adder:")
		.indent()
			.line("int add(int a, int b):")
			.indent()
				.line("return a + b")
			.dedent()
		.dedent()
		.line("class Calc:")
		.indent()
			.line("int mult(int a, int b, Adder adder):")
			.indent()
				.line("return adder.add(a, b) * 10")
			.dedent()
		.dedent()
		.line("Calc calc = new Calc()")
		.line("Adder adder = new Adder()")
		.line("println(calc.mult(1, 2, adder))")
		.toString(), "30\n");
	}
	
	@Test
	public void testThisVariable() {
		assertOutput(new SourceBuilder()
			.line("class Test:")
			.indent()
				.line("int add(int a, int b):")
				.indent()
					.line("return a + b")
				.dedent()
				.line("int calc(int a, int b):")
				.indent()
					.line("return this.add(a, b) * 10")
				.dedent()
			.dedent()
			.line("Test test = new Test()")			
			.line("println(test.add(1, 2))")	
			.line("println(test.calc(1, 2))")
			.toString(), "3\n30\n");
	}
	
	// @formatter:on
}
