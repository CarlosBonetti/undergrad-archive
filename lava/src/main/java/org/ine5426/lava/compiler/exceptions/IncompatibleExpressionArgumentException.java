package org.ine5426.lava.compiler.exceptions;

import org.antlr.v4.runtime.Token;
import org.ine5426.lava.compiler.symbols.DataType;

public class IncompatibleExpressionArgumentException extends LavaCompilerException {
	private static final long serialVersionUID = 804218179633066102L;
	private int paramIndex;
	private DataType argType;

	public IncompatibleExpressionArgumentException(Token token, DataType argType, int paramIndex) {
		super(token);
		this.argType = argType;
		this.paramIndex = paramIndex;
	}

	@Override
	public String getMessage() {
		return line + ":" + column + " Argument <" + tokenText + "> is not compatible with expression type";
		// TODO: improve this message
	}
}
