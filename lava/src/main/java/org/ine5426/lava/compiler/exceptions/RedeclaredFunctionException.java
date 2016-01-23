package org.ine5426.lava.compiler.exceptions;

import org.antlr.v4.runtime.Token;

public class RedeclaredFunctionException extends LavaCompilerException {
	private static final long serialVersionUID = 7152921899714220452L;

	public RedeclaredFunctionException(Token token) {
		super(token);
	}
	
	@Override
	public String getMessage() {
		return line + ":" + column + " function <" + tokenText + "> already defined";
	}
}
