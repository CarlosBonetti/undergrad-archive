package org.ine5426.lava.compiler.symbols;

public enum PrimitiveType implements DataType {

	INT("I", 'i'),
	STRING("Ljava/lang/String;", 'a'),
	BOOLEAN("Z", 'i'),
	VOID("V", ' ');

	private final String jasminType;
	private final char instructionPrefix;

	private PrimitiveType(String jasminType, char instructionPrefix) {
		this.jasminType = jasminType;
		this.instructionPrefix = instructionPrefix;
	}

	@Override
	public String toString() {
		return this.jasminType;
	}

	@Override
	public String typeString() {
		return this.jasminType;
	}

	@Override
	public String storeInstruction() {
		return instructionPrefix + "store";
	}

	@Override
	public String loadInstruction() {
		return instructionPrefix + "load";
	}

	@Override
	public String returnInstruction() {
		return instructionPrefix + "return";
	}
}
