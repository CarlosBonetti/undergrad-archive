package com.bonaguiar.formais2.core;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Um gerador de parser para gramáticas livres de contexto
 * que usa a técnica Descendente Recursivo
 */
public class ParserGenerator {

	/**
	 * Gramática Base do Parser
	 */
	protected GLC glc;

	/**
	 * Cria um novo gerador de parser para a gramática
	 */
	public ParserGenerator(GLC glc) {
		// TODO: verificar se é LL(1)
		this.glc = glc;
	}

	public String getParser() throws IOException {
		Template parserTemplate = this.getParserTemplate();

		parserTemplate = parserTemplate.replace("$(inicial)", this.glc.getSimboloInicial());
		parserTemplate = parserTemplate.replace("$(metodos)", this.getMetodos());
		return parserTemplate.toString();
	}

	public String getMetodos() {
		return "TODO";
	}

	protected Template getParserTemplate() throws IOException {
		return new Template(Files.readAllLines(Paths.get("./src/main/templates/parser.template"), Charset.defaultCharset()));
	}

	/**
	 * Lista de linhas (String), representando um arquivo de texto com tags especiais para serem processadas
	 * em tempo de execução, como por exemplo, a tag $(inicial)
	 */
	public static class Template extends ArrayList<String> {
		private static final long serialVersionUID = -9031772507297147620L;

		public Template() {
		}

		public Template(List<String> list) {
			super(list);
		}

		/**
		 * Altera toda a sequência de caracteres encontrada pelo replacement
		 *
		 * @param target
		 * @param replacement
		 * @return
		 */
		public Template replace(String target, String replacement) {
			Template novo = new Template();
			for (String line : this) {
				novo.add(line.replace(target, replacement));
			}
			return novo;
		}

		@Override
		public String toString() {
			String result = "";
			for (String line : this) {
				result += line + "\n";
			}
			return result;
		}
	}
}
