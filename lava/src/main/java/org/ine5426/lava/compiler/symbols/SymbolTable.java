package org.ine5426.lava.compiler.symbols;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.ine5426.lava.compiler.JasminTemplate;

public class SymbolTable {
	private Map<String, Variable> variables = new HashMap<>();
	private Stack<Map<String, Variable>> scopes = new Stack<>();
	private Map<String, ClassDef> classes = new HashMap<>();

	public final ClassDef mainClass = new ClassDef(JasminTemplate.MAIN_CLASS_NAME);

	/**
	 * Create a new scope context
	 */
	public void scopePush() {
		scopes.push(this.variables);
		variables = new HashMap<>();
	}

	/**
	 * Closes a scope context, restoring the previous one
	 */
	public void scopePop() {
		variables = scopes.pop();
	}

	// ====================================
	// Variables

	/**
	 * Insert a variable on actual scope
	 */
	public Variable insertVariable(String name, DataType type) {
		return this.variables.put(name, new Variable(name, type, this.variables.size()));
	}

	/**
	 * Get a variable, looking in all open scopes starting on the deepest closest one
	 */
	public Variable getVariable(String variableName) {
		Variable var = this.variables.get(variableName);
		// Iterator<Map<String, Variable>> it = scopes.iterator();

		// while (var == null && it.hasNext()) {
		// Map<String, Variable> vars = it.next();
		// var = vars.get(variableName);
		// }

		return var;
	}

	/**
	 * Check whether a variable already exists on actual scope
	 */
	public boolean containsVariable(String variableName) {
		return this.variables.containsKey(variableName);
	}

	// ====================================
	// Functions: delegates simple function calls to MainClass

	public boolean containsFunction(String functionName, int parameterCount) {
		return mainClass.containsFunction(functionName, parameterCount);
	}

	public Function getFunction(String functionName, int parameterCount) {
		return mainClass.getFunction(functionName, parameterCount);
	}

	public Function insertFunction(String functionName, DataType returnType, List<DataType> parameterTypes) {
		return mainClass.insertFunction(functionName, returnType, parameterTypes);
	}

	// ====================================
	// Classes

	public boolean containsClass(String className) {
		return this.classes.containsKey(className);
	}

	public ClassDef insertClass(String className) {
		ClassDef klass = new ClassDef(className);
		this.classes.put(className, klass);
		return klass;
	}

	public ClassDef getClass(String className) {
		return this.classes.get(className);
	}

	/**
	 * Return a PrimitiveType given a String
	 * Return null if typeName matches with no PrimitiveType
	 * 
	 * @param text
	 * @return
	 */
	public DataType getType(String typeName) {
		switch (typeName) {
		case "int":
			return PrimitiveType.INT;
		case "string":
			return PrimitiveType.STRING;
		case "bool":
			return PrimitiveType.BOOLEAN;
		case "void":
			return PrimitiveType.VOID;
		default:
			return this.classes.get(typeName);
		}
	}
}
