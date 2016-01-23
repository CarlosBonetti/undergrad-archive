package org.ine5426.lava.compiler.symbols;

public interface DataType {

	@Override
	String toString();

	String typeString();

	String storeInstruction();

	String loadInstruction();

	String returnInstruction();

	String name();
}
