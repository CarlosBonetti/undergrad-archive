package org.ine5426.lava.compiler.symbols;

import java.util.List;

public class Function extends Symbol {
	public final DataType returnType;
	public final List<DataType> parameterTypes;
	public DataType tmpReturnType; // Used by the compilation tree to save the type that is being returned

	public Function(String functionName, DataType returnType, List<DataType> parameterTypes) {
		super(functionName, SymbolType.FUNCTION);
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
	}

	public int parameterCount() {
		return parameterTypes.size();
	}

	public String parametersToString() {
		String s = "";

		for (DataType type : this.parameterTypes)
			s += type.typeString();

		return s;
	}
}
