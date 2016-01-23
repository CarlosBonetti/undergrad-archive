package org.ine5426.lava.compiler.exceptions;

import org.antlr.v4.runtime.Token;
import org.ine5426.lava.compiler.symbols.DataType;
import org.ine5426.lava.compiler.symbols.Function;

public class IncompatibleArgumentException extends LavaCompilerException {
	private static final long serialVersionUID = 804218179633066102L;
	private Function function;
	private int paramIndex;
	private DataType argType;

	public IncompatibleArgumentException(Token token, DataType argType, Function function, int paramIndex) {
		super(token);
		this.argType = argType;
		this.function = function;
		this.paramIndex = paramIndex;
	}

	@Override
	public String getMessage() {
		return line + ":" + column + " Expression of type <" + argType.name() + ">"
				+ " is not compatible with argument " + paramIndex
				+ " of type <" + function.parameterTypes.get(paramIndex).name() + "> of function "
				+ "<" + function.name + ">";
	}
}
