package org.ine5426.lava;

import org.ine5426.lava.compiler.exceptions.IncompatibleArgumentException;
import org.ine5426.lava.compiler.exceptions.LavaCompilerException;
import org.ine5426.lava.compiler.exceptions.RedeclaredFunctionException;
import org.ine5426.lava.compiler.exceptions.ReturnStatementException;
import org.ine5426.lava.compiler.exceptions.UndefinedFunctionException;
import org.ine5426.lava.utils.SourceBuilder;
import org.junit.Test;

public class FunctionTest extends LavaTest {

	@Test
	public void testPrintln() {
		assertOutput("println(23)", "23\n");
	}

	@Test
	public void testPrintWithString() {
		assertOutput("println(\"Testing!\")", "Testing!\n");
	}

	// @formatter:off

	@Test
	public void testFunction() {
		String source = new SourceBuilder()
			.line("int test():")
			.indent()
				.line("return 2 * 4")
			.dedent()
			.line("println(test())")
			.toString();

		assertOutput(source, "8\n");

		// Different scopes:
		source = new SourceBuilder()
			.line("int test_with_var():")
			.indent()
				.line("int a = 4 / 2 + 1")
				.line("return a")
			.dedent()
			.line("println(test_with_var())")
			.line("int a = 28")
			.line("println(a)")
			.toString();

		assertOutput(source, "3\n28\n");

		// Arguments:
		source = new SourceBuilder()
			.line("int add(int a, int b):")
			.indent()
				.line("return a + b")
			.dedent()
			.line("println(add(1, 2))")
			.line("println(add(-10, 5))")
			.toString();

		assertOutput(source, "3\n-5\n");
	}	
	
	@Test
	public void testVoidFunction() {
		assertOutput(new SourceBuilder()
			.line("void a():")
			.indent()
				.line("return")
			.dedent()
			.line("a()")
			.toString(), "");
	}
	
	@Test
	public void testPassFunction() {
		assertOutput(new SourceBuilder()
			.line("void a():")
			.indent()
				.line("pass")
			.dedent()
			.line("a()")
			.toString(), "");
	}
	
	@Test
	public void testSharedScopes() {
		/*assertOutput(new SourceBuilder()
			.line("int foo = 42")
			.line("int a(int b):")
			.indent()
				.line("return foo + b")
			.dedent()
			.line("a(2)")
			.line("println(a(2))")
			.toString(), "44\n");
		
		assertOutput(new SourceBuilder()
			.line("int foo = 42")
			.line("int a(int foo):")
			.indent()
				.line("return foo")
			.dedent()
			.line("a(2)")
			.line("println(a(2))")
			.toString(), "2\n"); */
	}
	
	@Test
	public void testUndefinedFunction() throws Exception {
		expectedException.expect(UndefinedFunctionException.class);
		expectedException.expectMessage("foo");
		expectedException.expectMessage("1:0");
		expectedException.expectMessage("undefined function");
		Runner.compileAndRun("foo()");
	}
	
	@Test
	public void testRedeclaredFunctionWithDifferentSignature() throws Exception {		
		String source = new SourceBuilder()
			.line("int bar():")
			.indent()
				.line("return 42")
			.dedent()
			.line("string bar(string a):")
			.indent()
				.line("return a")
			.dedent()
			.line("println(bar())")
			.line("println(bar(\"test\"))")
			.toString();
	
		assertOutput(source, "42\ntest\n");
	}
	
	@Test
	public void testRedeclaredFunctionWithSameSignature() throws Exception {		
		String source = new SourceBuilder()
			.line("int bar():")
			.indent()
				.line("return 42")
			.dedent()
			.line("int bar():")
			.indent()
				.line("return 4")
			.dedent()
			.toString();
		
		expectedException.expect(RedeclaredFunctionException.class);
		expectedException.expectMessage("bar");
		expectedException.expectMessage("3:4");
		expectedException.expectMessage("already defined");
		Runner.compileAndRun(source);
	}
	
	@Test
	public void testFunctionCallWrongNumberOfArguments() throws Exception {
		String src = new SourceBuilder()
			.line("int foo(int c):")
			.indent()
				.line("return 1")
			.dedent()
			.line("foo()")
			.toString();
		
		// TODO: show more details at exception (wrong number of arguments)
		expectedException.expect(UndefinedFunctionException.class);
		expectedException.expectMessage("foo");
		expectedException.expectMessage("3:0");
		expectedException.expectMessage("undefined");
		Runner.compileAndRun(src);
	}
	
	@Test
	public void testFunctionDefinitionInsideAnotherFunction() throws Exception {
		String src = new SourceBuilder()
			.line("int func_a():")
			.indent()
				.line("int func_b():")
				.indent()
					.line("return 1")
				.dedent()
				.line("return 0")
			.dedent()
			.toString();
		
		expectedException.expect(LavaCompilerException.class);
		Runner.compileAndRun(src);
	}
	
	@Test
	public void testReturnOutsideFunction() throws Exception {
		expectedException.expect(ReturnStatementException.class);
		expectedException.expectMessage("allowed");
		Runner.compileAndRun("return 1");
	}	
	
	@Test
	public void testFunctionCallTypeCompatibility() throws Exception {
		String src = new SourceBuilder()
			.line("int foo(int c):")
			.indent()
				.line("return 1")
			.dedent()
			.line("foo(\"you shall not pass\")")
			.toString();
		
		expectedException.expect(IncompatibleArgumentException.class);
		expectedException.expectMessage("foo");
		expectedException.expectMessage("3:4");
		expectedException.expectMessage("not compatible");		
		Runner.compileAndRun(src);
	}
	
	@Test
	public void testReturnTypeCompatibility1() throws Exception {
		expectedException.expect(ReturnStatementException.class);
		expectedException.expectMessage("INT");
		expectedException.expectMessage("STRING");
		expectedException.expectMessage("bar");
		
		Runner.compileAndRun(new SourceBuilder()
			.line("int bar():")
			.indent()
				.line("return \"not a int\"")
			.dedent()
			.toString());		
	}	
	
	@Test
	public void testReturnTypeCompatibility2() throws Exception {
		expectedException.expect(ReturnStatementException.class);
		expectedException.expectMessage("STRING");
		expectedException.expectMessage("INT");
		expectedException.expectMessage("foo");
		
		Runner.compileAndRun(new SourceBuilder()
			.line("string foo():")
			.indent()
				.line("return 4 * 2")
			.dedent()
			.toString());		
	}
	
	@Test
	public void testReturnTypeCompatibilityWithVoid() throws Exception {
		expectedException.expect(ReturnStatementException.class);
		expectedException.expectMessage("VOID");
		expectedException.expectMessage("INT");
		expectedException.expectMessage("baz");
		
		Runner.compileAndRun(new SourceBuilder()
			.line("void baz():")
			.indent()
				.line("return 10 / 2")
			.dedent()
			.toString());		
	}
	
	@Test
	public void testIfReturnExists() throws Exception {
		expectedException.expect(ReturnStatementException.class);
		// TODO: better error message?
		
		Runner.compileAndRun(new SourceBuilder()
			.line("int foo():")
			.indent()
				.line("int a = 3")
			.dedent()
			.toString());		
	}
	
	@Test
	public void testFunctionArgumentOrderAndCompativility() throws Exception {
		String src = new SourceBuilder()
			.line("int square(int a, string msg):")
			.indent()
				.line("println(msg)")
				.line("return a * a")
			.dedent()
			.line("println(square(3, \"Calculating...\"))")
			.toString();
		
		assertOutput(src, "Calculating...\n9\n");
	}
	
	
	// @formatter:on
}
