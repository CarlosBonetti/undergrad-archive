package org.ine5426.lava.compiler.symbols;

public class Variable extends Symbol {
	public final DataType type;
	public final int index;

	public Variable(String name, DataType type, int index) {
		super(name, SymbolType.VARIABLE);
		this.type = type;
		this.index = index;
	}

	@Override
	public String toString() {
		return type.name() + " " + name;
	}
}
