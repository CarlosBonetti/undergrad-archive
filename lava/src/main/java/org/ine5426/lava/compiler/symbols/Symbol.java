package org.ine5426.lava.compiler.symbols;

public class Symbol {
	public final String name;
	public final SymbolType symbolType;

	public Symbol(String name, SymbolType symbolType) {
		this.name = name;
		this.symbolType = symbolType;
	}
}
