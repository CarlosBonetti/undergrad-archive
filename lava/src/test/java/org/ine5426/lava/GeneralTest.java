package org.ine5426.lava;

import org.ine5426.lava.compiler.exceptions.IncompatibleExpressionArgumentException;
import org.ine5426.lava.compiler.exceptions.IncompatibleTypesException;
import org.ine5426.lava.compiler.exceptions.RedeclaredVariableException;
import org.ine5426.lava.compiler.exceptions.UndeclaredVariableException;
import org.ine5426.lava.utils.SourceBuilder;
import org.junit.Test;

public class GeneralTest extends LavaTest {

	// @formatter:off

	@Test
	public void testExpr() {
		// Factor (positive and negative numbers)
		assertOutput("println(-5)", "-5\n");
		assertOutput("println(+10)", "10\n");

		// Addition
		assertOutput("println(1+2)", "3\n");
		assertOutput("println(1+2+45)", "48\n");
		assertOutput("println(1+2+10+123)", "136\n");
		assertOutput("println(+1 + +4 + -5 + -0)", "0\n");

		// Subtraction
		assertOutput("println(10-4)", "6\n");
		assertOutput("println(1-16)", "-15\n");
		assertOutput("println(256-10-6)", "240\n");
		assertOutput("println(-5 - 1 - -3 - +4)", "-7\n");

		// Multiplication
		assertOutput("println(2*3)", "6\n");
		assertOutput("println(2*10*5)", "100\n");
		assertOutput("println(2*-1*+4)", "-8\n");
		assertOutput("println(-5*-4)", "20\n");

		// Division
		assertOutput("println(4/2)", "2\n");
		assertOutput("println(5/2)", "2\n"); // No float here (TODO?)
		assertOutput("println(10/2/5)", "1\n");
		assertOutput("println(+10 / -2)", "-5\n");

		// Precedence and mixed symbols
		assertOutput("println(2+3*5)", "17\n");
		assertOutput("println(8/4*2)", "4\n");
		assertOutput("println(2*8/4)", "4\n");
		assertOutput("println(2 + 2*4 - 4/4)", "9\n");
		assertOutput("println(2 * 4 + 2 / 2 - 4)", "5\n");

		// Subexpression (parentheses)
		assertOutput("println((2))", "2\n");
		assertOutput("println(2*(3+2))", "10\n");
		assertOutput("println((2+((4))) * (10-4) / (3*2-3))", "12\n");
	}

	@Test
	public void testMultipleLineInput() {
		assertOutput("println(1) \nprintln(2) \nprintln(3)", "1\n2\n3\n");
		assertOutput("\n\n\n\nprintln(4) \n\n\nprintln(5) \n\n\n", "4\n5\n");
	}

	@Test
	public void testDeclarationAndAssigment() {
		// Declaring, then assigning
		assertOutput("int a \nint b \nb = 67 \na = 3 \nprintln(a) \nprintln(b)", "3\n67\n");

		// Declaring and assigning at same time
		assertOutput("int a = 4 \nprintln(a)", "4\n");

		// Variables inside expressions
		assertOutput("int test = 98 \nprintln((test+2)/ 50 * 3)", "6\n");
		assertOutput("int a = 1 \nint b = 4 \nprintln(a+b)", "5\n");

		// Assigning expressions to variables
		assertOutput("int ab \nab = 3 * 4 \nprintln(ab)", "12\n");
		assertOutput("int lala = 2 * 4 \nprintln(lala)", "8\n");
		assertOutput("int lala = 2 * 4 + 2 / 2 - 4 \nprintln(lala)", "5\n");
	}

	@Test
	public void testUndeclaredVariable_atExpression() throws Exception {
		expectedException.expect(UndeclaredVariableException.class);
		expectedException.expectMessage("not_declared_var");
		expectedException.expectMessage("1:8");
		expectedException.expectMessage("undeclared");

		Runner.compileAndRun("println(not_declared_var)");
	}

	@Test
	public void testUndeclaredVariable_atAssigment() throws Exception {
		expectedException.expect(UndeclaredVariableException.class);
		expectedException.expectMessage("foo");
		expectedException.expectMessage("1:0");
		expectedException.expectMessage("undeclared");
		Runner.compileAndRun("foo = 56");
	}

	@Test
	public void testVariableRedeclaration() throws Exception {
		expectedException.expect(RedeclaredVariableException.class);
		expectedException.expectMessage("bar");
		expectedException.expectMessage("2:4");
		expectedException.expectMessage("already defined");
		Runner.compileAndRun("int bar \nint bar");
	}

