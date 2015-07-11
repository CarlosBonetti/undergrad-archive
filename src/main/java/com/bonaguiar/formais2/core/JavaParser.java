package com.bonaguiar.formais2.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import lombok.Getter;

/**
 * Parser Descendente Recursivo de uma gramática livre de contexto
 * Pode ser executado e visualizado
 */
public class JavaParser extends SimpleJavaFileObject {

	/**
	 * Código do Parser em String
	 */
	@Getter
	protected String code;

	public JavaParser(String code) throws URISyntaxException {
		super(new URI("Parser.java"), Kind.SOURCE);
		this.code = code;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		return this.code;
	}

	@Override
	public String toString() {
		return this.code;
	}

	/**
	 * Roda o parser com a sentença de entrada
	 *
	 * @param sentenca
	 * @throws Exception
	 */
	public ParseResult run(String sentenca) throws Exception {
		this.compile();

		Process p = Runtime.getRuntime().exec("java Parser " + sentenca);
		return new ParseResult(p);
	}

	private void compile() throws Exception {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
		Iterable<? extends JavaFileObject> fileObjects = Arrays.asList(this);

		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null,
				null, fileObjects);
		boolean success = task.call();
		fileManager.close();

		if (!success) {
			throw new Exception("Compilação falhou: " + diagnostics.getDiagnostics());
		}
	}

	public static class ParseResult {
		private Process p;
		private String output;

		public ParseResult(Process p) throws IOException, InterruptedException {
			this.p = p;
			this.output = "";

			BufferedReader is = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line;
			while ((line = is.readLine()) != null) {
				output += line;
			}

			p.waitFor();
		}

		public int exitValue() {
			return p.exitValue();
		}

		public boolean success() {
			return exitValue() == 0;
		}

		public String message() {
			return output;
		}
	}
}
