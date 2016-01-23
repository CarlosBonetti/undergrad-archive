package org.ine5426.lava.compiler.exceptions;

import org.antlr.v4.runtime.Token;

public class ReturnStatementException extends LavaCompilerException {
	private static final long serialVersionUID = 1740119349239288354L;

	public ReturnStatementException(Token token, String msg) {
		super(token, msg);
	}
}
