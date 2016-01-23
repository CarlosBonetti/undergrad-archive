package org.ine5426.lava.compiler.exceptions;

import org.antlr.v4.runtime.Token;

public class RedeclaredVariableException extends LavaCompilerException {
	private static final long serialVersionUID = 5762936739673241367L;

	public RedeclaredVariableException(Token varNameToken) {
		super(varNameToken);
	}

	@Override
	public String getMessage() {
		return line + ":" + column + " variable <" + tokenText + "> already defined";
	}
}
