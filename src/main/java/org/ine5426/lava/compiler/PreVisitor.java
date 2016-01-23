package org.ine5426.lava.compiler;

import java.util.ArrayList;

import org.ine5426.lava.compiler.exceptions.RedeclaredClassException;
import org.ine5426.lava.compiler.exceptions.RedeclaredFunctionException;
import org.ine5426.lava.compiler.symbols.ClassDef;
import org.ine5426.lava.compiler.symbols.DataType;
import org.ine5426.lava.compiler.symbols.SymbolTable;
import org.ine5426.lava.generated.LavaParser.Class_defContext;
import org.ine5426.lava.generated.LavaParser.DeclarationContext;
import org.ine5426.lava.generated.LavaParser.FunctionContext;
import org.ine5426.lava.generated.LavaParserBaseVisitor;

public class PreVisitor extends LavaParserBaseVisitor<Void> {
	private SymbolTable symbolTable = new SymbolTable();
	private ClassDef actualClass = symbolTable.mainClass;

	public SymbolTable getSymbolTable() {
		return symbolTable;
	}

	@Override
	public Void visitClass_def(Class_defContext ctx) {
		if (symbolTable.containsClass(ctx.className.getText())) {
			throw new RedeclaredClassException(ctx.className);
		}

		actualClass = symbolTable.insertClass(ctx.className.getText());
		visit(ctx.block);
		actualClass = symbolTable.mainClass;

		return null;
	}

	@Override
	public Void visitFunction(FunctionContext ctx) {
		String functionName = ctx.funcName.getText();
		DataType returnType = symbolTable.getType(ctx.funcType.getText()); // TODO: check for 'null' type

		ArrayList<DataType> parameterTypes = new ArrayList<>();
		for (DeclarationContext parameter : ctx.params.varDeclarations) {
			parameterTypes.add(symbolTable.getType(parameter.typeName.getText())); // TODO: check for 'null' type
		}

		if (symbolTable.containsFunction(functionName, parameterTypes.size())) {
			throw new RedeclaredFunctionException(ctx.funcName);
		}

		actualClass.insertFunction(functionName, returnType, parameterTypes);
		return null;
	}

	@Override
	protected Void aggregateResult(Void aggregate, Void nextResult) {
		return super.aggregateResult(aggregate, nextResult);
	}

}
