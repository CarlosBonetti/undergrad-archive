package org.ine5426.lava.compiler.exceptions;

import org.antlr.v4.runtime.Token;

public class UndefinedFunctionException extends LavaCompilerException {
	private static final long serialVersionUID = 1L;

	private final String functionName;
	public UndefinedFunctionException(Token funcNameToken) {
		super(funcNameToken);
		this.functionName = funcNameToken.getText();
	}

	@Override
	public String getMessage() {
		return line + ":" + column + " call to undefined function: <" + functionName + ">";
	}
}
