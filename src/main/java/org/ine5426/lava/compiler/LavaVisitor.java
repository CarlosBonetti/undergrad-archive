package org.ine5426.lava.compiler;

import java.util.Stack;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.ine5426.lava.compiler.exceptions.IncompatibleArgumentException;
import org.ine5426.lava.compiler.exceptions.IncompatibleExpressionArgumentException;
import org.ine5426.lava.compiler.exceptions.IncompatibleTypesException;
import org.ine5426.lava.compiler.exceptions.LavaCompilerException;
import org.ine5426.lava.compiler.exceptions.RedeclaredVariableException;
import org.ine5426.lava.compiler.exceptions.ReturnStatementException;
import org.ine5426.lava.compiler.exceptions.UndeclaredVariableException;
import org.ine5426.lava.compiler.exceptions.UndefinedFunctionException;
import org.ine5426.lava.compiler.exceptions.UndefinedTypeException;
import org.ine5426.lava.compiler.symbols.ClassDef;
import org.ine5426.lava.compiler.symbols.DataType;
import org.ine5426.lava.compiler.symbols.Function;
import org.ine5426.lava.compiler.symbols.PrimitiveType;
import org.ine5426.lava.compiler.symbols.SymbolTable;
import org.ine5426.lava.compiler.symbols.Variable;
import org.ine5426.lava.generated.LavaParser.AndExprContext;
import org.ine5426.lava.generated.LavaParser.AssignmentContext;
import org.ine5426.lava.generated.LavaParser.Block_statementContext;
import org.ine5426.lava.generated.LavaParser.BoolLiteralContext;
import org.ine5426.lava.generated.LavaParser.Bool_litContext;
import org.ine5426.lava.generated.LavaParser.ClassDefinitionContext;
import org.ine5426.lava.generated.LavaParser.Class_defContext;
import org.ine5426.lava.generated.LavaParser.DeclarationContext;
import org.ine5426.lava.generated.LavaParser.DivContext;
import org.ine5426.lava.generated.LavaParser.EqualContext;
import org.ine5426.lava.generated.LavaParser.ExprFactorContext;
import org.ine5426.lava.generated.LavaParser.ExprMethodCallContext;
import org.ine5426.lava.generated.LavaParser.ExprNewContext;
import org.ine5426.lava.generated.LavaParser.ExprStringContext;
import org.ine5426.lava.generated.LavaParser.ExprVariableContext;
import org.ine5426.lava.generated.LavaParser.FactorContext;
import org.ine5426.lava.generated.LavaParser.FunctionContext;
import org.ine5426.lava.generated.LavaParser.Function_callContext;
import org.ine5426.lava.generated.LavaParser.Function_returnContext;
import org.ine5426.lava.generated.LavaParser.GreaterThanContext;
import org.ine5426.lava.generated.LavaParser.GreaterThanEqualContext;
import org.ine5426.lava.generated.LavaParser.IfElseContext;
import org.ine5426.lava.generated.LavaParser.LessThanContext;
import org.ine5426.lava.generated.LavaParser.LessThanEqualContext;
import org.ine5426.lava.generated.LavaParser.MainFunctionStatementContext;
import org.ine5426.lava.generated.LavaParser.Method_callContext;
import org.ine5426.lava.generated.LavaParser.MinusContext;
import org.ine5426.lava.generated.LavaParser.MultContext;
import org.ine5426.lava.generated.LavaParser.New_operatorContext;
import org.ine5426.lava.generated.LavaParser.NotEqualContext;
import org.ine5426.lava.generated.LavaParser.NotExprContext;
import org.ine5426.lava.generated.LavaParser.NumberContext;
import org.ine5426.lava.generated.LavaParser.OnlyIFContext;
import org.ine5426.lava.generated.LavaParser.OrExprContext;
import org.ine5426.lava.generated.LavaParser.PlusContext;
import org.ine5426.lava.generated.LavaParser.PrintlnContext;
import org.ine5426.lava.generated.LavaParser.ProgramContext;
import org.ine5426.lava.generated.LavaParser.SubexprContext;
import org.ine5426.lava.generated.LavaParser.While_stmtContext;
import org.ine5426.lava.generated.LavaParserBaseVisitor;

