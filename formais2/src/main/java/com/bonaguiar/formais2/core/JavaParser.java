package com.bonaguiar.formais2.core;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
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
	 * Retorna a sequência de ativações caso a sentença seja aceita ou lança um
	 * ParseException caso contrário
	 *
	 * @param sentenca
	 * @throws Throwable
	 */
	public String run(String sentenca) throws Throwable {
		this.compile();

		File file = new File("./");
		URL[] classUrls = { file.toURL() };
		ClassLoader cl = new URLClassLoader(classUrls);
		Class<?> parserClass = cl.loadClass("Parser");

		Method main = parserClass.getMethod("parse", String.class);
		Object parser = parserClass.newInstance();

		Object result = null;
		try {
			result = main.invoke(parser, sentenca);
		} catch (InvocationTargetException e) {
			e.getTargetException();
			return e.getTargetException().getMessage();
		}

		return (String) result;
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
}
