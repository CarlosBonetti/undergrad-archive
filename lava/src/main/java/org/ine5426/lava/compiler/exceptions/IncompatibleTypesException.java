package org.ine5426.lava.compiler.exceptions;

import org.antlr.v4.runtime.Token;
import org.ine5426.lava.compiler.symbols.DataType;
import org.ine5426.lava.compiler.symbols.Variable;

public class IncompatibleTypesException extends LavaCompilerException {
	private static final long serialVersionUID = -5854256173754857746L;
	private Variable var;
	private DataType exprType;

	public IncompatibleTypesException(Token token, Variable var, DataType exprType) {
		super(token);
		this.var = var;
		this.exprType = exprType;
	}

	@Override
	public String getMessage() {
		return line + ":" + column + " Variable <" + this.var.name + "> of type " + this.var.type.name()
				+ " is not compatible with assigned expression of type " + this.exprType.name();
	}
}