public class LavaVisitor extends LavaParserBaseVisitor<String> {
	private SymbolTable symbolTable;
	private Stack<DataType> typeStack = new Stack<>();
	private int branchCounter = 0;

	/**
	 * Save the reference to the current function scope, or null it we're not inside one
	 */
	private Function currentFunction;

	/**
	 * Save the reference to the current class scope
	 */
	private ClassDef currentClass;

	// ============================================================================
	// Helpers

	public LavaVisitor(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
		this.currentClass = symbolTable.mainClass;
	}

	/**
	 * Return the variable definition
	 * Throws a UndeclaredVariableException if variable was not declared
	 *
	 * @param variableToken The token of the variable
	 * @return
	 */
	protected Variable getVariable(Token variableToken) {
		Variable var = this.symbolTable.getVariable(variableToken.getText());
		if (var == null) {
			throw new UndeclaredVariableException(variableToken);
		}
		return var;
	}

	/**
	 * Define a variable and return its definition
	 * Throws a RedeclaredVariableException if variable already exists
	 *
	 * @param variableToken
	 * @return
	 */
	protected Variable defineVariable(Token variableToken, DataType type) {
		if (symbolTable.containsVariable(variableToken.getText())) {
			throw new RedeclaredVariableException(variableToken);
		}

		return symbolTable.insertVariable(variableToken.getText(), type);
	}

	@Override
	protected String aggregateResult(String aggregate, String nextResult) {
		if (aggregate == null) {
			return nextResult;
		}
		if (nextResult == null) {
			return aggregate;
		}
		return aggregate + "\n" + nextResult;
	}

