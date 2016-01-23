package org.ine5426.lava;

import jasmin.ClassFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import org.ine5426.lava.compiler.Compilation;
import org.ine5426.lava.compiler.JasminTemplate;
import org.ine5426.lava.compiler.LavaCompiler;

public class Runner {
	private static Path tempDir;

	public static void createTempDir() throws IOException {
		tempDir = Files.createTempDirectory("lavaCompilerRunner");
	}

	public static void deleteTempDir() {
		deleteRecursive(tempDir.toFile());
	}

	private static void deleteRecursive(File file) {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				deleteRecursive(child);
			}
		}
		if (!file.delete()) {
			throw new Error("Could not delete file <" + file + ">");
		}
	}

	/**
	 * Compile the code using the Lava compiler, execute the code and return the output
	 *
	 * @param code Code to be executed
	 * @return The output of the program
	 * @throws Exception
	 */
	public static String compileAndRun(String code) throws Exception {
		return run(LavaCompiler.compile(code));
	}

	/**
	 * Run a Lava Compilation
	 *
	 * @param compilation
	 * @return
	 * @throws Exception
	 */
	public static String run(Compilation compilation) throws Exception {
		for (String code : compilation.jFiles) {
			ClassFile classFile = new ClassFile();
			classFile.getClassName();
			classFile.readJasmin(new StringReader(code), "", false);
			Path outputPath = tempDir.resolve(classFile.getClassName() + ".class");
			try (OutputStream output = Files.newOutputStream(outputPath)) {
				classFile.write(output);
			}
		}

		return runJavaClass(tempDir, JasminTemplate.MAIN_CLASS_NAME);
	}

	/**
	 * Execute a '.class' file and return the output
	 *
	 * @param dir The directory which contains the class to be executed
	 * @param className The name of the class to be executed, without the '.class' extension
	 * @return
	 * @throws IOException
	 */
	public static String runJavaClass(Path dir, String className) throws IOException {
		Process process = Runtime.getRuntime().exec(new String[] {
				"java", "-cp", dir.toString(), className
		});

		try (InputStream in = process.getInputStream()) {
			Scanner scanner = new Scanner(in);
			scanner.useDelimiter("\\A");
			String result = scanner.hasNext() ? scanner.next() : "";
			scanner.close();
			return result;
		}
	}

}
