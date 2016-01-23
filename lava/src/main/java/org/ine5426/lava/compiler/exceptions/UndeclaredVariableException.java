package org.ine5426.lava.compiler.exceptions;

import org.antlr.v4.runtime.Token;

public class UndeclaredVariableException extends LavaCompilerException {
	private static final long serialVersionUID = -2988266994411203889L;

	public UndeclaredVariableException(Token varNameToken) {
		super(varNameToken);
	}

	@Override
	public String getMessage() {
		return line + ":" + column + " undeclared variable <" + tokenText + ">";
	}
}