	protected String stringRepeat(String repeat, int times) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < times; i++) {
			result.append(repeat);
		}
		return result.toString();
	}

	// ============================================================================
	// Program and statement visitors

	@Override
	public String visitProgram(ProgramContext ctx) {
		String mainFunctionCode = "";
		String mainClassCode = "";
		String classDefinitions = "";

		for (int i = 0; i < ctx.getChildCount(); i++) {
			ParseTree child = ctx.getChild(i);
			String instructions = visit(child);
			if (instructions == null) {
				continue;
			}
			if (child instanceof MainFunctionStatementContext) {
				mainFunctionCode += instructions;
			}
			else if (child instanceof ClassDefinitionContext) {
				classDefinitions += instructions;
			} else { // MainClassStatement
				mainClassCode += instructions;
			}
		}

		return JasminTemplate.mainClassDefinition(mainClassCode) + "\n" +
				JasminTemplate.mainFunction(mainFunctionCode) + "\n" +
				classDefinitions;
	}

	@Override
	public String visitBlock_statement(Block_statementContext ctx) {
		// Open a new scope
		symbolTable.scopePush();
		Stack<DataType> oldTypeStack = this.typeStack;
		this.typeStack = new Stack<>();

		String blockInstructions = visitChildren(ctx);

		// Restore old scope
		this.typeStack = oldTypeStack;
		symbolTable.scopePop();

		return blockInstructions;
	}

	// ============================================================================
	// Class visitors

	@Override
	public String visitClass_def(Class_defContext ctx) {
		currentClass = this.symbolTable.getClass(ctx.className.getText());
		String instructions = visit(ctx.block);
		currentClass = symbolTable.mainClass;
		return JasminTemplate.classDefinition(ctx.className.getText(), instructions == null ? "" : instructions);
	}

	@Override
	public String visitNew_operator(New_operatorContext ctx) {
		String className = ctx.className.getText();
		ClassDef klass = symbolTable.getClass(className);

		if (klass == null) {
			throw new UndefinedTypeException(ctx.className);
		}

		// TODO: arguments and constructor!

		typeStack.push(klass);
		return "	new " + klass + "\n"
				+ "	dup" + "\n"
				+ "	invokespecial " + klass + "/<init>()V\n";
	}

	@Override
	public String visitExprNew(ExprNewContext ctx) {
		return visit(ctx.op);
	}

	@Override
	public String visitMethod_call(Method_callContext ctx) {
		Variable obj = getVariable(ctx.objName);
		ClassDef objClass = symbolTable.getClass(obj.type.name());

		// TODO: objClass == null, throw NotAClass

		String functionName = ctx.funcName.getText();
		int parameterCount = ctx.arguments.expressions.size();
		Function func = objClass.getFunction(functionName, parameterCount);

		if (func == null) {
			// TODO: show className on exception
			throw new UndefinedFunctionException(ctx.funcName);
		}

		String argumentInstructions = visit(ctx.arguments);
		for (int i = parameterCount - 1; i >= 0; i--) {
			DataType argType = typeStack.pop();
			if (func.parameterTypes.get(i) != argType) {
				throw new IncompatibleArgumentException(ctx.arguments.expressions.get(i).start,
						argType, func, i);
			}
		}

		typeStack.push(func.returnType);

		return "	" + obj.type.loadInstruction() + " " + obj.index + "\n"
				+ (argumentInstructions != null ? argumentInstructions : "")
				+ "	invokevirtual " + objClass + "/" + func.name + "(" + func.parametersToString() + ")"
				+ func.returnType.toString()
				+ "\n";
	}

	@Override
	public String visitExprMethodCall(ExprMethodCallContext ctx) {
		return visit(ctx.m);
	}

	// ============================================================================
	// Function visitors

	@Override
	public String visitFunction_call(Function_callContext ctx) {
		String funcName = ctx.funcName.getText();
		int parameterCount = ctx.arguments.expressions.size();
		Function func = symbolTable.getFunction(funcName, parameterCount);

		if (func == null) {
			throw new UndefinedFunctionException(ctx.funcName);
		}

		String argumentInstructions = visit(ctx.arguments);
		for (int i = parameterCount - 1; i >= 0; i--) {
			DataType argType = typeStack.pop();
			if (func.parameterTypes.get(i) != argType) {
				throw new IncompatibleArgumentException(ctx.arguments.expressions.get(i).start,
						argType, func, i);
			}
		}

		typeStack.push(func.returnType);

		return (argumentInstructions != null ? argumentInstructions : "") +
				"	invokestatic LavaMain/" + func.name + "(" + func.parametersToString() + ")"
				+ func.returnType.toString()
				+ "\n";
	}

	@Override
	public String visitFunction_return(Function_returnContext ctx) {
		if (currentFunction == null) {
			throw new ReturnStatementException(ctx.start, "Return statements are allowed just inside function definitions");
		}

		String instructions;
		DataType returnType;

		if (ctx.returnExpr != null) {
			instructions = visit(ctx.returnExpr);
			returnType = typeStack.pop();
		}
		else {
			// 'return' with no expression (for void functions)
			instructions = "";
			returnType = PrimitiveType.VOID;
		}

		currentFunction.tmpReturnType = returnType;

		instructions += "	" + returnType.returnInstruction() + "\n";
		return instructions;
	}

	@Override
	public String visitFunction(FunctionContext ctx) {
		String functionName = ctx.funcName.getText();
		int parameterCount = ctx.params.varDeclarations.size();
		Function func = currentClass.getFunction(functionName, parameterCount);

		if (currentFunction != null)
			throw new LavaCompilerException(ctx.funcName, "Cannot declare a function inside another");
		else
			currentFunction = func;

		// If this is a member function, define the 'this' variable
		if (currentClass != symbolTable.mainClass)
			symbolTable.insertVariable("this", currentClass);

		// Visit parameters (variable declaration)
		visit(ctx.params);

		// Visit suite (block) and end method
		String blockInstructions = visit(ctx.block);

		if (blockInstructions == null)
			blockInstructions = "";

		// If no return statement was reached, but a implicit void return at end of method
		if (currentFunction.tmpReturnType == null) {
			currentFunction.tmpReturnType = PrimitiveType.VOID;
			typeStack.push(PrimitiveType.VOID);
			blockInstructions += "	" + "return" + "\n";
		}

		if (currentFunction.tmpReturnType != currentFunction.returnType) {
			throw new ReturnStatementException(ctx.start,
					"Return expression of type <" + currentFunction.tmpReturnType.name() + "> "
							+ "is not compatible with function <" + currentFunction.name + "> which is of type "
							+ "<" + currentFunction.returnType.name() + ">");
		}

		String result = "";
		if (currentClass == symbolTable.mainClass)
			result = JasminTemplate.staticMethod(func.name, func.parametersToString(), func.returnType.toString(), blockInstructions);
		else
			result = JasminTemplate.method(func.name, func.parametersToString(), func.returnType.toString(), blockInstructions);

		currentFunction = null;
		return result;
	}

	@Override
	public String visitPrintln(PrintlnContext ctx) {
		String instructions = "	getstatic java/lang/System/out Ljava/io/PrintStream;\n" +
				visit(ctx.arg);
		DataType argType = typeStack.pop();
		instructions += "	invokevirtual java/io/PrintStream/println(" + argType + ")V\n";
		return instructions;
	}

	// ============================================================================
	// Declaration and Assigment

	@Override
	public String visitDeclaration(DeclarationContext ctx) {
		DataType type = symbolTable.getType(ctx.typeName.getText());

		if (type == null) {
			throw new UndefinedTypeException(ctx.typeName.start);
		}

		defineVariable(ctx.varName, type);

		if (ctx.expression == null) { // If this is a declaration with no assigment (e.g. 'int a')
			return "";
		}

		String instructions = visit(ctx.expression);

		Variable var = getVariable(ctx.varName);
		checkVariableCompatibility(var, typeStack.pop(), ctx.varName);

		instructions += "	" + var.type.storeInstruction() + " " + var.index + "\n";
		return instructions;
	}

	@Override
	public String visitAssignment(AssignmentContext ctx) {
		String instructions = visit(ctx.expression);

		Variable var = getVariable(ctx.varName);
		checkVariableCompatibility(var, typeStack.pop(), ctx.varName);

		instructions += "	" + var.type.storeInstruction() + " " + var.index + "\n";
		return instructions;
	}

	private void checkVariableCompatibility(Variable var, DataType exprType, Token varNameToken) {
		if (var.type != exprType) {
			throw new IncompatibleTypesException(varNameToken, var, exprType);
		}
	}

	// ============================================================================
	// Expression visitors

	@Override
	public String visitNumber(NumberContext ctx) {
		typeStack.push(PrimitiveType.INT);
		return "	ldc " + ctx.num.getText() + "\n";
	}

	@Override
	public String visitFactor(FactorContext ctx) {
		typeStack.push(PrimitiveType.INT);
		String signal = ctx.signal == null ? "" : ctx.signal.getText();
		return "	ldc " + signal + ctx.num.getText() + "\n";
	}

	@Override
	public String visitExprFactor(ExprFactorContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public String visitExprString(ExprStringContext ctx) {
		typeStack.push(PrimitiveType.STRING);
		return "	ldc " + ctx.strValue.getText() + "\n";
	}

	private void checkTypeCompatibility(DataType exprType, Token varNameToken) {
		if (typeStack.isEmpty() || !typeStack.pop().equals(exprType)) {
			throw new IncompatibleExpressionArgumentException(varNameToken, exprType, 0);
		}
	}

	@Override
	public String visitPlus(PlusContext ctx) {
		String instructions = visitChildren(ctx)
				+ "	iadd" + "\n";

		checkTypeCompatibility(PrimitiveType.INT, ctx.left.start);
		checkTypeCompatibility(PrimitiveType.INT, ctx.right.start);
		typeStack.push(PrimitiveType.INT);
		return instructions;
	}

	@Override
	public String visitMinus(MinusContext ctx) {
		String instructions = visitChildren(ctx)
				+ "	isub" + "\n";

		checkTypeCompatibility(PrimitiveType.INT, ctx.left.start);
		checkTypeCompatibility(PrimitiveType.INT, ctx.right.start);
		typeStack.push(PrimitiveType.INT);
		return instructions;
	}

	@Override
	public String visitDiv(DivContext ctx) {
		String instructions = visitChildren(ctx)
				+ "	idiv" + "\n";

		checkTypeCompatibility(PrimitiveType.INT, ctx.left.start);
		checkTypeCompatibility(PrimitiveType.INT, ctx.right.start);

		typeStack.push(PrimitiveType.INT);
		return instructions;
	}

	@Override
	public String visitMult(MultContext ctx) {
		String instructions = visitChildren(ctx)
				+ "	imul" + "\n";

		checkTypeCompatibility(PrimitiveType.INT, ctx.left.start);
		checkTypeCompatibility(PrimitiveType.INT, ctx.right.start);

		typeStack.push(PrimitiveType.INT);
		return instructions;
	}

	@Override
	public String visitSubexpr(SubexprContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public String visitExprVariable(ExprVariableContext ctx) {
		Variable var = getVariable(ctx.varName);
		typeStack.push(var.type);
		return "	" + var.type.loadInstruction() + " " + var.index + "\n";
	}

	@Override
	public String visitOnlyIF(OnlyIFContext ctx) {
		String conditionInstrution = visit(ctx.condition);
		String onTrueInstrution = visit(ctx.onTrue);

		if (!(PrimitiveType.BOOLEAN == typeStack.peek() || PrimitiveType.INT == typeStack.peek())) {
			throw new IncompatibleExpressionArgumentException(ctx.condition.start, typeStack.peek(), 0);
		}

		int counter = branchCounter;
		branchCounter++;
		return conditionInstrution
				+ " ifTrue" + counter + "\n"
				+ "goto endIf" + counter + "\n"
				+ "ifTrue" + counter + ":\n"
				+ onTrueInstrution + "\n"
				+ "endIf" + counter + ":\n";
	}

	@Override
	public String visitIfElse(IfElseContext ctx) {
		String conditionInstrution = visit(ctx.condition);
		String onTrueInstrution = visit(ctx.onTrue);
		String onFalseInstrution = visit(ctx.onFalse);

		if (!(PrimitiveType.BOOLEAN == typeStack.peek() || PrimitiveType.INT == typeStack.peek())) {
			throw new IncompatibleExpressionArgumentException(ctx.condition.start, typeStack.peek(), 0);
		}

		int counter = branchCounter;
		branchCounter++;
		return conditionInstrution
				+ " ifTrue" + counter + "\n"
				+ onFalseInstrution + "\n"
				+ "goto endIf" + counter + "\n"
				+ "ifTrue" + counter + ":\n"
				+ onTrueInstrution + "\n"
				+ "endIf" + counter + ":\n";
	}

	@Override
	public String visitBool_lit(Bool_litContext ctx) {
		typeStack.push(PrimitiveType.BOOLEAN);
		if (ctx.bool == null) {
			throw new IncompatibleExpressionArgumentException(ctx.start, typeStack.peek(), 0);
		}
		int valueBool = ctx.bool.getText().equals("false") ? 1 : 0;
		return "	ldc " + valueBool + "\n";
	}

	@Override
	public String visitBoolLiteral(BoolLiteralContext ctx) {
		String instructions = visitChildren(ctx);
		return instructions + "\n"
				+ "ifeq ";
	}

	@Override
	public String visitEqual(EqualContext ctx) {
		String instructions = visitChildren(ctx);

		checkTypeCompatibility(PrimitiveType.INT, ctx.left.start);
		checkTypeCompatibility(PrimitiveType.INT, ctx.right.start);
		typeStack.push(PrimitiveType.INT);

		return instructions + "\n"
				+ "if_icmpeq ";
	}

	@Override
	public String visitNotEqual(NotEqualContext ctx) {
		String instructions = visitChildren(ctx);

		checkTypeCompatibility(PrimitiveType.INT, ctx.left.start);
		checkTypeCompatibility(PrimitiveType.INT, ctx.right.start);
		typeStack.push(PrimitiveType.INT);

		return instructions + "\n"
				+ "if_icmpne ";
	}

	@Override
	public String visitLessThan(LessThanContext ctx) {
		String instructions = visitChildren(ctx);

		checkTypeCompatibility(PrimitiveType.INT, ctx.left.start);
		checkTypeCompatibility(PrimitiveType.INT, ctx.right.start);
		typeStack.push(PrimitiveType.INT);

		return instructions + "\n"
				+ "if_icmplt ";
	}

	@Override
	public String visitLessThanEqual(LessThanEqualContext ctx) {
		String instructions = visitChildren(ctx);

		checkTypeCompatibility(PrimitiveType.INT, ctx.left.start);
		checkTypeCompatibility(PrimitiveType.INT, ctx.right.start);
		typeStack.push(PrimitiveType.INT);

		return instructions + "\n"
				+ "if_icmple ";
	}

	@Override
	public String visitGreaterThan(GreaterThanContext ctx) {
		String instructions = visitChildren(ctx);

		checkTypeCompatibility(PrimitiveType.INT, ctx.left.start);
		checkTypeCompatibility(PrimitiveType.INT, ctx.right.start);
		typeStack.push(PrimitiveType.INT);

		return instructions + "\n"
				+ "if_icmpgt ";
	}

	@Override
	public String visitGreaterThanEqual(GreaterThanEqualContext ctx) {
		String instructions = visitChildren(ctx);

		checkTypeCompatibility(PrimitiveType.INT, ctx.left.start);
		checkTypeCompatibility(PrimitiveType.INT, ctx.right.start);
		typeStack.push(PrimitiveType.INT);

		return instructions + "\n"
				+ "if_icmpge ";
	}

	@Override
	public String visitAndExpr(AndExprContext ctx) {
		int counter = branchCounter;
		branchCounter++;

		String instructions = visit(ctx.right) + "rightTrue" + counter + "\n"
				+ "goto endFalse" + counter + "\n"
				+ "rightTrue" + counter + " :\n"
				+ visit(ctx.left) + "leftTrue" + counter + "\n"
				+ "goto endFalse" + counter + "\n"

				+ "endFalse" + counter + " :\n"
				+ "    ldc 1" + "\n"
				+ "goto end" + counter + "\n"
				+ "leftTrue" + counter + " :\n"
				+ "    ldc 0" + "\n"
				+ "goto end" + counter + "\n"
				+ "end" + counter + " :\n"
				+ " ifeq ";

		return instructions;
	}

	@Override
	public String visitOrExpr(OrExprContext ctx) {
		int counter = branchCounter;
		branchCounter++;

		String instructions = visit(ctx.right) + "endTrue" + counter + "\n"
				+ "goto rightExp" + counter + "\n"
				+ "rightExp" + counter + " :\n"
				+ visit(ctx.left) + "endTrue" + counter + "\n"
				+ "goto endFalse" + counter + "\n"

				+ "endTrue" + counter + " :\n"
				+ "    ldc 0" + "\n"
				+ "goto end" + counter + "\n"
				+ "endFalse" + counter + " :\n"
				+ "    ldc 1" + "\n"
				+ "goto end" + counter + "\n"
				+ "end" + counter + " :\n"
				+ " ifeq ";

		return instructions;
	}

	@Override
	public String visitNotExpr(NotExprContext ctx) {
		String instructions = visit(ctx.condition);

		int counter = branchCounter;
		branchCounter++;

		return instructions + "ifTrue" + counter + "\n"
				+ "    ldc 0 " + "\n"
				+ "goto end" + counter + "\n"
				+ "ifTrue" + counter + " :\n"
				+ "    ldc 1" + "\n"
				+ "goto end" + counter + "\n"
				+ "end" + counter + " : \n"
				+ "ifeq ";
	}

	@Override
	public String visitWhile_stmt(While_stmtContext ctx) {
		int counter = branchCounter;
		branchCounter++;

		String conditionInstrution = "while" + counter + " :\n" + visit(ctx.condition);
		String onTrueInstrution = visit(ctx.onTrue);

		if (!(PrimitiveType.BOOLEAN == typeStack.peek() || PrimitiveType.INT == typeStack.peek())) {
			branchCounter--;
			throw new IncompatibleExpressionArgumentException(ctx.condition.start, typeStack.peek(), 0);
		}

		return conditionInstrution
				+ " whileTrue" + counter + "\n"
				+ "goto endWhile" + counter + "\n"
				+ "whileTrue" + counter + " :\n"
				+ onTrueInstrution + "\n"
				+ "goto while" + counter + "\n"
				+ "endWhile" + counter + " :\n";
	}
}