	@Test
	public void testStringAsVariableAndExpression() {
		assertOutput("string a = \"test!\" \nprintln(a)", "test!\n");
		assertOutput(new SourceBuilder()
			.line("string a")
			.line("a = \"hey!\"")
			.line("a = \"ho!\"")
			.line("println(a)")
			.toString(), "ho!\n");
	}

	@Test
	public void testDeclarationCompatibility() throws Exception {
		expectedException.expect(IncompatibleTypesException.class);
		expectedException.expectMessage("a");
		expectedException.expectMessage("1:4");
		expectedException.expectMessage("not compatible");
		Runner.compileAndRun("int a = \"test\"");
	}

	@Test
	public void testAssignmentCompatibility() throws Exception {
		expectedException.expect(IncompatibleTypesException.class);
		expectedException.expectMessage("boo");
		expectedException.expectMessage("2:0");
		expectedException.expectMessage("not compatible");
		Runner.compileAndRun("string boo \nboo = 3");
	}

	@Test
	public void testBooleanExpression() {
		assertOutput(new SourceBuilder()
			.line("if (false) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("else :")
			.indent()
				.line("println(\"ho!\")")
			.dedent()
			.toString(), "ho!\n");

		assertOutput(new SourceBuilder()
			.line("if (true) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("else :")
			.indent()
				.line("println(\"ho!\")")
			.dedent()
			.toString(), "hey!\n");
	}

	// @Test
	// public void testPrintWithBoolean() {
	// assertOutput("println(false)", "false\n");
	// }
	//
	// @Test
	// public void testBooleanAsVariableAndExpression() {
	// assertOutput("bool a = false\nprintln(a)", "false\n");
	// assertOutput(new SourceBuilder()
	// .line("bool a")
	// .line("a = false")
	// .line("a = true")
	// .line("println(a)")
	// .toString(), "true\n");
	// }
	//

	@Test
	public void testIfElseCondition() {
		assertOutput(new SourceBuilder()
			.line("if (false) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("else :")
			.indent()
				.line("println(\"ho!\")")
			.dedent()
			.toString(), "ho!\n");

		assertOutput(new SourceBuilder()
			.line("if (true) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("else :")
			.indent()
				.line("println(\"ho!\")")
			.dedent()
			.toString(), "hey!\n");
		
		assertOutput(new SourceBuilder()
		.line("if(false):")
		.indent()
			.line("println(\"first -if\")")
			.line("if(false):")
			.indent()
				.line("println(\"second -if\")")
			.dedent()
			.line("else:")
			.indent()
				.line("println(\"second -else\")")
			.dedent()
		.dedent()
		.line("else:")
		.indent()
			.line("println(\"first -else\")")
		.dedent()
		.line("if(false):")
		.indent()
			.line("println(\"third -if\")")
		.dedent()
		.line("else:")
		.indent()
			.line("println(\"second -elseqwe\")")
		.dedent()
		.line("println(\"end program\")").toString(),
		"first -else\nsecond -elseqwe\nend program\n");
	}
	
	@Test
	public void testIfCondition() {
		assertOutput(new SourceBuilder()
			.line("if (false) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "end\n");

		assertOutput(new SourceBuilder()
			.line("if (true) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "hey!\nend\n");
	}

	@Test
	public void testIfElseConditionCompatibility() throws Exception {
		String src = new SourceBuilder()
			.line("if (\"something\") :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("else :")
			.indent()
				.line("println(\"ho!\")")
			.dedent()
			.toString();

		expectedException.expect(IncompatibleExpressionArgumentException.class);
		expectedException.expectMessage("something");
		expectedException.expectMessage("1:4");
		expectedException.expectMessage("is not compatible with expression type");
		Runner.compileAndRun(src);
	}
	
	@Test
	public void testArithmeticCompatibility() throws Exception {
		String src = new SourceBuilder()
			.line("int a = 1 + false")
			.line("println(a)")
			.toString();

		expectedException.expect(IncompatibleExpressionArgumentException.class);
		expectedException.expectMessage("false");
		expectedException.expectMessage("1:12");
		expectedException.expectMessage("is not compatible with expression type");
		Runner.compileAndRun(src);
		
		src = new SourceBuilder()
			.line("int a = false + 1")
			.line("println(a)")
			.toString();

		expectedException.expect(IncompatibleExpressionArgumentException.class);
		expectedException.expectMessage("false");
		expectedException.expectMessage("1:9");
		expectedException.expectMessage("is not compatible with expression type");
		Runner.compileAndRun(src);
		
		src = new SourceBuilder()
			.line("int a = 1 + \"wasd\"")
			.line("println(a)")
			.toString();

		expectedException.expect(IncompatibleExpressionArgumentException.class);
		expectedException.expectMessage("\"wasd\"");
		expectedException.expectMessage("1:12");
		expectedException.expectMessage("is not compatible with expression type");
		Runner.compileAndRun(src);
		
		src = new SourceBuilder()
			.line("int a = \"wasd\" + 1")
			.line("println(a)")
			.toString();
		
		expectedException.expect(IncompatibleExpressionArgumentException.class);
		expectedException.expectMessage("\"wasd\"");
		expectedException.expectMessage("1:9");
		expectedException.expectMessage("is not compatible with expression type");
		Runner.compileAndRun(src);
	}
	
	@Test
	public void testEqualConditionExpr() {
		assertOutput(new SourceBuilder()
			.line("if (2 == 1 ) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "end\n");

		assertOutput(new SourceBuilder()
			.line("if (1 == 1) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "hey!\nend\n");

		assertOutput(new SourceBuilder()
		.line("if (1 == -1 ) :")
		.indent()
			.line("println(\"hey!\")")
		.dedent()
		.line("println(\"end\")")
		.toString(), "end\n");

		assertOutput(new SourceBuilder()
			.line("if (-1 == -1) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "hey!\nend\n");
	}
	@Test
	public void testNotEqualConditionExpr() {
		assertOutput(new SourceBuilder()
			.line("if (1 != 1 ) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "end\n");

		assertOutput(new SourceBuilder()
			.line("if (2 != 1) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "hey!\nend\n");

		assertOutput(new SourceBuilder()
		.line("if (-1 != -1) :")
		.indent()
			.line("println(\"hey!\")")
		.dedent()
		.line("println(\"end\")")
		.toString(), "end\n");

		assertOutput(new SourceBuilder()
			.line("if (1 != -1 ) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "hey!\nend\n");
	}

	@Test
	public void testLessThanConditionExpr() {
		assertOutput(new SourceBuilder()
			.line("if (1 < 1 ) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "end\n");

		assertOutput(new SourceBuilder()
			.line("if (2 < 1) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "end\n");

		assertOutput(new SourceBuilder()
		.line("if (-1 < 1) :")
		.indent()
			.line("println(\"hey!\")")
		.dedent()
		.line("println(\"end\")")
		.toString(), "hey!\nend\n");

		assertOutput(new SourceBuilder()
			.line("if (1 < 3 ) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "hey!\nend\n");
	}
	
	@Test
	public void testLessThanEqualConditionExpr() {
		assertOutput(new SourceBuilder()
			.line("if (2 <= 1 ) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "end\n");

		assertOutput(new SourceBuilder()
			.line("if (1 <= 1) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "hey!\nend\n");

		assertOutput(new SourceBuilder()
		.line("if (1 <= -1 ) :")
		.indent()
			.line("println(\"hey!\")")
		.dedent()
		.line("println(\"end\")")
		.toString(), "end\n");

		assertOutput(new SourceBuilder()
			.line("if (-1 <= -1) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "hey!\nend\n");
	}
	
	@Test
	public void testGreaterThanConditionExpr() {
		assertOutput(new SourceBuilder()
			.line("if (2 > 1 ) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "hey!\nend\n");

		assertOutput(new SourceBuilder()
			.line("if (1 > 1) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "end\n");

		assertOutput(new SourceBuilder()
		.line("if (-3 > 5 ) :")
		.indent()
			.line("println(\"hey!\")")
		.dedent()
		.line("println(\"end\")")
		.toString(), "end\n");

		assertOutput(new SourceBuilder()
			.line("if (1 > -1) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "hey!\nend\n");
	}

	
	@Test
	public void testGreaterThanEqualConditionExpr() {
		assertOutput(new SourceBuilder()
			.line("if (2 >= 1 ) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "hey!\nend\n");

		assertOutput(new SourceBuilder()
			.line("if (1 >= 1) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "hey!\nend\n");

		assertOutput(new SourceBuilder()
		.line("if (-3 >= 5 ) :")
		.indent()
			.line("println(\"hey!\")")
		.dedent()
		.line("println(\"end\")")
		.toString(), "end\n");

		assertOutput(new SourceBuilder()
			.line("if (1 >= -1) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "hey!\nend\n");
	}
	
	@Test
	public void testWhileConditionExpr() {
		assertOutput(new SourceBuilder()
			.line("int i = 0")
			.line("while (i < 6 ) :")
			.indent()
				.line("println(i)")
				.line("i = i + 1")
			.dedent()
			.line("println(\"end\")")
			.toString(), "0\n1\n2\n3\n4\n5\nend\n");

		assertOutput(new SourceBuilder()
		.line("int i = 0")
		.line("while (6 > i ) :")
		.indent()
			.line("println(i)")
			.line("i = i + 1")
		.dedent()
		.line("println(\"end\")")
		.toString(), "0\n1\n2\n3\n4\n5\nend\n");

		assertOutput(new SourceBuilder()
		.line("int i = 6")
		.line("while (6 > i ) :")
		.indent()
			.line("println(i)")
			.line("i = i + 1")
		.dedent()
		.line("println(\"end\")")
		.toString(), "end\n");

		assertOutput(new SourceBuilder()
		.line("int i = 6")
		.line("while (false) :")
		.indent()
			.line("println(i)")
			.line("i = i + 1")
		.dedent()
		.line("println(\"end\")")
		.toString(), "end\n");
	}
	
	@Test
	public void testAndConditionExpr() {
		assertOutput(new SourceBuilder()
			.line("if (2 >= 1 and true) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "hey!\nend\n");

		assertOutput(new SourceBuilder()
			.line("if (1 >= 1 and true) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "hey!\nend\n");

		assertOutput(new SourceBuilder()
		.line("if (-3 >= 5 and false) :")
		.indent()
			.line("println(\"hey!\")")
		.dedent()
		.line("println(\"end\")")
		.toString(), "end\n");

		assertOutput(new SourceBuilder()
			.line("if (true and true) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "hey!\nend\n");
	}
	
	@Test
	public void testOrConditionExpr() {
		assertOutput(new SourceBuilder()
			.line("if (2 <= 1 or false) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "end\n");

		assertOutput(new SourceBuilder()
			.line("if (1 <= 1 or false) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "hey!\nend\n");

		assertOutput(new SourceBuilder()
		.line("if (1 <= -1 or false) :")
		.indent()
			.line("println(\"hey!\")")
		.dedent()
		.line("println(\"end\")")
		.toString(), "end\n");

		assertOutput(new SourceBuilder()
			.line("if (false or true) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "hey!\nend\n");
	}
	
	@Test
	public void testNotConditionExpr() {
		assertOutput(new SourceBuilder()
			.line("if (!(2 <= 1 or false)) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "hey!\nend\n");

		assertOutput(new SourceBuilder()
			.line("if (!(1 <= 1 and !false)) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "end\n");

		assertOutput(new SourceBuilder()
		.line("if (1 <= -1 and !false) :")
		.indent()
			.line("println(\"hey!\")")
		.dedent()
		.line("println(\"end\")")
		.toString(), "end\n");

		assertOutput(new SourceBuilder()
			.line("if (!false and !false) :")
			.indent()
				.line("println(\"hey!\")")
			.dedent()
			.line("println(\"end\")")
			.toString(), "hey!\nend\n");
	}
	
	@Test
	public void testWhileAndOrConditionExpr() {
		assertOutput(new SourceBuilder()
			.line("int i = 0")
			.line("while (i < 6 and !false) :")
			.indent()
				.line("println(i)")
				.line("i = i + 1")
			.dedent()
			.line("println(\"end\")")
			.toString(), "0\n1\n2\n3\n4\n5\nend\n");

		assertOutput(new SourceBuilder()
		.line("int i = 0")
		.line("while (6 > i or false) :")
		.indent()
			.line("println(i)")
			.line("i = i + 1")
		.dedent()
		.line("println(\"end\")")
		.toString(), "0\n1\n2\n3\n4\n5\nend\n");

		assertOutput(new SourceBuilder()
		.line("int i = 6")
		.line("while (6 > i and true) :")
		.indent()
			.line("println(i)")
			.line("i = i + 1")
		.dedent()
		.line("println(\"end\")")
		.toString(), "end\n");

		assertOutput(new SourceBuilder()
		.line("int i = 6")
		.line("while (!true or false) :")
		.indent()
			.line("println(i)")
			.line("i = i + 1")
		.dedent()
		.line("println(\"end\")")
		.toString(), "end\n");
	}
}
