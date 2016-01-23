package org.ine5426.lava.compiler.exceptions;

import org.antlr.v4.runtime.Token;

public class UndefinedTypeException extends LavaCompilerException {
	private static final long serialVersionUID = 7152921899714220452L;

	public UndefinedTypeException(Token token) {
		super(token);
	}

	@Override
	public String getMessage() {
		return line + ":" + column + " type <" + tokenText + "> is not defined";
	}
}
