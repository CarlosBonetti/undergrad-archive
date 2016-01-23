package org.ine5426.lava.compiler.symbols;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassDef extends Symbol implements DataType {
	protected Collection<Function> functions = new ArrayList<>();
	private Map<String, Variable> attributes = new HashMap<>();

	public ClassDef(String className) {
		super(className, SymbolType.CLASS);
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public String typeString() {
		return "L" + this.name + ";";
	}

	@Override
	public String storeInstruction() {
		return "astore";
	}

	@Override
	public String loadInstruction() {
		return "aload";
	}

	@Override
	public String returnInstruction() {
		return "areturn";
	}

	@Override
	public String name() {
		return this.name;
	}

	public boolean containsFunction(String functionName, int parameterCount) {
		for (Function function : functions) {
			if (function.name.equals(functionName) && function.parameterCount() == parameterCount) {
				return true;
			}
		}
		return false;
	}

	public Function getFunction(String functionName, int parameterCount) {
		for (Function function : functions) {
			if (function.name.equals(functionName) && function.parameterCount() == parameterCount) {
				return function;
			}
		}
		return null;
	}

	public Function insertFunction(String functionName, DataType returnType, List<DataType> parameterTypes) {
		Function f = new Function(functionName, returnType, parameterTypes);
		functions.add(f);
		return f;
	}

	public Variable defineAttribute(String name, DataType type) {
		return this.attributes.put(name, new Variable(name, type, this.attributes.size()));
	}

}
