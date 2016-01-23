package org.ine5426.lava.compiler;

public class JasminTemplate {
	public static final String MAIN_CLASS_NAME = "LavaMain";
	public static final String CLASS_SEPARATOR = "; ClassDef definition\n";

	public static String classDefinition(String className, String instructions) {
		return CLASS_SEPARATOR +
				".class public " + className + "\n" +
				".super java/lang/Object\n" +
				"\n" +
				classConstructor() +
				"\n\n" +
				instructions;
	}

	public static String mainClassDefinition(String instructions) {
		return ".class public " + MAIN_CLASS_NAME + "\n" +
				".super java/lang/Object\n" +
				"\n" +
				instructions;
	}

	public static String staticMethod(String funcName, String parameters, String returnType, String instructions) {
		return ".method static public " + funcName + "(" + parameters + ")"
				+ returnType + "\n"
				+ "	.limit locals 100\n"
				+ "	.limit stack 100\n\n"
				+ instructions
				+ ".end method\n";
	}

	public static String method(String funcName, String parameters, String returnType, String instructions) {
		return ".method public " + funcName + "(" + parameters + ")"
				+ returnType + "\n"
				+ "	.limit locals 100\n"
				+ "	.limit stack 100\n\n"
				+ instructions
				+ ".end method\n";
	}

	public static String mainFunction(String instructions) {
		return ".method public static main([Ljava/lang/String;)V\n" +
				"	.limit stack 100\n" +
				"	.limit locals 100\n\n" +
				instructions +
				"	return\n" +
				".end method\n";
	}

	public static String classConstructor() {
		return ".method public <init>()V \n" +
				"	.limit stack 1 \n" +
				"	.limit locals 1 \n" +
				"	aload_0 \n" +
				"	invokespecial java/lang/Object/<init>()V \n" +
				"	return \n" +
				".end method";
	}

}
