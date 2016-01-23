package org.ine5426.lava.compiler.exceptions;

import org.antlr.v4.runtime.Token;

public class LavaCompilerException extends RuntimeException {
	private static final long serialVersionUID = -2988266990411203889L;

	protected String tokenText;
	protected int line;
	protected int column;
	protected String msg;

	public LavaCompilerException(Token token) {
		this(token, "Compiler error");
	}

	public LavaCompilerException(Token token, String msg) {
		this.tokenText = token.getText();
		this.line = token.getLine();
		this.column = token.getCharPositionInLine();
		this.msg = msg;
	}

	@Override
	public String getMessage() {
		return line + ":" + column + " " + msg;
	}
}
