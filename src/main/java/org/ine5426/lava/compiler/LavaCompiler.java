package org.ine5426.lava.compiler;

import java.io.IOException;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;

/**
 * Compiles Lava code and return the correspondent instructions in Jasmin Java Assembler
 */
public class LavaCompiler {

	public static Compilation compileFile(String path) throws IOException {
		ANTLRInputStream input = new ANTLRFileStream(path);
		return compile(input);
	}

	/**
	 * Compile the code using the Lava compiler, returning corresponding Jasmin assembler instructions
	 *
	 * @param code The code as raw string
	 * @return Jasmin assemlber instructions
	 */
	public static Compilation compile(String code) {
		code += System.lineSeparator(); // Include a NEWLINE at <EOF>
		return compile(new ANTLRInputStream(code));
	}

	/**
	 * Compile the code using the Lava compiler, returning corresponding Jasmin assembler instructions
	 *
	 * @param input The ANTLRInputStream
	 * @return Jasmin assemlber instructions
	 */
	public static Compilation compile(ANTLRInputStream input) {
		return new Compilation(input);
	}
}
